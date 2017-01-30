package com.saladjack.im.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import scut.saladjack.core.bean.FriendBean;

import com.saladjack.im.ClientCoreSDK;

import com.saladjack.im.R;
import com.saladjack.im.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageFragment extends BaseFragment {

    private RecyclerView friendRv;
    private MessageAdapter adapter;
    private List<FriendBean> friendList;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_fragment,container,false);
        friendRv = (RecyclerView) view.findViewById(R.id.friend_rv);
        friendRv.setLayoutManager(new LinearLayoutManager(getContext()));

        friendList = new ArrayList<>();
        int id = ClientCoreSDK.getInstance().getCurrentUserId();
        friendList.add(createFriend(id,id+"号","什么鬼"));

        adapter = new MessageAdapter(friendList);
        friendRv.setAdapter(adapter);
        return view;
    }



    private FriendBean createFriend(int id, String name, String latestContent){
        FriendBean friend = new FriendBean();
        friend.setId(id);
        friend.setName(name);
        friend.setLatestContent(latestContent);
        return friend;
    }


}