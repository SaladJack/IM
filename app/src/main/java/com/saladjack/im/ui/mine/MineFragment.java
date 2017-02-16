package com.saladjack.im.ui.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saladjack.im.R;
import com.saladjack.im.ui.base.BaseFragment;
import com.saladjack.im.ui.findfriends.FindFriendsActivity;
import com.saladjack.im.ui.signin.SigninActivity;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by saladjack on 17/1/28.
 */

public class MineFragment extends BaseFragment implements MineView{

    private MineIPresenter presenter;
    private TextView userName;
    private BroadcastReceiver signOutReceiver;
    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment,container,false);
        presenter = new MinePresenter(this);
        presenter.fetchUserInfo();
        userName = (TextView)view.findViewById(R.id.profile_user_name);
        view.findViewById(R.id.signout).setOnClickListener(v -> presenter.signout());
        view.findViewById(R.id.find_friends).setOnClickListener(v -> FindFriendsActivity.open(getContext()));
        signOutReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                int code = intent.getBundleExtra("bundle").getInt("code");
                if(code == 0) onSignOutSuccess();
                else onSignOutFail(code);
            }
        };
        return view;
    }

    @Override public void onResume() {
        super.onResume();
        IntentFilter filterSignOut = new IntentFilter("signout");
        getActivity().registerReceiver(signOutReceiver,filterSignOut);
    }

    @Override public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(signOutReceiver);
    }

    @Override public void updateUserInfo(UserBean userBean) {
        System.out.println("updateUserinfo: " + userBean.getUserName());
        userName.setText(userBean.getUserName());
    }

    @Override public void onSignOutSuccess() {
        getActivity().finish();
        SigninActivity.open(getContext());
    }

    @Override public void onSignOutFail(int code) {
        showToast(R.string.signout_fail + code);
    }

    @Override public void onFetchUserInfoFail() {
        showToast(R.string.fetch_userinfo_fail);
    }


}
