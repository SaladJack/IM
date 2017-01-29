package com.saladjack.im.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saladjack.im.IMClientManager;
import com.saladjack.im.ui.base.BaseActivity;
import com.saladjack.im.ui.chat.ChatFragment;
import com.saladjack.im.ui.friend.FriendFragment;
import com.saladjack.im.ui.message.MessageFragment;
import com.saladjack.im.ui.mine.MineFragment;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/27.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private ImageView chatImg;
    private ImageView friendImg;
    private ImageView mineImg;
    private TextView chatTv;
    private TextView friendTv;
    private TextView mineTv;
    private TextView toolbarTitle;
    private View chat_ll;
    private View friend_ll;
    private View mine_ll;
    private HomePagerAdapter adapter;
    private SparseArray<Fragment> fragmentList = new SparseArray<>();

    public static void open(Context context){
        Intent intent = new Intent(context,HomeActivity.class);
        context.startActivity(intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        chatImg = (ImageView) findViewById(R.id.chat_img);
        chatTv = (TextView)findViewById(R.id.chat_tv);
        chat_ll = findViewById(R.id.chat_ll);
        friendImg = (ImageView) findViewById(R.id.friend_img);
        friendTv = (TextView)findViewById(R.id.friend_tv);
        friend_ll = findViewById(R.id.friend_ll);
        mineImg = (ImageView) findViewById(R.id.mine_img);
        mineTv = (TextView)findViewById(R.id.mine_tv);
        mine_ll = findViewById(R.id.mine_ll);
        mViewPager = (ViewPager) findViewById(R.id.home_pager);
        mViewPager.setOnPageChangeListener(vpSlide);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        chat_ll.setOnClickListener(this);
        friend_ll.setOnClickListener(this);
        mine_ll.setOnClickListener(this);


        adapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        // 释放IM占用资源
        IMClientManager.getInstance(this).release();
        System.exit(0);
    }

    public ViewPager.OnPageChangeListener vpSlide = new ViewPager.OnPageChangeListener() {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override public void onPageScrollStateChanged(int state) {

        }
        @Override public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            if (mViewPager.getCurrentItem() == 0) {
                changeImg(0);
                changeTv(0);
                changeTitle(0);
                changeFragment(0);
            } else if (mViewPager.getCurrentItem() == 1) {
                changeImg(1);
                changeTv(1);
                changeTitle(1);
                changeFragment(1);
            } else if (mViewPager.getCurrentItem() == 2) {
                changeImg(2);
                changeTv(2);
                changeTitle(2);
                changeFragment(2);
            }
        }
    };

    /**
     * 设置选项图案变化
     * @param position
     */
    private void changeImg(int position) {
        switch (position) {
            case 0:
                chatImg.setImageResource(R.drawable.chat_pressed);
                friendImg.setImageResource(R.drawable.friend_normal);
                mineImg.setImageResource(R.drawable.mine_normal);
                break;
            case 1:
                chatImg.setImageResource(R.drawable.chat_normal);
                friendImg.setImageResource(R.drawable.friend_pressed);
                mineImg.setImageResource(R.drawable.mine_normal);
                break;
            case 2:
                chatImg.setImageResource(R.drawable.chat_normal);
                friendImg.setImageResource(R.drawable.friend_normal);
                mineImg.setImageResource(R.drawable.mine_pressed);
                break;
        }
    }

    private void changeTv(int position) {
        switch (position) {
            case 0:
                chatTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                friendTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                mineTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case 1:
                chatTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                friendTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mineTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case 2:
                chatTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                friendTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                mineTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    /**
     * changeTitleText
     * @param position
     */
    private void changeTitle(int position){
        switch (position){
            case 0:
                toolbarTitle.setText(getString(R.string.chat));
                break;
            case 1:
                toolbarTitle.setText(getString(R.string.friends));
                break;
            case 2:
                toolbarTitle.setText(getString(R.string.mine));
                break;
        }
    }

    /**
     * change fragment
     * @param position
     */
    private void changeFragment(int position){
        mViewPager.setCurrentItem(position);
    }

    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_ll:
                changeImg(0);
                changeTv(0);
                changeTitle(0);
                changeFragment(0);
                break;
            case R.id.friend_ll:
                changeImg(1);
                changeTv(1);
                changeTitle(1);
                changeFragment(1);
                break;
            case R.id.mine_ll:
                changeImg(2);
                changeTv(2);
                changeTitle(2);
                changeFragment(2);
                break;
        }

    }

    private class HomePagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_PAGES = 3;

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        private Fragment getFragment(int position){
            Fragment fragment = fragmentList.get(position);
            if(fragment != null){
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = new MessageFragment();
                    break;
                case 1:
                    fragment = new FriendFragment();
                    break;
                case 2:
                    fragment = new MineFragment();
                    break;
            }
            fragmentList.put(position,fragment);
            return fragment;
        }
    }
}
