package com.saladjack.im.ui.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladjack.im.ClientCoreSDK;
import com.saladjack.im.R;
import com.saladjack.im.app.Constant;
import com.saladjack.im.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by saladjack on 17/1/28.
 */

public class FriendFragment extends BaseFragment implements FriendView {
    private RecyclerView friendRv;
    private FriendIPresenter presenter;
    private FriendAdapter adapter;
    private List<UserBean> userBeen = new ArrayList<>();
    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        friendRv = (RecyclerView) view.findViewById(R.id.friend_rv);
        adapter = new FriendAdapter(userBeen);
        friendRv.setLayoutManager(new LinearLayoutManager(getContext()));
        friendRv.setAdapter(adapter);
        presenter = new FriendPresenter(this);
        presenter.loadFriends(Constant.USER_ID);
        return view;
    }


    @Override public void onLoadFriendsSuccess(List<UserBean> userBeen) {
        this.userBeen.clear();
        this.userBeen.addAll(userBeen);
        adapter.notifyDataSetChanged();
    }

    @Override public void onLoadFriendsFail() {
        showToast(R.string.load_friends_fail);
    }
}
