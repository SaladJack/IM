package com.saladjack.im.ui.chat;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.saladjack.im.core.LocalUDPDataSender;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatModel implements ChatIModel {
    ChatIPresenter presenter;

    public ChatModel(ChatIPresenter presenter) {
        this.presenter = presenter;
    }


    @Override public void sendMessage(Context context, String message, int friendId, boolean qos) {
        if(message.length() > 0) {
            new LocalUDPDataSender.SendCommonDataAsync(context, message, friendId, true) {
                @Override
                protected void onPostExecute(Integer code) {
                    if(code == 0) presenter.onSendMessageSuccess();
                    else presenter.onSendMessageFail(code);
                }
            }.execute();
        }
    }
}
