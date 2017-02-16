package com.saladjack.im.protocal.s;
/**
 * 错误信息DTO类。
 *
 * Created by saladjack on 17/2/20.
 */
public class PErrorResponse {
	private int errorCode = -1;
	private String errorMsg = null;

	public PErrorResponse(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getErrorCode()
	{
		return this.errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getErrorMsg()
	{
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}
}