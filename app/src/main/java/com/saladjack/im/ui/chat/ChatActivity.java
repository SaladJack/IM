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

import com.saladjack.im.IMClientManager;
import scut.saladjack.core.bean.FriendBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.saladjack.im.R;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatActivity extends Activity implements ChatView
{
	public static void open(Context context, FriendBean friendBean){
		Intent intent = new Intent(context,ChatActivity.class);
		intent.putExtra(FRIEND_BEAN,friendBean);
		context.startActivity(intent);
	}

	private final static String TAG = ChatActivity.class.getSimpleName();

	private final static String FRIEND_BEAN = "friendBean";
	private ChatIPresenter presenter;

	private EditText editContent;
	private Button btnSend;
	
	private ListView chatInfoListView;
	private MyAdapter chatInfoListAdapter;
	private FriendBean friendBean;
	private RecyclerView chatInfoRv;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chat_activity);
		presenter = new ChatPresenter(this);
		friendBean = (FriendBean) getIntent().getSerializableExtra(FRIEND_BEAN);
		initViews();
		initOthers();
	}
	private void initViews() {
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editContent = (EditText)this.findViewById(R.id.content_editText);

		chatInfoListView = (ListView)this.findViewById(R.id.chat_lv);
		chatInfoListAdapter = new MyAdapter(this);
		chatInfoListView.setAdapter(chatInfoListAdapter);
		btnSend.setOnClickListener(v-> {
			String content = editContent.getText().toString().trim();
			if(content.length() > 0) {
				showSendMessage(editContent.getText().toString().trim());
				presenter.sendMessage(this, content,friendBean.getId(), true);
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
		chatInfoListAdapter.addItem(txt,ContentType.SEND);
	}
	@Override public void showResponseMessage(String txt) {
		chatInfoListAdapter.addItem(txt,ContentType.RECEIVE);
	}

	@Override public void showIMInfo_blue(String txt) {
//		chatInfoListAdapter.addItem(txt, ChatInfoColorType.blue);
	}
	@Override public void showSendMessageFail(String txt) {
		chatInfoListAdapter.addItem(txt,ContentType.FAIL);
	}
	@Override public void onDisconnect(String txt) {
		chatInfoListAdapter.addItem(txt,ContentType.FAIL);
	}
	@Override public void onReConnectSuccess(String txt) {
		chatInfoListAdapter.addItem(txt,ContentType.FAIL);
	}

	@Override public void onSendMessageSuccess() {
		//发送成功不显示错误logo即可,此接口留着以后用
	}

	@Override public void onSendMessageFail(Integer code) {
//		showSendMessageFail();
	}
	//--------------------------------------------------------------- 各种信息输出方法 END
	
	//--------------------------------------------------------------- inner classes START
	/**
	 * 各种显示列表Adapter实现类。
	 */
	public class MyAdapter extends BaseAdapter {
		private List<Map<String, Object>> mData;
        private LayoutInflater mInflater;
        private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
		private SendVH sendVH;
		private ReceiveVH receiveVH;
		private FailVH failVH;

		public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
            mData = new ArrayList<>();
        }
        
        public void addItem(String content,int type) {
        	Map<String, Object> it = new HashMap<String, Object>();
        	it.put("content", content);
			it.put("time",hhmmDataFormat.format(new Date()));
			it.put("type",type);
        	mData.add(it);
        	this.notifyDataSetChanged();
        	chatInfoListView.setSelection(this.getCount());
        }
        
        @Override
        public int getCount() 
        {
            return mData.size();
        }
 
        @Override
        public Map<String, Object> getItem(int position)
        {
            return mData.get(position);
        }

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return (int) mData.get(position).get("type");
		}


 
        @Override
        public View getView(int position, View view, ViewGroup parent) {
			int type = getItemViewType(position);
			switch (type){
				case ContentType.SEND:
					if (view == null) {
						sendVH = new SendVH();
						view = mInflater.inflate(R.layout.chat_item_send_layout, null);
						sendVH.content = (TextView) view.findViewById(R.id.send_content);
						sendVH.time = (TextView) view.findViewById(R.id.send_time);
						view.setTag(sendVH);
					}else{
						sendVH = (SendVH) view.getTag();
					}
					sendVH.content.setText((String)mData.get(position).get("content"));
					sendVH.time.setText((String)mData.get(position).get("time"));
					break;
				case ContentType.RECEIVE:
					if (view == null) {
						receiveVH = new ReceiveVH();
						view = mInflater.inflate(R.layout.chat_item_receive_layout, null);
						receiveVH.content = (TextView) view.findViewById(R.id.receive_content);
						receiveVH.time = (TextView) view.findViewById(R.id.receive_time);
						view.setTag(receiveVH);
					}else{
						receiveVH = (ReceiveVH) view.getTag();
					}
					sendVH.content.setText((String)mData.get(position).get("content"));
					sendVH.time.setText((String)mData.get(position).get("time"));
					break;
				case ContentType.FAIL:
					if (view == null) {
						failVH = new FailVH();
						view = mInflater.inflate(R.layout.chat_item_fail_layout, null);
						failVH.content = (TextView) view.findViewById(R.id.fail_content);
						view.setTag(failVH);
					}else{
						failVH = (FailVH) view.getTag();
					}
					failVH.content.setText((String)mData.get(position).get("content"));
					break;
			}

            return view;
        }
        
        public final class SendVH {
            public TextView content = null;
			public TextView time = null;
		}
		public final class ReceiveVH {
			public TextView content = null;
			public TextView time = null;
		}
		public final class FailVH {
			public TextView content = null;
		}
    }
	


	public class ContentType{
		static final int SEND = 1;
		static final int RECEIVE = 2;
		static final int FAIL = 3;
	}
	//--------------------------------------------------------------- inner classes END
}
