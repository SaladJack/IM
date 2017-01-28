package com.saladjack.im.ui.chat;

/**
 * Created by saladjack on 17/1/27.
 */

public interface ChatView {

    void showIMInfo_black(String txt);

    void showIMInfo_blue(String txt);

    void showIMInfo_brightred(String txt);

    void onDisconnect(String txt);

    void showIMInfo_green(String txt);

    
}
