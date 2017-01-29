package com.saladjack.im.ui.signin;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */

public class SigninPresenter implements SigninIPresenter {
    private final SigninView view;
    private SigninIModel model;
    public SigninPresenter(SigninView view) {
        this.view = view;
        model = new SigninModel(this);
    }


    @Override public void signin(Context context, String account, String password, String serverIP, int serverPort) {
        model.signin(context,account,password,serverIP,serverPort);
    }

    @Override public void onSendMsgFail(int code) {
        view.onSendMsgFail(code);
    }

    @Override public void onSendMsgSuccess() {
        view.onSendMsgSuccess();
    }
}
