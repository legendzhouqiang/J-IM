package org.tio.websocket.common;

import java.util.List;

import org.tio.http.common.HttpSessionContext;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.monitor.RateLimiterWrap;

/**
 * 
 * @author tanyaowu 
 *
 */
public class WsSessionContext extends HttpSessionContext
{
	/**
	 * 消息请求频率控制器
	 */
	private RateLimiterWrap requestRateLimiter = null;
	
	/**
	 * 是否已经握过手
	 */
	private boolean isHandshaked = false;

	/**
	 * websocket 握手请求包
	 */
	private HttpRequestPacket handshakeRequestPacket = null;
	
	/**
	 * websocket 握手响应包
	 */
	private HttpResponsePacket handshakeResponsePacket = null;

	private String token = null;
	
	
	//websocket 协议用到的，有时候数据包是分几个到的，注意那个fin字段，本im暂时不支持
	private List<byte[]> lastParts = null;
	
	
	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public WsSessionContext()
	{
		
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public static void main(String[] args)
	{
		
	}


	/**
	 * @return the isHandshaked
	 */
	public boolean isHandshaked()
	{
		return isHandshaked;
	}

	/**
	 * @param isHandshaked the isHandshaked to set
	 */
	public void setHandshaked(boolean isHandshaked)
	{
		this.isHandshaked = isHandshaked;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/**
	 * @return the lastPart
	 */
	public List<byte[]> getLastParts() {
		return lastParts;
	}

	/**
	 * @param lastParts the lastPart to set
	 */
	public void setLastParts(List<byte[]> lastParts) {
		this.lastParts = lastParts;
	}

	/**
	 * @return the httpHandshakePacket
	 */
	public HttpRequestPacket getHandshakeRequestPacket() {
		return handshakeRequestPacket;
	}

	/**
	 * @param httpHandshakePacket the httpHandshakePacket to set
	 */
	public void setHandshakeRequestPacket(HttpRequestPacket handshakeRequestPacket) {
		this.handshakeRequestPacket = handshakeRequestPacket;
	}

	/**
	 * @return the requestRateLimiter
	 */
	public RateLimiterWrap getRequestRateLimiter() {
		return requestRateLimiter;
	}

	/**
	 * @param requestRateLimiter the requestRateLimiter to set
	 */
	public void setRequestRateLimiter(RateLimiterWrap requestRateLimiter) {
		this.requestRateLimiter = requestRateLimiter;
	}

	/**
	 * @return the handshakeResponsePacket
	 */
	public HttpResponsePacket getHandshakeResponsePacket() {
		return handshakeResponsePacket;
	}

	/**
	 * @param handshakeResponsePacket the handshakeResponsePacket to set
	 */
	public void setHandshakeResponsePacket(HttpResponsePacket handshakeResponsePacket) {
		this.handshakeResponsePacket = handshakeResponsePacket;
	}


	
}
