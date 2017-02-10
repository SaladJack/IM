package com.saladjack.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.saladjack.im.ui.chat.ChatActivity;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by SaladJack on 2017/2/7.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        int friendId = bundle.getInt("friendId");
        String content = bundle.getString("content");
        Intent chatIntent = new Intent(context, ChatActivity.class);
        UserBean userBean = new UserBean();
        userBean.setUserId(friendId);
        bundle.clear();
        bundle.putParcelable(ChatActivity.USER_BEAN,userBean);
        bundle.putString("content",content);
        chatIntent.putExtra("bundle",bundle);
        context.startActivity(chatIntent);
    }

}
