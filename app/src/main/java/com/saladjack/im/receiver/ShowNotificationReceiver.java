package com.saladjack.im.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.saladjack.im.R;

/**
 * Created by SaladJack on 2017/2/7.
 */

public class ShowNotificationReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        int friendId = bundle.getInt("friendId");
        String content = bundle.getString("content");

        Intent notificationIntent = new Intent(context,NotificationReceiver.class);
        notificationIntent.putExtra("bundle",bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(""+friendId)
                .setTicker(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.launcher);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());

    }
}
