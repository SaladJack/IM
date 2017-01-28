package com.saladjack.im.ui.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladjack.im.ui.base.BaseFragment;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/28.
 */
public class ChatFragment extends BaseFragment  {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_login_activity_layout,container,false);
        return view;
    }



}
