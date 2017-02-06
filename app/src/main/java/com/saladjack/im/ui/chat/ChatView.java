package com.saladjack.im.ui.chat;

/**
 * Created by saladjack on 17/1/27.
 */

public interface ChatView {
    void onSendMessageSuccess();
    void onSendMessageFail(Integer code);
}
