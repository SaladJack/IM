package com.saladjack.im.ui.findfriends;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import scut.saladjack.core.bean.FindFriendsResult;
import scut.saladjack.core.http.RxUtils;

/**
 * Created by SaladJack on 2017/2/2.
 */

public class FindFriendsModel implements FindFriendsIModel {


    private final FindFriendsIPresenter presenter;

    public FindFriendsModel(FindFriendsIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void findFriends(String userName) {
        RxUtils.createService(FindFriendsService.class)
                .findFriends(userName)
                .compose(RxUtils.<List<FindFriendsResult>>normalSchedulers())
                .subscribe(friendsResultList -> presenter.onFindFriendsSuccess(friendsResultList)
                 ,throwable -> presenter.onFindFriendsFail());
    }

    interface FindFriendsService{
        @GET("user/findfriends")
        Observable<List<FindFriendsResult>> findFriends(@Query("username") String userName);
    }
}
