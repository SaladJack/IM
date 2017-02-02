package com.saladjack.im.ui.findfriends;

import java.util.List;

import scut.saladjack.core.bean.FindFriendsResult;

/**
 * Created by SaladJack on 2017/2/2.
 */

public class FindFriendsPresenter implements FindFriendsIPresenter {
    private final FindFriendsIModel model;
    private FindFriendsView view;

    public FindFriendsPresenter(FindFriendsView view) {
        this.view = view;
        model = new FindFriendsModel(this);
    }


    @Override public void findFriends(String userName) {
        model.findFriends(userName);
    }

    @Override public void onFindFriendsSuccess(List<FindFriendsResult> friendsResultList) {
        view.onFindFriendsSuccess(friendsResultList);
    }

    @Override public void onFindFriendsFail() {
        view.onFindFriendsFail();
    }
}
