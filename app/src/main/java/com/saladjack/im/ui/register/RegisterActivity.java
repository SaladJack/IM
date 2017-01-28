package com.saladjack.im.ui.register;

import android.os.Bundle;

import com.saladjack.im.ui.base.BaseActivity;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/25.
 */

public class RegisterActivity extends BaseActivity implements RegisterView{
    RegisterIPresenter presenter;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RegisterPresenter(this);

    }

    @Override public void onRegisterSuccess() {
        showToast(R.string.registerSuccess);
    }

    @Override public void onRegisterFail() {
        showToast(R.string.registerFail);
    }
}
