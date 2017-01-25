package net.openmob.mobilesdk.android.ui.register;

import net.openmob.mobilesdk.android.entity.RegisterResult;
import net.openmob.mobilesdk.android.http.RetrofitMananger;
import net.openmob.mobilesdk.android.http.RxUtils;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by saladjack on 17/1/25.
 */

public class RegisterModel implements RegisterIModel {
    private RegisterIPresenter presenter;
    public RegisterModel(RegisterIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void register(String userName, String account, String password) {
        RxUtils.createService(RegisterService.class)
                .register(userName,account,password)
                .compose(RxUtils.<RegisterResult>normalSchedulers())
                .subscribe(registerResult -> {
                    if(registerResult.getRessult().equals("success"))
                        presenter.registerSuccess();
                    else if(registerResult.getRessult().equals("failed"))
                        presenter.registerFailed();
                },throwable -> presenter.registerFailed());
    }

    interface RegisterService{
        @GET
        Observable<RegisterResult> register(@Query("username") String userName,
                                            @Query("account") String account,
                                            @Query("password") String password);
    }
}
