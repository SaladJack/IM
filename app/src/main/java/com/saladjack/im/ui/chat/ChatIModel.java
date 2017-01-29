package com.saladjack.im.ui.chat;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */
public interface ChatIModel {

    void sendMessage(Context context, String message, int userId, boolean qos);
}
