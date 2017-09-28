package org.tio.core;

import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ReconnConf;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.ChannelTraceHandler;
import org.tio.core.intf.GroupListener;
import org.tio.core.intf.TioUuid;
import org.tio.core.maintain.ChannelContextMapWithLock;
import org.tio.core.maintain.ChannelContextSetWithLock;
import org.tio.core.maintain.ClientNodeMap;
import org.tio.core.maintain.Groups;
import org.tio.core.maintain.Ids;
import org.tio.core.maintain.IpBlacklist;
import org.tio.core.maintain.IpStats;
import org.tio.core.maintain.Ips;
import org.tio.core.maintain.Users;
import org.tio.core.stat.GroupStat;
import org.tio.utils.prop.MapWithLockPropSupport;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

public abstract class GroupContext extends MapWithLockPropSupport {
	static Logger log = LoggerFactory.getLogger(GroupContext.class);

	private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

	//	public static final int CORE_POOL_SIZE = _CORE_POOL_SIZE;// < 160 ? 160 : _CORE_POOL_SIZE;

	private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 4 < 256 ? 256 : CORE_POOL_SIZE * 4;

	//	public static final Semaphore SYN_SEND_SEMAPHORE = new Semaphore(CORE_POOL_SIZE);

	//	/**
	//	 * 默认的心跳超时时间(单位: 毫秒)
	//	 */
	//	private static final long DEFAULT_HEARTBEAT_TIMEOUT = 1000 * 120;

	/**
	 * 默认的接收数据的buffer size
	 */
	public static final int READ_BUFFER_SIZE = Integer.getInteger("tio.default.read.buffer.size", 2048);

	public static final long KEEP_ALIVE_TIME = 90L;

	private final static AtomicInteger ID_ATOMIC = new AtomicInteger();

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	private boolean isShortConnection = false;

	/**
	 * 心跳超时时间(单位: 毫秒)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
	 */
	protected long heartbeatTimeout = 1000 * 120;

	private PacketHandlerMode packetHandlerMode = PacketHandlerMode.SINGLE_THREAD;//.queue;

	/**
	 * 接收数据的buffer size
	 */
	protected int readBufferSize = READ_BUFFER_SIZE;

	protected ReconnConf reconnConf;//重连配置

	private ChannelTraceHandler clientTraceHandler = new DefaultChannelTraceHandler();

	private GroupListener groupListener = null;

	private TioUuid tioUuid = new DefaultTioUuid();

	/** The group executor. */
	protected SynThreadPoolExecutor tioExecutor = null;

	protected ThreadPoolExecutor groupExecutor = null;
	public final ClientNodeMap clientNodeMap = new ClientNodeMap();
	public final ChannelContextSetWithLock connections = new ChannelContextSetWithLock();
	public final ChannelContextSetWithLock connecteds = new ChannelContextSetWithLock();

	public final ChannelContextSetWithLock closeds = new ChannelContextSetWithLock();
	public final Groups groups = new Groups();
	public final Users users = new Users();
	public final Ids ids = new Ids();
	public final Ips ips = new Ips();
	public IpStats ipStats = null;
	
	

	/**
	 * ip黑名单
	 */
	public IpBlacklist ipBlacklist = null;//new IpBlacklist();

	public final ChannelContextMapWithLock waitingResps = new ChannelContextMapWithLock();

	/**
	 * packet编码成bytebuffer时，是否与ChannelContext相关，false: packet编码与ChannelContext无关
	 */
	private boolean isEncodeCareWithChannelContext = true;

	protected String id;
	
	/**
	 * 解码异常多少次就把ip拉黑
	 */
	protected int maxDecodeErrorCountForIp = 10;

	protected String name;

	private boolean isStopped = false;

	

	public GroupContext() {
		this(null, null);
	}

	public GroupContext(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		super();
		this.id = ID_ATOMIC.incrementAndGet() + "";
		this.ipBlacklist = new IpBlacklist(id);
		this.ipStats = new IpStats(this, null, null);
		this.tioExecutor = tioExecutor;
		if (this.tioExecutor == null) {
			LinkedBlockingQueue<Runnable> tioQueue = new LinkedBlockingQueue<>();
			String tioThreadName = "tio";
			this.tioExecutor = new SynThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, KEEP_ALIVE_TIME, tioQueue,
					DefaultThreadFactory.getInstance(tioThreadName, Thread.NORM_PRIORITY), tioThreadName);
			this.tioExecutor.prestartAllCoreThreads();
		}

		this.groupExecutor = groupExecutor;
		if (this.groupExecutor == null) {
			LinkedBlockingQueue<Runnable> groupQueue = new LinkedBlockingQueue<>();
			String groupThreadName = "tio-group";
			this.groupExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, groupQueue,
					DefaultThreadFactory.getInstance(groupThreadName, Thread.NORM_PRIORITY));
			this.groupExecutor.prestartAllCoreThreads();
		}
	}

	/**
	 * @return
	 *
	 * @author tanyaowu
	 * 2016年12月20日 上午11:32:02
	 *
	 */
	public abstract AioHandler getAioHandler();

	/**
	 * @return
	 *
	 * @author tanyaowu
	 * 2016年12月20日 上午11:33:28
	 *
	 */
	public abstract AioListener getAioListener();

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * @return the clientTraceHandler
	 */
	public ChannelTraceHandler getClientTraceHandler() {
		return clientTraceHandler;
	}

	/**
	 * @return the groupExecutor
	 */
	public ThreadPoolExecutor getGroupExecutor() {
		return groupExecutor;
	}

	/**
	 * @return the groupListener
	 */
	public GroupListener getGroupListener() {
		return groupListener;
	}

	/**
	 * @return
	 *
	 * @author tanyaowu
	 * 2016年12月20日 上午11:33:02
	 *
	 */
	public abstract GroupStat getGroupStat();

	/**
	 * @return the heartbeatTimeout
	 */
	public long getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the packetHandlerMode
	 */
	public PacketHandlerMode getPacketHandlerMode() {
		return packetHandlerMode;
	}

	/**
	 * @return the readBufferSize
	 */
	public int getReadBufferSize() {
		return readBufferSize;
	}

	/**
	 * @return the reconnConf
	 */
	public ReconnConf getReconnConf() {
		return reconnConf;
	}

	/**
	 * @return the groupExecutor
	 */
	public SynThreadPoolExecutor getTioExecutor() {
		return tioExecutor;
	}

	/**
	 * @return the tioUuid
	 */
	public TioUuid getTioUuid() {
		return tioUuid;
	}

	/**
	 * @return the syns
	 */
	public ChannelContextMapWithLock getWaitingResps() {
		return waitingResps;
	}

	/**
	 * @return the isEncodeCareWithChannelContext
	 */
	public boolean isEncodeCareWithChannelContext() {
		return isEncodeCareWithChannelContext;
	}

	/**
	 * @return the isShortConnection
	 */
	public boolean isShortConnection() {
		return isShortConnection;
	}

	/**
	 * @return the isStop
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 *
	 * @param byteOrder
	 * @author tanyaowu
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @param clientTraceHandler the clientTraceHandler to set
	 */
	public void setClientTraceHandler(ChannelTraceHandler clientTraceHandler) {
		this.clientTraceHandler = clientTraceHandler;
	}

	/**
	 * @param isEncodeCareWithChannelContext the isEncodeCareWithChannelContext to set
	 */
	public void setEncodeCareWithChannelContext(boolean isEncodeCareWithChannelContext) {
		this.isEncodeCareWithChannelContext = isEncodeCareWithChannelContext;
	}

	/**
	 * @param groupListener the groupListener to set
	 */
	public void setGroupListener(GroupListener groupListener) {
		this.groupListener = groupListener;
	}

	/**
	 * @param heartbeatTimeout the heartbeatTimeout to set
	 */
	public void setHeartbeatTimeout(long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param packetHandlerMode the packetHandlerMode to set
	 */
	public void setPacketHandlerMode(PacketHandlerMode packetHandlerMode) {
		this.packetHandlerMode = packetHandlerMode;
	}

	/**
	 * @param readBufferSize the readBufferSize to set
	 */
	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	/**
	 * @param isShortConnection the isShortConnection to set
	 */
	public void setShortConnection(boolean isShortConnection) {
		this.isShortConnection = isShortConnection;
	}

	/**
	 * @param isStop the isStop to set
	 */
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	/**
	 * @param tioUuid the tioUuid to set
	 */
	public void setTioUuid(TioUuid tioUuid) {
		this.tioUuid = tioUuid;
	}
}
