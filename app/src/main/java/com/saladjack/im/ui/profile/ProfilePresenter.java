package com.saladjack.im.ui.profile;

import com.saladjack.im.R;

/**
 * Created by SaladJack on 2017/2/4.
 */
public class ProfilePresenter implements ProfileIPresenter {
    private final ProfileView view;
    private final ProfileIModel model;
    public ProfilePresenter(ProfileView view) {
        this.view = view;
        model = new ProfileModel(this);
    }

    @Override public void addFriend(int userId, int friendId) {
        model.addFriend(userId,friendId);
    }

    @Override public void onAddFriendSuccess() {
        view.onAddFriendSuccess();
    }

    @Override public void onAddFriendFailByFriendNotFound() {
        view.onAddFriendFail(R.string.add_friend_fail_by_friend_not_found);
    }

    @Override public void onAddFriendFailByAlreadyAdd() {
        view.onAddFriendFail(R.string.add_friend_fail_by_already_add);

    }


}
