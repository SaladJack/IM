package com.saladjack.im.ui.signup;

/**
 * Created by saladjack on 17/1/25.
 */

public class SignUpPresenter implements SignUpIPresenter {

    private SignUpView view;
    private SignUpIModel model;

    public SignUpPresenter(SignUpView view) {
        this.view = view;
        model = new SignUpModel(this);
    }

    @Override public void signUp(String userName, String account, String password) {
        model.signUp(userName,account,password);
    }

    @Override public void signUpSuccess() {
        view.onSignUpSuccess();
    }

    @Override public void signUpFailForAccountAlreadyExist() {
        view.onSignUpFailForAccountAlreadyExist();
    }

    @Override public void signUpFailForNetWork() {
        view.onSignUpFailForNetWork();
    }


}
