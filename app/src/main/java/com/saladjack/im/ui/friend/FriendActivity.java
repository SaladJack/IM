package com.saladjack.im.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.saladjack.im.entity.Friend;
import com.saladjack.im.ui.BaseActivity;

import net.openmob.mobilesdk.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saladjack on 17/1/27.
 */

public class FriendActivity extends BaseActivity {

    public static void open(Context context){
        Intent intent = new Intent(context,FriendActivity.class);
        context.startActivity(intent);
    }

    private RecyclerView friendRv;
    private FriendAdapter adapter;
    private List<Friend> friendList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_activity);
        friendRv = (RecyclerView) findViewById(R.id.friend_rv);
        friendRv.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        for(int i = 0;i < 3;++i) friendList.add(createFriend(i,i+"号","什么鬼"));

        adapter = new FriendAdapter(friendList);
        friendRv.setAdapter(adapter);
    }

    private Friend createFriend(int id,String name,String latestContent){
        Friend friend = new Friend();
        friend.setId(id);
        friend.setName(name);
        friend.setLatestContent(latestContent);
        return friend;
    }


}
