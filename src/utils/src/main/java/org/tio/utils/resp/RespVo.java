package org.tio.utils.resp;

/**
 * @author tanyaowu 
 * 2017年8月18日 下午3:54:27
 */
public class RespVo implements java.io.Serializable {
	private static final long serialVersionUID = 7492427869347211588L;
//	private static Logger log = LoggerFactory.getLogger(RespVo.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	private RespVo(RespResult respCode) {
		this.result = respCode;
	}

	/**
	 * 结果：成功、失败或未知
	 */
	private RespResult result;
	/**
	 * 消息，一般用于显示
	 */
	private String msg;

	/**
	 * 业务数据，譬如分页数据，用户信息数据等
	 */
	private Object data;
	/**
	 * 业务编码：一般是在失败情况下会用到这个，以便告知用户失败的原因是什么
	 */
	private Object code;

	public static RespVo ok() {
		RespVo resp = new RespVo(RespResult.ok);
		return resp;
	}
	
	public static RespVo ok(Object data) {
		return ok().data(data);
	}

	public static RespVo fail() {
		RespVo resp = new RespVo(RespResult.fail);
		return resp;
	}
	
	public static RespVo fail(String msg) {
		return fail().msg(msg);
	}

	public RespVo data(Object data) {
		this.setData(data);
		return this;
	}

	public RespVo msg(String msg) {
		this.setMsg(msg);
		return this;
	}

	public RespVo code(Object code) {
		this.setCode(code);
		return this;
	}

	public RespResult getResult() {
		return result;
	}

	public void setResult(RespResult result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getCode() {
		return code;
	}

	public void setCode(Object code) {
		this.code = code;
	}
	
	public boolean isOk() {
		return this.result == RespResult.ok;
	}
	
//	public boolean isFail() {
//		return this.result == RespResult.fail;
//	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {
		RespVo.fail().code(null).data(null).msg(null);
	}
}
