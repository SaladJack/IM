package com.saladjack.im.ui.mine;

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

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment,container,false);
        presenter = new MinePresenter(this);
        presenter.fetchUserInfo();
        userName = (TextView)view.findViewById(R.id.user_name);
        view.findViewById(R.id.signout).setOnClickListener(v -> presenter.signout());
        view.findViewById(R.id.find_friends).setOnClickListener(v -> FindFriendsActivity.open(getContext()));
        return view;
    }

    @Override public void updateUserInfo(UserBean userBean) {
        userName.setText(userBean.getUserName());
    }

    @Override public void onSignOutSuccess() {
        getActivity().finish();
        SigninActivity.open(getContext());
    }

    @Override public void onSignOutFail(int code) {
        showToast(R.string.signout_fail + code);
    }


}
