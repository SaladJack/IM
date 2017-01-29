package com.saladjack.im.ui.signin;

/**
 * Created by saladjack on 17/1/27.
 */

public interface SigninView {
    void onSendMsgSuccess();

    void onSendMsgFail(int code);

    void onSigninSuccess(int userId, String userName);

    void onsigninFail(int errorcode);
}
