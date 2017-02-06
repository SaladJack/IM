package com.saladjack.im.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.Button;
import android.widget.TextView;

import com.saladjack.im.R;
import com.saladjack.im.app.Constant;
import com.saladjack.im.ui.base.BaseActivity;
import com.saladjack.im.ui.chat.ChatActivity;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by SaladJack on 2017/2/4.
 */

public class ProfileActivity extends BaseActivity implements ProfileView {
    public static final String USER_BEAN = "user_bean";
    private Button addBtn;
    private Button messagesBtn;

    public static void open(Context context, UserBean userBean){
        Intent intent = new Intent(context,ProfileActivity.class);
        intent.putExtra(USER_BEAN,userBean);
        context.startActivity(intent);
    }

    private UserBean userBean;
    private ProfileIPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        presenter = new ProfilePresenter(this);
        userBean = (UserBean)getIntent().getSerializableExtra(USER_BEAN);
        ((TextView)findViewById(R.id.profile_user_id)).setText(String.format("ID: %d",userBean.getUserId()));
        ((TextView)findViewById(R.id.profile_user_name)).setText("Name: " + userBean.getUserName());
        addBtn = (Button)findViewById(R.id.profile_add);
        messagesBtn = (Button)findViewById(R.id.profile_messages);
        addBtn.setOnClickListener(v-> presenter.addFriend(Constant.USER_ID,userBean.getUserId()));
        messagesBtn.setOnClickListener(v -> ChatActivity.open(this,userBean));
    }

    @Override public void onAddFriendSuccess() {

    }

    @Override public void onAddFriendFail(@StringRes int errorResId) {
        showToast(errorResId);
    }
}
