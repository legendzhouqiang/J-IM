package org.tio.http.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpPacket extends Packet {

	private static Logger log = LoggerFactory.getLogger(HttpPacket.class);

	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 5.1); //只支持多少M数据

	//	public static byte encodeVersion(byte firstByte, byte version)
	//	{
	//		if (isCompress)
	//		{
	//			return (byte) (firstByte | FIRST_BYTE_HAS_SYNSEQ_COMPRESS);
	//		} else
	//		{
	//			return (byte) (firstByte & (FIRST_BYTE_HAS_SYNSEQ_COMPRESS ^ 0b01111111));
	//		}
	//	}

	protected byte[] body;
	
	/**
	 * 本次请求的所有byte，包含头和体等
	 */
	private String headerString;

	//不包含cookie的头部
	protected Map<String, String> headers = new HashMap<>();

	public HttpPacket() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	//	public final static AtomicInteger seq = new AtomicInteger();
	//
	//	private Integer seqNo = null;
	//
	//	@Override
	//	public String getSeqNo()
	//	{
	//		if (this.seqNo == null)
	//		{
	//			return null;
	//		}
	//		return String.valueOf(this.seqNo);
	//	}
	//
	//	@Override
	//	public void setSeqNo(String seqNo)
	//	{
	//		this.seqNo = seqNo;
	//	}

	//	public void setBodyLen(int bodyLen)
	//	{
	//		this.bodyLen = bodyLen;
	//	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	
	

	
	
	
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void removeHeader(String key, String value) {
		headers.remove(key);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/** 
	 * @see org.tio.core.intf.Packet#logstr()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月22日 下午3:15:18
	 * 
	 */
	@Override
	public String logstr() {

		return null;

	}

	public String getHeaderString() {
		return headerString;
	}

	public void setHeaderString(String headerString) {
		this.headerString = headerString;
	}

	//	/**
	//	 * @return the isCompress
	//	 */
	//	public boolean isCompress()
	//	{
	//		return isCompress;
	//	}
	//
	//	/**
	//	 * @param isCompress the isCompress to set
	//	 */
	//	public void setCompress(boolean isCompress)
	//	{
	//		this.isCompress = isCompress;
	//	}

	//	/**
	//	 * @return the hasSynSeq
	//	 */
	//	public boolean isHasSynSeq()
	//	{
	//		return hasSynSeq;
	//	}
	//
	//	/**
	//	 * @param hasSynSeq the hasSynSeq to set
	//	 */
	//	public void setHasSynSeq(boolean hasSynSeq)
	//	{
	//		this.hasSynSeq = hasSynSeq;
	//	}

	//	/**
	//	 * @return the is4byteLength
	//	 */
	//	public boolean isIs4ByteLength()
	//	{
	//		return is4ByteLength;
	//	}
	//
	//	/**
	//	 * @param is4ByteLength the is4byteLength to set
	//	 */
	//	public void setIs4byteLength(boolean is4ByteLength)
	//	{
	//		this.is4ByteLength = is4ByteLength;
	//	}

}
