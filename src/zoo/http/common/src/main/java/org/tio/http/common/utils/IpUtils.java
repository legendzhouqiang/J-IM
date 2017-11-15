package org.tio.http.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;

/**
 *
 * @author tanyaowu
 * 2017年8月10日 下午5:05:49
 */
public class IpUtils {

	/**
	 * 获取本机 ip
	 * @return 本机IP
	 */
	public static String getLocalIp() throws SocketException {
		String localip = null; // 本地IP，如果没有配置外网IP则返回
		String netip = null; // 外网IP

		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		boolean finded = false; // 是否找到外网IP
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public static String getRealIp(HttpRequest request) {
		HttpConfig httpConfig = request.getHttpConfig();
		if(httpConfig.isProxied()) {
			String ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("proxy-client-ip");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("wl-proxy-client-ip");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemote().getIp();
			}
			return ip;
		} else {
			return request.getRemote().getIp();
		}
		
		
	}
}
