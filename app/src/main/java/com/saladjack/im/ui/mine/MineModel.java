package com.saladjack.im.ui.mine;

import com.saladjack.im.ClientCoreSDK;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.db.dao.UserDao;

/**
 * Created by saladjack on 17/1/28.
 */

public class MineModel implements MineIModel {
    private final MineIPresenter presenter;

    public MineModel(MineIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void fetchUserInfo() {
        Observable.create((Observable.OnSubscribe<UserBean>) subscriber -> subscriber.onNext(getUserBean()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userBean ->presenter.onFetchUserInfoSuccess(userBean));
    }

    private UserBean getUserBean(){
        UserDao userDao = new UserDao();
        UserBean userBean = userDao.query(ClientCoreSDK.getInstance().getCurrentUserId());
        userDao.close();
        return userBean;
    }


}
