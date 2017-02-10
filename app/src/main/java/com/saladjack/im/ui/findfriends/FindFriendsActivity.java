package com.saladjack.im.ui.findfriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.saladjack.im.R;
import com.saladjack.im.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import scut.saladjack.core.bean.FindFriendsResult;

/**
 * Created by SaladJack on 2017/2/2.
 */

public class FindFriendsActivity extends BaseActivity implements FindFriendsView {

    private FindFriendsIPresenter presenter;
    private EditText findFriendsEt;

    public static void open(Context context){
        Intent intent = new Intent(context,FindFriendsActivity.class);
        context.startActivity(intent);
    }
    private RecyclerView findFriendsRv;
    private FindFriendsAdapter adapter;
    private List<FindFriendsResult> findFriendsList = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findfriends_activity);
        presenter = new FindFriendsPresenter(this);
        findFriendsEt = (EditText)findViewById(R.id.findfriends_et);
        findViewById(R.id.find_btn).setOnClickListener(view->presenter.findFriends(findFriendsEt.getText().toString()));
        findFriendsRv = (RecyclerView)findViewById(R.id.findfriends_rv);
        findFriendsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FindFriendsAdapter(findFriendsList);
        findFriendsRv.setAdapter(adapter);
    }

    @Override public void onFindFriendsSuccess(List<FindFriendsResult> friendsResultList) {
        findFriendsList.clear();
        findFriendsList.addAll(friendsResultList);
        adapter.notifyDataSetChanged();
    }

    @Override public void onFindFriendsFail() {
        showToast(R.string.find_friends_fail);
    }
}
