package com.saladjack.im.ui.signup;

/**
 * Created by saladjack on 17/1/25.
 */

public interface SignUpView {
    void onSignUpSuccess();
    void onSignUpFailForAccountAlreadyExist();
    void onSignUpFailForNetWork();
}
