/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * MainActivity.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.ui.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.saladjack.im.IMClientManager;

import scut.saladjack.core.bean.UserBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatActivity extends Activity implements ChatView
{
	private ChatAdapter adapter;

	public static void open(Context context, UserBean userBean){
		Intent intent = new Intent(context,ChatActivity.class);
		intent.putExtra(USER_BEAN,userBean);
		context.startActivity(intent);
	}

	private final static String TAG = ChatActivity.class.getSimpleName();

	private final static String USER_BEAN = "user_bean";
	private ChatIPresenter presenter;

	private EditText editContent;
	private Button btnSend;
	
	private UserBean userBean;
	private RecyclerView chatRv;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chat_activity);
		presenter = new ChatPresenter(this);
		userBean = (UserBean) getIntent().getSerializableExtra(USER_BEAN);
		initViews();
		initOthers();
	}
	private void initViews() {
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editContent = (EditText)this.findViewById(R.id.content_editText);

		chatRv = (RecyclerView)findViewById(R.id.chat_rv);
		chatRv.setLayoutManager(new LinearLayoutManager(this));
		adapter = new ChatAdapter();
		chatRv.setAdapter(adapter);
		btnSend.setOnClickListener(v-> {
			String content = editContent.getText().toString().trim();
			if(content.length() > 0) {
				showSendMessage(editContent.getText().toString().trim());
				presenter.sendMessage(this, content, userBean.getUserId(), true);
			}
		});
	}


	private void initOthers() {
		IMClientManager.getInstance(this).getTransDataListener().setChatView(this);
		IMClientManager.getInstance(this).getBaseEventListener().setChatView(this);
		IMClientManager.getInstance(this).getMessageQoSListener().setChatView(this);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		IMClientManager.getInstance(this).getTransDataListener().setChatView(null);
		IMClientManager.getInstance(this).getBaseEventListener().setChatView(null);
		IMClientManager.getInstance(this).getMessageQoSListener().setChatView(null);
	}

	
//	private void doExit()
//	{
//		finish();
//		System.exit(0);
//	}

	//--------------------------------------------------------------- 各种信息输出方法 START
	@Override public void showSendMessage(String txt) {
		adapter.addItem(txt,ContentType.SEND);
	}
	@Override public void showResponseMessage(String txt) {
		adapter.addItem(txt,ContentType.RECEIVE);
	}

	@Override public void showIMInfo_blue(String txt) {
	}
	@Override public void showSendMessageFail(String txt) {
		adapter.addItem(txt,ContentType.FAIL);
	}
	@Override public void onDisconnect(String txt) {
		adapter.addItem(txt,ContentType.FAIL);
	}
	@Override public void onReConnectSuccess(String txt) {
		adapter.addItem(txt,ContentType.FAIL);
	}

	@Override public void onSendMessageSuccess() {
		//发送成功不显示错误logo即可,此接口留着以后用
	}

	@Override public void onSendMessageFail(Integer code) {
//		showSendMessageFail();
	}
	//--------------------------------------------------------------- 各种信息输出方法 END
	
	//--------------------------------------------------------------- inner classes START


	public class ChatAdapter extends RecyclerView.Adapter {

		private List<Map<String, Object>> mData;
		private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
		private Context context;

		public ChatAdapter() {
			mData = new ArrayList<>();
		}

		public synchronized void addItem(String content, int type) {
			Map<String, Object> it = new HashMap<String, Object>();
			it.put("content", content);
			it.put("time", hhmmDataFormat.format(new Date()));
			it.put("type", type);
			mData.add(it);
			this.notifyDataSetChanged();
		}


		@Override public int getItemViewType(int position) {
			return (int) mData.get(position).get("type");
		}

		@Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			context = parent.getContext();
			View view;
			switch (viewType) {
				case ContentType.SEND:
					view = LayoutInflater.from(context).inflate(R.layout.chat_item_send_layout, parent, false);
					return new SendVH(view);
				case ContentType.RECEIVE:
					view = LayoutInflater.from(context).inflate(R.layout.chat_item_receive_layout, parent, false);
					return new ReceiveVH(view);
				case ContentType.FAIL:
					view = LayoutInflater.from(context).inflate(R.layout.chat_item_fail_layout, parent, false);
					return new SendVH(view);
			}
			return null;
		}

		@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if(holder == null) return;
			if (holder instanceof SendVH) {
				SendVH sendVH = (SendVH) holder;
				sendVH.content.setText((String) mData.get(position).get("content"));
				sendVH.time.setText((String) mData.get(position).get("time"));
			} else if (holder instanceof ReceiveVH) {
				ReceiveVH receiveVH = (ReceiveVH) holder;
				receiveVH.content.setText((String) mData.get(position).get("content"));
				receiveVH.time.setText((String) mData.get(position).get("time"));
			} else if (holder instanceof FailVH) {
				FailVH failVH = (FailVH) holder;
				failVH.content.setText((String) mData.get(position).get("content"));
			}

		}

		@Override
		public int getItemCount() {
			return mData.size();
		}

		class SendVH extends RecyclerView.ViewHolder {
			public TextView content;
			public TextView time;
			public SendVH(View itemView) {
				super(itemView);
				content = (TextView) itemView.findViewById(R.id.send_content);
				time = (TextView) itemView.findViewById(R.id.send_time);
			}
		}

		class ReceiveVH extends RecyclerView.ViewHolder {
			public TextView content;
			public TextView time;

			public ReceiveVH(View itemView) {
				super(itemView);
				content = (TextView) itemView.findViewById(R.id.receive_content);
				time = (TextView) itemView.findViewById(R.id.receive_time);
			}
		}

		class FailVH extends RecyclerView.ViewHolder {
			public TextView content;

			public FailVH(View itemView) {
				super(itemView);
				content = (TextView) itemView.findViewById(R.id.fail_content);
			}


		}

	}
	public class ContentType{
		static final int SEND = 1;
		static final int RECEIVE = 2;
		static final int FAIL = 3;
	}
	//--------------------------------------------------------------- inner classes END
}

