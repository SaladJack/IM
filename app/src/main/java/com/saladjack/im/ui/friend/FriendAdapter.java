package com.saladjack.im.ui.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saladjack.im.R;
import com.saladjack.im.ui.profile.ProfileActivity;

import java.util.List;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by SaladJack on 2017/2/4.
 */

public class FriendAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<UserBean> friends;

    public FriendAdapter(List<UserBean> friends) {
        this.friends = friends;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item_layout,parent,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        VH holder = (VH)viewHolder;
        UserBean userBean = friends.get(position);
        holder.userName.setText(userBean.getUserName());
        holder.userLl.setOnClickListener(v-> ProfileActivity.open(context,userBean));
    }

    @Override public int getItemCount() {
        return friends.size();
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView userName;
        ImageView userAvatar;
        View userLl;
        public VH(View itemView) {
            super(itemView);
            userLl = itemView.findViewById(R.id.friend_item_user_ll);
            userName = (TextView)itemView.findViewById(R.id.friend_item_user_name);
            userAvatar = (ImageView)itemView.findViewById(R.id.friend_item_user_avatar);
        }
    }
}
