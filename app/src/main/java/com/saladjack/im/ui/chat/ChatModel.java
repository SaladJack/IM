package com.saladjack.im.ui.chat;

import android.content.Context;
import android.os.RemoteException;

import com.saladjack.im.app.IMApp;

import java.util.List;

import scut.saladjack.core.bean.FriendBean;
import scut.saladjack.core.bean.FriendMessageBean;
import scut.saladjack.core.db.dao.FriendDao;
import scut.saladjack.core.db.dao.FriendMessageDao;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatModel implements ChatIModel {
    ChatIPresenter presenter;

    public ChatModel(ChatIPresenter presenter) {
        this.presenter = presenter;
    }


    @Override public void sendMessage(Context context, String message, int friendId, boolean qos) {
        if(message.length() > 0) {
            try {
                IMApp.getInstance().getBinder().sendMessage(message,friendId,qos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public void insertMessageToDb(FriendMessageDao friendMessageDao, int friendId, int contentType, String content, long timeStamp) {
        FriendMessageBean friendMessageBean = new FriendMessageBean();
        friendMessageBean.setFriendId(friendId);
        friendMessageBean.setMessageType(contentType);
        friendMessageBean.setMessage(content);
        friendMessageBean.setTimeStamp(timeStamp);
        friendMessageDao.insertFriendMessage(friendMessageBean);
        FriendDao friendDao = new FriendDao();
        FriendBean friendBean = friendDao.query(friendId);
        friendBean.setLatestContent(content);
        friendDao.updateFriend(friendBean);
        friendDao.close();
        presenter.onInsertMessageToDbFinish(friendMessageBean);
    }



    @Override public void queryMessageFromDb(FriendMessageDao friendMessageDao,int friendId) {
        List<FriendMessageBean> friendMessageBeen;
        friendMessageBeen = friendMessageDao.queryFriendMessageList(friendId);
        System.out.println("query:  " + friendMessageBeen.size());
        presenter.onQueryMessageFromDbFinish(friendMessageBeen);
    }

    @Override public void queryUserNameFromDb(FriendMessageDao friendMessageDao, int userId) {
        FriendDao friendDao = new FriendDao();
        String userName = friendDao.query(userId).getName();
        friendDao.close();
        presenter.onQueryUserNameFromDbFinish(userName);
    }
}
