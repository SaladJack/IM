package com.saladjack.im.protocal.c;

/**
 * 登陆信息DTO类.
 *
 * Created by saladjack on 17/2/20.
 */
public class PLoginInfo
{
	private String loginName = null;
	private String loginPsw = null;
	private String extra = null;

	public PLoginInfo(String loginName, String loginPsw) {
		this(loginName, loginPsw, null);
	}


	public PLoginInfo(String loginName, String loginPsw, String extra) {
		this.loginName = loginName;
		this.loginPsw = loginPsw;
		this.extra = extra;
	}

	public String getLoginName()
	{
		return loginName;
	}

	public void setLoginName(String loginName)
	{
		this.loginName = loginName;
	}


	public String getLoginPsw()
	{
		return loginPsw;
	}

	public void setLoginPsw(String loginPsw)
	{
		this.loginPsw = loginPsw;
	}

	public String getExtra()
	{
		return extra;
	}

	public void setExtra(String extra)
	{
		this.extra = extra;
	}
}
