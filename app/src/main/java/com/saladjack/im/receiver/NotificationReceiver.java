package com.saladjack.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.saladjack.im.ui.chat.ChatActivity;

/**
 * Created by SaladJack on 2017/2/7.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getStringExtra("shit"));
        Bundle bundle = intent.getBundleExtra("bundle");
        int friendId = bundle.getInt("friendId");
        String content = bundle.getString("content");
        Intent chatIntent = new Intent(context, ChatActivity.class);
        context.startActivity(chatIntent);
    }

}
