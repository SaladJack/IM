package com.saladjack.im.ui.chat;

import android.content.Context;
import android.os.RemoteException;

import com.saladjack.im.app.IMApp;

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
            try {
                IMApp.getInstance().getBinder().sendMessage(message,friendId,qos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
