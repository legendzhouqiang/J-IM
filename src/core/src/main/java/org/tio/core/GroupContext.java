package org.tio.core;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ReconnConf;
import org.tio.core.cluster.TioClusterConfig;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.ChannelTraceHandler;
import org.tio.core.intf.GroupListener;
import org.tio.core.intf.Packet;
import org.tio.core.intf.TioUuid;
import org.tio.core.maintain.ClientNodeMap;
import org.tio.core.maintain.Groups;
import org.tio.core.maintain.Ids;
import org.tio.core.maintain.IpBlacklist;
import org.tio.core.maintain.IpStats;
import org.tio.core.maintain.Ips;
import org.tio.core.maintain.Tokens;
import org.tio.core.maintain.Users;
import org.tio.core.ssl.SslConfig;
import org.tio.core.stat.DefaultIpStatListener;
import org.tio.core.stat.GroupStat;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.Threads;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.prop.MapWithLockPropSupport;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * 
 * @author tanyaowu 
 * 2016年10月10日 下午5:25:43
 */
public abstract class GroupContext extends MapWithLockPropSupport {
	static Logger log = LoggerFactory.getLogger(GroupContext.class);
	/**
	 * 默认的接收数据的buffer size
	 */
	public static final int READ_BUFFER_SIZE = Integer.getInteger("tio.default.read.buffer.size", 2048);

	private final static AtomicInteger ID_ATOMIC = new AtomicInteger();

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	private boolean isShortConnection = false;
	
	private SslConfig sslConfig = null;

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
	public final SetWithLock<ChannelContext> connections = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
	public final SetWithLock<ChannelContext> connecteds = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());

	public final SetWithLock<ChannelContext> closeds = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
	public final Groups groups = new Groups();
	public final Users users = new Users();
	public final Tokens tokens = new Tokens();
	public final Ids ids = new Ids();
	public final Ips ips = new Ips();
	public IpStats ipStats = null;

	/**
	 * ip黑名单
	 */
	public IpBlacklist ipBlacklist = null;//new IpBlacklist();

	public final MapWithLock<Integer, Packet> waitingResps = new MapWithLock<Integer, Packet>(new HashMap<Integer, Packet>());

	/**
	 * packet编码成bytebuffer时，是否与ChannelContext相关，false: packet编码与ChannelContext无关
	 */
//	private boolean isEncodeCareWithChannelContext = true;

	protected String id;

	/**
	 * 解码异常多少次就把ip拉黑
	 */
	protected int maxDecodeErrorCountForIp = 10;

	protected String name = "未命名GroupContext";
	
	private IpStatListener ipStatListener = DefaultIpStatListener.me;

	private boolean isStopped = false;

	/**
	 * 如果此值不为null，就表示要集群
	 */
	private TioClusterConfig tioClusterConfig = null;

	public GroupContext() {
		this(null, null);
	}

	/**
	 * 
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	public GroupContext(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this(null, tioExecutor, groupExecutor);
	}

	/**
	 * 
	 * @param tioClusterConfig
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	public GroupContext(TioClusterConfig tioClusterConfig, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		super();
		this.id = ID_ATOMIC.incrementAndGet() + "";
		this.ipBlacklist = new IpBlacklist(id, this);
		this.ipStats = new IpStats(this, null);
		this.tioClusterConfig = tioClusterConfig;
		this.tioExecutor = tioExecutor;
		if (this.tioExecutor == null) {
			this.tioExecutor = Threads.tioExecutor;
		}

		this.groupExecutor = groupExecutor;
		if (this.groupExecutor == null) {
			this.groupExecutor = Threads.groupExecutor;
		}
	}

	/**
	 * 获取AioHandler对象
	 * @return
	 * @author: tanyaowu
	 */
	public abstract AioHandler getAioHandler();

	/**
	 * 获取AioListener对象
	 * @return
	 * @author: tanyaowu
	 */
	public abstract AioListener getAioListener();
	
	/**
	 * 是否是集群
	 * @return true: 是集群
	 * @author: tanyaowu
	 */
	public boolean isCluster() {
		return tioClusterConfig != null;
	}

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
	 * 获取GroupStat对象
	 * @return
	 * @author: tanyaowu
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
	public MapWithLock<Integer, Packet> getWaitingResps() {
		return waitingResps;
	}

	/**
	 * @return the isEncodeCareWithChannelContext
	 */
//	public boolean isEncodeCareWithChannelContext() {
//		return isEncodeCareWithChannelContext;
//	}

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
//	public void setEncodeCareWithChannelContext(boolean isEncodeCareWithChannelContext) {
//		this.isEncodeCareWithChannelContext = isEncodeCareWithChannelContext;
//	}

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

	public TioClusterConfig getTioClusterConfig() {
		return tioClusterConfig;
	}

	public void setTioClusterConfig(TioClusterConfig tioClusterConfig) {
		this.tioClusterConfig = tioClusterConfig;
	}

	public SslConfig getSslConfig() {
		return sslConfig;
	}

	public void setSslConfig(SslConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public IpStatListener getIpStatListener() {
		return ipStatListener;
	}

	public void setIpStatListener(IpStatListener ipStatListener) {
		this.ipStatListener = ipStatListener;
//		this.ipStats.setIpStatListener(ipStatListener);
	}
}
