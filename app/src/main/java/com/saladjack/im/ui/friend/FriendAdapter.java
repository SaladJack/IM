package com.saladjack.im.ui.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saladjack.im.entity.Friend;
import com.saladjack.im.ui.chat.ChatActivity;

import java.util.List;

/**
 * Created by saladjack on 17/1/27.
 */

public class FriendAdapter extends RecyclerView.Adapter {

    private List<Friend> array;
    private View.OnClickListener clickListener;
    private Context context;

    public FriendAdapter(List<Friend> array) {
        this.array = array;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item_layout,parent,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder = (VH) holder;
        Friend friend = array.get(position);
        ((VH) holder).name.setText(friend.getName());
        ((VH) holder).latestContent.setText(friend.getLatestContent());
        ((VH) holder).friendLayout.setOnClickListener((view)-> ChatActivity.open(context,friend));
    }

    @Override public int getItemCount() {
        return array.size();
    }

    public void setOnItemClickListener(View.OnClickListener clickListener){
        this.clickListener = clickListener;
    }

    static class VH extends RecyclerView.ViewHolder{
        View friendLayout;
        TextView name;
        TextView latestContent;
        public VH(View itemView) {
            super(itemView);
            friendLayout = itemView.findViewById(R.id.friend_layout);
            name = (TextView) itemView.findViewById(R.id.name);
            latestContent = (TextView) itemView.findViewById(R.id.latest_content);
        }
    }
}