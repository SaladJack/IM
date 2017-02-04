package com.saladjack.im.ui.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saladjack.im.R;
import scut.saladjack.core.bean.FriendBean;
import scut.saladjack.core.bean.UserBean;

import com.saladjack.im.ui.chat.ChatActivity;

import java.util.List;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageAdapter extends RecyclerView.Adapter {

    private List<FriendBean> array;
    private Context context;

    public MessageAdapter(List<FriendBean> array) {
        this.array = array;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.message_item_layout,parent,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        VH holder = (VH) viewHolder;
        FriendBean friendBean = array.get(position);
        holder.name.setText(friendBean.getName());
        holder.latestContent.setText(friendBean.getLatestContent());
        holder.friendLayout.setOnClickListener((view)-> ChatActivity.open(context,new UserBean(friendBean)));
    }

    @Override public int getItemCount() {
        return array.size();
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