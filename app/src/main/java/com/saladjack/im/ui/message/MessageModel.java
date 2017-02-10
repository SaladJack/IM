package com.saladjack.im.ui.message;

import com.saladjack.im.ui.chat.ContentType;
import com.saladjack.im.utils.TimeUtils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import scut.saladjack.core.bean.FriendBean;
import scut.saladjack.core.bean.FriendMessageBean;
import scut.saladjack.core.db.dao.FriendDao;
import scut.saladjack.core.db.dao.FriendMessageDao;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageModel implements MessageIModel {

    MessageIPrensenter prensenter;

    public MessageModel(MessageIPrensenter prensenter) {
        this.prensenter = prensenter;
    }


    @Override public void insertMessageToDbAndQueryFriend(int friendId, String content) {
        Observable.create((Observable.OnSubscribe<FriendBean>) subscriber -> subscriber.onNext(_insertMessageToDbAndQueryFriend(friendId,content)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendBean -> prensenter.onInsertMessageToDbAndQueryFriendSuccess(friendBean), throwable -> prensenter.onInsertMessageToDbAndQueryFriendFail());
    }

    @Override public void queryFriendWithLatestContent() {
        FriendDao friendDao = new FriendDao();
        //query
        List<FriendBean> friendBeen = friendDao.queryList();
        friendDao.close();
        prensenter.onQueryFriendWithLatestContentFinish(friendBeen);
    }

    private FriendBean _insertMessageToDbAndQueryFriend(int friendId, String content){
        FriendDao friendDao = new FriendDao();
        FriendMessageDao friendMessageDao = new FriendMessageDao();
        //query
        FriendBean friendBean = friendDao.query(friendId);
        friendBean.setLatestContent(content);
        friendDao.updateFriend(friendBean);
        //insert
        FriendMessageBean friendMessageBean = new FriendMessageBean();
        friendMessageBean.setMessage(content);
        friendMessageBean.setMessageType(ContentType.RECEIVE);
        friendMessageBean.setFriendId(friendId);
        friendMessageBean.setTimeStamp(TimeUtils.getCurTimeMills());
        friendMessageDao.insertFriendMessage(friendMessageBean);
        friendMessageDao.close();
        friendDao.close();

        System.out.println(friendBean.toString());
        return friendBean;
    }
}
