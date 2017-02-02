package com.saladjack.im.ui.findfriends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saladjack.im.R;

import java.util.List;

import scut.saladjack.core.bean.FindFriendsResult;

/**
 * Created by SaladJack on 2017/2/2.
 */

public class FindFriendsAdapter extends RecyclerView.Adapter {
    private List<FindFriendsResult> array;
    private Context context;
    private FindFriendsResult findFriendsResult;

    public FindFriendsAdapter(List<FindFriendsResult> array) {
        this.array = array;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.findfriend_item_layout,parent,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        VH holder = (VH)viewHolder;
        findFriendsResult = array.get(position);
        holder.userId.setText(String.format("ID Number: %d",findFriendsResult.getUserId()));
        holder.userName.setText("User Name: " + findFriendsResult.getUserName());
//        holder.findFriendsLayout.setOnClickListener();
    }

    @Override public int getItemCount() {
        return array.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        View findFriendsLayout;
        TextView userId;
        TextView userName;

        public VH(View itemView) {
            super(itemView);
            findFriendsLayout = itemView.findViewById(R.id.findfriends_item_layout);
            userId = (TextView) itemView.findViewById(R.id.user_id);
            userName = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}
