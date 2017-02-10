package com.saladjack.im.ui.findfriends;

import java.util.List;

import scut.saladjack.core.bean.FindFriendsResult;

/**
 * Created by SaladJack on 2017/2/2.
 */
public interface FindFriendsView {

    void onFindFriendsSuccess(List<FindFriendsResult> friendsResultList);

    void onFindFriendsFail();
}
