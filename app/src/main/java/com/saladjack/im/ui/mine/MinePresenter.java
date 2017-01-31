package com.saladjack.im.ui.mine;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by saladjack on 17/1/28.
 */

public class MinePresenter implements MineIPresenter {

    private final MineView view;
    private final MineIModel model;

    public MinePresenter(MineView view) {
        this.view = view;
        model = new MineModel(this);
    }

    @Override public void signout() {
        model.signout();
    }

    @Override public void fetchUserInfo() {
        model.fetchUserInfo();
    }

    @Override public void onFetchUserInfoSuccess(UserBean userBean) {
        view.updateUserInfo(userBean);
    }

    @Override public void onSignoutSuccess() {
        view.onSingoutSuccess();
    }

    @Override public void onSignoutFail(int code) {
        view.onSignoutFail(code);
    }

}
