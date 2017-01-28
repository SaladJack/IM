package com.saladjack.im.ui.login;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */

public class LoginPresenter implements LoginIPresenter {
    private final LoginView view;
    private LoginIModel model;
    public LoginPresenter(LoginView view) {
        this.view = view;
        model = new LoginModel(this);
    }


    @Override public void login(Context context, String account, String password, String serverIP, int serverPort) {
        model.login(context,account,password,serverIP,serverPort);
    }

    @Override public void onSendMsgFail(int code) {
        view.onSendMsgFail(code);
    }

    @Override public void onSendMsgSuccess() {
        view.onSendMsgSuccess();
    }
}
