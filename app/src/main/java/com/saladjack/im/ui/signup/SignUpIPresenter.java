package com.saladjack.im.ui.signup;

/**
 * Created by saladjack on 17/1/25.
 */
public interface SignUpIPresenter {
    void signUp(String userName, String account, String password);
    void signUpSuccess();

    void signUpFailForAccountAlreadyExist();

    void signUpFailForNetWork();
}
