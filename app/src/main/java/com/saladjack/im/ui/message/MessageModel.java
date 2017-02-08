package com.saladjack.im.ui.message;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import scut.saladjack.core.bean.FriendBean;
import scut.saladjack.core.db.dao.FriendDao;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageModel implements MessageIModel {

    MessageIPrensenter prensenter;

    public MessageModel(MessageIPrensenter prensenter) {
        this.prensenter = prensenter;
    }


    @Override public void queryFriend(int friendId,String content) {
        Observable.create((Observable.OnSubscribe<FriendBean>) subscriber -> subscriber.onNext(_queryFriend(friendId,content)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendBean -> prensenter.onQueryFriendSuccess(friendBean),throwable -> prensenter.onQueryFriendFail());
    }

    private FriendBean _queryFriend(int friendId,String content){
        FriendDao friendDao = new FriendDao();
        FriendBean friendBean = friendDao.query(friendId);
        friendBean.setLatestContent(content);
        friendDao.updateFriend(friendBean);
        friendDao.close();
        System.out.println(friendBean.toString());
        return friendBean;
    }
}
