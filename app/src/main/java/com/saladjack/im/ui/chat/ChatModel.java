package com.saladjack.im.ui.chat;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatModel implements ChatIModel {
    ChatIPresenter presenter;

    public ChatModel(ChatIPresenter presenter) {
        this.presenter = presenter;
    }


}
