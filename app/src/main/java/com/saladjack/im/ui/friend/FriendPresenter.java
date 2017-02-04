package com.saladjack.im.ui.friend;

import java.util.List;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by SaladJack on 2017/2/4.
 */
public class FriendPresenter implements FriendIPresenter {
    private final FriendView view;
    private final FriendIModel model;
    public FriendPresenter(FriendView view) {
        this.view = view;
        model = new FriendModel(this);
    }

    @Override public void loadFriends(int userId) {
        model.loadFriends(userId);
    }

    @Override public void onLoadFriendsSuccess(List<UserBean> userBeen) {
        view.onLoadFriendsSuccess(userBeen);
    }

    @Override public void onLoadFriendsFail() {
        view.onLoadFriendsFail();
    }
}
