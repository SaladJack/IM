package com.saladjack.im.ui.chat;

/**
 * Created by saladjack on 17/1/27.
 */

public interface ChatView {
    void showSendMessage(String txt);

    void showResponseMessage(String txt);

    void showIMInfo_blue(String txt);

    void showIMInfo_brightred(String txt);

    void onDisconnect(String txt);

    void showIMInfo_green(String txt);

    void onSendMessageSuccess();

    void onSendMessageFail(Integer code);

    
}
