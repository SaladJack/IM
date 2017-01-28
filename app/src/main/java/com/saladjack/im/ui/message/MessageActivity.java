package com.saladjack.im.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import scut.saladjack.core.bean.FriendAcount;
import com.saladjack.im.ui.base.BaseActivity;

import com.saladjack.im.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageActivity extends BaseActivity {

    public static void open(Context context){
        Intent intent = new Intent(context,MessageActivity.class);
        context.startActivity(intent);
    }

    private RecyclerView friendRv;
    private MessageAdapter adapter;
    private List<FriendAcount> friendList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_activity);
        friendRv = (RecyclerView) findViewById(R.id.friend_rv);
        friendRv.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        for(int i = 0;i < 3;++i) friendList.add(createFriend(i,i+"号","什么鬼"));

        adapter = new MessageAdapter(friendList);
        friendRv.setAdapter(adapter);
    }

    private FriendAcount createFriend(int id, String name, String latestContent){
        FriendAcount friend = new FriendAcount();
        friend.setId(id);
        friend.setName(name);
        friend.setLatestContent(latestContent);
        return friend;
    }


}
