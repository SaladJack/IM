package com.saladjack.im.ui.chat;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */
public interface ChatIPresenter {
    void sendMessage(Context context, String message, int userId, boolean qos);

    void onSendMessageSuccess();

    void onSendMessageFail(Integer code);
}
