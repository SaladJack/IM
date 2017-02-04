package com.saladjack.im.ui.profile;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import scut.saladjack.core.bean.NormalResult;
import scut.saladjack.core.http.RxUtils;

/**
 * Created by SaladJack on 2017/2/4.
 */
public class ProfileModel implements ProfileIModel {
    private final ProfileIPresenter presenter;

    public ProfileModel(ProfileIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void addFriend(int userId, int friendId) {
        RxUtils.createService(ProfileService.class)
                .addFriend(userId,friendId)
                .compose(RxUtils.<NormalResult>normalSchedulers())
                .subscribe(normalResult -> {
                    switch (normalResult.getCode()){
                        case 0 :
                            presenter.onAddFriendSuccess();
                            break;
                        case 1:
                            presenter.onAddFriendFailByFriendNotFound();
                            break;
                        case 2:
                            presenter.onAddFriendFailByAlreadyAdd();
                    }
                });
    }

    interface ProfileService{
        @GET("friend/addfriend")
        Observable<NormalResult> addFriend(@Query("userid") int userId,@Query("friendid") int friendId);

    }
}
