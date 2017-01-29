package com.saladjack.im.ui.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladjack.im.R;
import com.saladjack.im.ui.base.BaseFragment;

/**
 * Created by saladjack on 17/1/28.
 */

public class FriendFragment extends BaseFragment {
    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        return view;
    }
}
