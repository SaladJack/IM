package com.saladjack.im.protocal.s;

/**
 * 登陆结果响应信息DTO类。
 *
 * Created by saladjack on 17/2/16.
 */
public class PLoginInfoResponse {
	/** 错误码：0表示认证成功，否则是用户自定的错误码（该码应该是>1024的整数） */
	private int code = 0;
	/** 用户登陆路成功后分配给客户的唯一id：此值只在code==0时才有意义 */
	private int user_id = -1;

	public PLoginInfoResponse(int code, int user_id) {
		this.code = code;
		this.user_id = user_id;
	}

	public int getCode()
	{
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getUser_id()
	{
		return this.user_id;
	}

	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
	}
}