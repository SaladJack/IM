package com.saladjack.im.ui.chat.event;


import com.saladjack.im.event.ChatBaseEvent;
import com.saladjack.im.service.IMService;
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


	public ChatBaseEventImpl(Context context) {
		this.context = context;
	}



	@Override public void onSignInMessage(int userId, int responseCode, String username) {
		if (responseCode == 0) {

			((IMService)context).setUserId(userId);
			Log.i(TAG, "【DEBUG_UI】登录成功，当前分配的user_id=！"+userId);
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

//		if(this.chatView != null) {
//			this.chatView.onDisconnect("服务器连接已断开,error="+errcode);
//		}
		Bundle bundle = new Bundle();
		bundle.putInt("erroCode",errcode);
		sendBroadCast(bundle,"showDisconnectMessage");
	}
	

	

}
