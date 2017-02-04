package com.saladjack.im.ui.profile;

/**
 * Created by SaladJack on 2017/2/4.
 */
public interface ProfileIPresenter {
    void onAddFriendSuccess();

    void onAddFriendFailByFriendNotFound();

    void onAddFriendFailByAlreadyAdd();

    void addFriend(int userId, int friendId);
}
