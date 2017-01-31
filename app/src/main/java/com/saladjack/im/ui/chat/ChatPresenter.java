package com.saladjack.im.ui.chat;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatPresenter implements ChatIPresenter {
    private final ChatView view;
    private final ChatIModel chatModel;

    public ChatPresenter(ChatView view) {
        this.view = view;
        chatModel = new ChatModel(this);
    }
    @Override public void sendMessage(Context context, String message, int userId, boolean qos) {
        chatModel.sendMessage(context,message,userId,qos);
    }

    @Override public void onSendMessageSuccess() {
        view.onSendMessageSuccess();
    }

    @Override public void onSendMessageFail(Integer code) {
        view.onSendMessageFail(code);
    }
}
