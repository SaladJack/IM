package com.saladjack.im.ui.signup;

import scut.saladjack.core.http.RxUtils;
import scut.saladjack.core.bean.SignUpResult;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by saladjack on 17/1/25.
 */

public class SignUpModel implements SignUpIModel {
    private SignUpIPresenter presenter;
    public SignUpModel(SignUpIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void signUp(String userName, String account, String password) {

        RxUtils.createService(SignUpService.class)
                .signUp(userName,account,password)
                .map(SignUpResult::getCode)
                .compose(RxUtils.<Integer>normalSchedulers())
                .subscribe(code -> {
                    if(code == 0)
                        presenter.signUpSuccess();
                    else if(code == 1026)
                        presenter.signUpFailForAccountAlreadyExist();
                },throwable -> presenter.signUpFailForNetWork());
    }

    interface SignUpService {
        @GET("user/register")
        Observable<SignUpResult> signUp(@Query("username") String userName,
                                        @Query("account") String account,
                                        @Query("password") String password);
    }
}
