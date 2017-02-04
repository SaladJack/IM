package com.saladjack.im.ui.friend;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.http.RxUtils;

/**
 * Created by SaladJack on 2017/2/4.
 */
public class FriendModel implements FriendIModel {
    private final FriendIPresenter presenter;

    public FriendModel(FriendIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void loadFriends(int userId) {
        RxUtils.createService(FriendsService.class)
                .loadFriends(userId)
                .compose(RxUtils.<List<UserBean>>normalSchedulers())
                .subscribe(userBeen -> presenter.onLoadFriendsSuccess(userBeen),
                        throwable -> presenter.onLoadFriendsFail());
    }

    interface FriendsService{
        @GET("user/getfriends")
        Observable<List<UserBean>> loadFriends(@Query("userid") int userId);
    }
}
