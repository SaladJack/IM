package com.saladjack.im.ui.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.saladjack.im.ui.base.BaseActivity;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/25.
 */

public class SignUpActivity extends BaseActivity implements SignUpView {

    private EditText userNameEt;
    private EditText accountEt;
    private EditText passwordEt;

    public static void open(Context context) {
        Intent intent = new Intent(context,SignUpActivity.class);
        context.startActivity(intent);
    }

    SignUpIPresenter presenter;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        presenter = new SignUpPresenter(this);
        userNameEt = (EditText)findViewById(R.id.user_name_et);
        accountEt = (EditText)findViewById(R.id.account_et);
        passwordEt = (EditText)findViewById(R.id.password_et);
        findViewById(R.id.signup_btn).setOnClickListener(v ->{
            String userName = userNameEt.getText().toString().trim();
            String account  = accountEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            if(!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(account) || !TextUtils.isEmpty(password))
                showToast(R.string.signup_info_wrong);
            presenter.signUp(userName,account,password);
        });

    }

    @Override public void onSignUpSuccess() {
        showToast(R.string.registerSuccess);
    }

    @Override
    public void onSignUpFailForAccountAlreadyExist() {
        showToast(R.string.register_fail_for_account_already_exist);
    }

    @Override
    public void onSignUpFailForNetWork() {
        showToast(R.string.register_fail_for_network);
    }



}
