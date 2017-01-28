package com.saladjack.im.ui.login;

/**
 * Created by saladjack on 17/1/27.
 */

public interface LoginView {
    void onSendMsgSuccess();

    void onSendMsgFail(int code);

    void onLoginSuccess(int userId,String userName);

    void onLoginFail(int errorcode);
}
