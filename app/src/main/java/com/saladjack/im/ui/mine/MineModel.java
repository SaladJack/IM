package com.saladjack.im.ui.mine;

import android.os.RemoteException;

import com.saladjack.im.app.Constant;
import com.saladjack.im.app.IMApp;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.db.dao.UserDao;
import scut.saladjack.core.http.RxUtils;

/**
 * Created by saladjack on 17/1/28.
 */

public class MineModel implements MineIModel {
    private final MineIPresenter presenter;

    public MineModel(MineIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void fetchUserInfo() {

//        Observable.create((Observable.OnSubscribe<UserBean>) subscriber -> subscriber.onNext(setAndGetUserBean()))
//                .flatMap(userBean -> RxUtils.createService(MineService.class).getUserName(userBean.getUserId()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(userBean ->presenter.onFetchUserInfoSuccess(userBean));

        RxUtils.createService(MineService.class)
                .getUserName(Constant.USER_ID)
                .flatMap(userNameResponseBody -> Observable.create((Observable.OnSubscribe<UserBean>) subscriber -> {
                    try {
                        subscriber.onNext(setAndGetUserBean(userNameResponseBody.string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userBean ->presenter.onFetchUserInfoSuccess(userBean),throwable -> presenter.onFetchUserInfoFail());



    }

    @Override public void signOut() {
        try {
            IMApp.getInstance().getBinder().signOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private UserBean setAndGetUserBean(String userName){
        UserDao userDao = new UserDao();
        UserBean userBean = userDao.query(Constant.USER_ID);
        userBean.setUserName(userName);
        userDao.updateUser(userBean);
        userDao.close();
        return userBean;
    }

    interface MineService{
        @GET("user/getname")
        Observable<ResponseBody> getUserName(@Query("userid") int userId);
    }

}
