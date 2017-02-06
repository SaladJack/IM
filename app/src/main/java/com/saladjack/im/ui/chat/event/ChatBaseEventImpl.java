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
import com.saladjack.im.ui.signin.SigninView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChatBaseEventImpl implements ChatBaseEvent {
	private void sendBroadCast(Bundle bundle,String action){
		Intent intent = new Intent(action);
		intent.putExtra("bundle",bundle);
		context.sendBroadcast(intent);
	}
	private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
	private final Context context;

	private ChatView chatView = null;

	private SigninView signinView = null;

	public ChatBaseEventImpl(Context context) {
		this.context = context;
	}



	@Override public void onSignInMessage(int userId, int responseCode, String username)
	{
		if (responseCode == 0) {


			Log.i(TAG, "【DEBUG_UI】登录成功，当前分配的user_id=！"+userId+ "kong:" + String.valueOf(signinView!=null));
//			if(this.chatView != null) {
//				this.chatView.showReConnectMessage("Reconnect Success");
//			}
//			if(signinView != null){
//				signinView.onSigninSuccess(userId,username);
//				signinView = null;
//			}


			Bundle bundle = new Bundle();
			bundle.putInt("userId",userId);
			bundle.putString("userName",username);
			sendBroadCast(bundle,"onSignInSuccess");
		}
		else {
			Log.e(TAG, "【DEBUG_UI】登录失败，错误代码：" + responseCode);
//			if(this.chatView != null) {
//				this.chatView.showDisconnectMessage("登录失败,code="+responseCode);
//			}
//			if(signinView != null){
//				signinView.onSigninFail(responseCode);
//			}

			Bundle bundle = new Bundle();
			bundle.putInt("erroCode",responseCode);
			sendBroadCast(bundle,"onSignInFail");
		}

	}

	@Override public void onLinkCloseMessage(int errcode) {
		Log.e(TAG, "【DEBUG_UI】网络连接出错关闭了，error：" + errcode);
		
		// TODO 以下代码仅用于DEMO哦
//		if(this.chatView != null) {
//			this.chatView.onDisconnect("服务器连接已断开,error="+errcode);
//		}
		Bundle bundle = new Bundle();
		bundle.putInt("erroCode",errcode);
		sendBroadCast(bundle,"showDisconnectMessage");
	}
	

	
	public void setChatView(ChatView chatView) {
		this.chatView = chatView;
	}
}
