package org.tio.core.cluster;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.utils.json.Json;

/**
 * 
 * @author tanyaowu 
 * 2017年10月10日 下午1:09:16
 */
public class TioClusterConfig {
	private static Logger log = LoggerFactory.getLogger(TioClusterConfig.class);

	public static final String TIO_CLUSTER_TOPIC = "TIOCORE_CLUSTER";

	private String topicSuffix;

	private String topic;

	private RedissonClient redisson;

	public RTopic<TioClusterVo> rtopic;

	/**
	 * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
	 */
	private boolean cluster4group = false;
	/**
	 * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4user = true;
	/**
	 * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4ip = true;
	/**
	 * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
	 */
	private boolean cluster4channelId = true;
	/**
	 * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4all = true;

	private GroupContext groupContext = null;
	
	/**
	 * 收到了多少次topic
	 */
	public static final AtomicLong RECEIVED_TOPIC_COUNT = new AtomicLong();

	/**
	 * tio内置的集群是用redis的topic来实现的，所以不同groupContext就要有一个不同的topicSuffix
	 * @param topicSuffix 不同类型的groupContext就要有一个不同的topicSuffix
	 * @param redisson
	 * @param groupContext
	 * @return
	 * @author: tanyaowu
	 */
	public static TioClusterConfig newInstance(String topicSuffix, RedissonClient redisson, GroupContext groupContext) {
		if (redisson == null) {
			throw new RuntimeException(RedissonClient.class.getSimpleName() + "不允许为空");
		}
		if (groupContext == null) {
			throw new RuntimeException("GroupContext不允许为空");
		}

		TioClusterConfig me = new TioClusterConfig(topicSuffix, redisson, groupContext);
		me.rtopic = redisson.getTopic(me.topic);
		me.rtopic.addListener(new MessageListener<TioClusterVo>() {
			@Override
			public void onMessage(String channel, TioClusterVo tioClusterVo) {
				log.info("收到topic:{}, count:{}, tioClusterVo:{}", channel, RECEIVED_TOPIC_COUNT.incrementAndGet(), Json.toJson(tioClusterVo));
				String clientid = tioClusterVo.getClientId();
				if (StringUtils.isBlank(clientid)) {
					log.error("clientid is null");
					return;
				}
				if (Objects.equals(TioClusterVo.CLIENTID, clientid)) {
					log.info("自己发布的消息，忽略掉,{}", clientid);
					return;
				}

				Packet packet = tioClusterVo.getPacket();
				if (packet == null) {
					log.error("packet is null");
					return;
				}
				packet.setFromCluster(true);
				
				//发送给所有
				boolean isToAll = tioClusterVo.isToAll();
				if (isToAll) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToAll(groupContext, packet);
					//								}
					//return;
				}

				//发送给指定组
				String group = tioClusterVo.getGroup();
				if (StringUtils.isNotBlank(group)) {
					Aio.sendToGroup(me.groupContext, group, packet);
					//return;
				}

				//发送给指定用户
				String userid = tioClusterVo.getUserid();
				if (StringUtils.isNotBlank(userid)) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToUser(me.groupContext, userid, packet);
					//								}
					//return;
				}
				
				//发送给指定token
				String token = tioClusterVo.getToken();
				if (StringUtils.isNotBlank(token)) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToToken(me.groupContext, token, packet);
					//								}
					//return;
				}

				//发送给指定ip
				String ip = tioClusterVo.getIp();
				if (StringUtils.isNotBlank(ip)) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToIp(me.groupContext, ip, packet);
					//								}
					//return;
				}
			}
		});
		return me;
	}
	
	public void publishAsyn(TioClusterVo tioClusterVo) {
		rtopic.publishAsync(tioClusterVo);
	}
	
	public void publish(TioClusterVo tioClusterVo) {
		rtopic.publish(tioClusterVo);
	}

	private TioClusterConfig(String topicSuffix, RedissonClient redisson, GroupContext groupContext) {
		//		this();
		this.setTopicSuffix(topicSuffix);
		this.setRedisson(redisson);
		this.groupContext = groupContext;
	}

	//	public TioClusterConfig() {
	//	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}

	public boolean isCluster4group() {
		return cluster4group;
	}

	public void setCluster4group(boolean cluster4group) {
		this.cluster4group = cluster4group;
	}

	public boolean isCluster4user() {
		return cluster4user;
	}

	public void setCluster4user(boolean cluster4user) {
		this.cluster4user = cluster4user;
	}

	public boolean isCluster4ip() {
		return cluster4ip;
	}

	public void setCluster4ip(boolean cluster4ip) {
		this.cluster4ip = cluster4ip;
	}

	public boolean isCluster4all() {
		return cluster4all;
	}

	public void setCluster4all(boolean cluster4all) {
		this.cluster4all = cluster4all;
	}

	public String getTopicSuffix() {
		return topicSuffix;
	}

	public void setTopicSuffix(String topicSuffix) {
		this.topicSuffix = topicSuffix;
		this.topic = topicSuffix + TIO_CLUSTER_TOPIC;

	}

	public String getTopic() {
		return topic;
	}

	public boolean isCluster4channelId() {
		return cluster4channelId;
	}

	public void setCluster4channelId(boolean cluster4channelId) {
		this.cluster4channelId = cluster4channelId;
	}
}
