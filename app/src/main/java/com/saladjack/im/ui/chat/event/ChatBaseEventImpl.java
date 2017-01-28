/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatBaseEventImpl.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.ui.chat.event;


import com.saladjack.im.event.ChatBaseEvent;
import com.saladjack.im.ui.chat.ChatView;
import com.saladjack.im.ui.login.LoginView;

import android.util.Log;

public class ChatBaseEventImpl implements ChatBaseEvent {
private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
	
	private ChatView chatView = null;

	private LoginView loginView = null;


	@Override public void onLoginMessage(int userId, int responseCode,String username)
	{
		if (responseCode == 0) {
//			Log.i(TAG, "【DEBUG_UI】登录成功，当前分配的user_id=！"+userId);
			if(this.chatView != null) {
				this.chatView.showIMInfo_green("登录成功,id="+userId);
			}
			if(loginView != null){
				loginView.onLoginSuccess(userId,username);
				loginView = null;
			}
		}
		else {
			Log.e(TAG, "【DEBUG_UI】登录失败，错误代码：" + responseCode);
			if(this.chatView != null) {
				this.chatView.onDisconnect("登录失败,code="+responseCode);
			}
			if(loginView != null){
				loginView.onLoginFail(responseCode);
			}
		}




	}

	@Override public void onLinkCloseMessage(int errcode) {
		Log.e(TAG, "【DEBUG_UI】网络连接出错关闭了，error：" + errcode);
		
		// TODO 以下代码仅用于DEMO哦
		if(this.chatView != null) {
			this.chatView.onDisconnect("服务器连接已断开,error="+errcode);
		}
	}
	
	public void setLoginView(LoginView loginView) {
		this.loginView = loginView;
	}
	
	public void setChatView(ChatView chatView) {
		this.chatView = chatView;
	}
}
