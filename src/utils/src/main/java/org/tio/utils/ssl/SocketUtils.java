package org.tio.utils.ssl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2017年11月5日 上午10:18:53
 */
public class SocketUtils {
	private static Logger log = LoggerFactory.getLogger(SocketUtils.class);

	/**
	 * 
	 * @author tanyaowu
	 */
	public SocketUtils() {
	}
	
	 public static void close(Socket s){
	        try {
	            s.shutdownInput();
	            s.shutdownOutput();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }



	    public static byte[] readBytes(DataInputStream in,int length) throws IOException {
	        int r=0;
	        byte[] data=new byte[length];
	        while(r<length){
	            r+=in.read(data,r,length-r);
	        }

	        return data;
	    }

	    public static void writeBytes(DataOutputStream out,byte[] bytes,int length) throws IOException{
	        out.writeInt(length);
	        out.write(bytes,0,length);
	        out.flush();
	    }
}
