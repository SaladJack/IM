package com.saladjack.im.ui.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scut.saladjack.core.bean.FriendBean;
import scut.saladjack.core.bean.FriendMessageBean;
import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.db.dao.FriendMessageDao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.saladjack.im.R;
import com.saladjack.im.ui.home.HomeActivity;
import com.saladjack.im.utils.TimeUtils;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatActivity extends Activity implements ChatView {


	public static void open(Context context, UserBean userBean){
		Intent intent = new Intent(context,ChatActivity.class);
		intent.putExtra(USER_BEAN,userBean);
		context.startActivity(intent);
	}


	public final static String USER_BEAN = "user_bean";
	private ChatAdapter adapter;
	private LinearLayoutManager linearLayoutManager;
	private ChatIPresenter presenter;

	private TextView toolbarTitle;
	private EditText editContent;
	private Button btnSend;
	private int friendId;
	private UserBean userBean;
	private RecyclerView chatRv;
	private BroadcastReceiver mChatReceiver;
	private FriendMessageDao friendMessageDao;
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chat_activity);
		presenter = new ChatPresenter(this);
		initViews();
		handleIntent();
		mChatReceiver = new BroadcastReceiver() {
			@Override public void onReceive(Context context, Intent intent) {
				abortBroadcast();
				Bundle bundle = intent.getBundleExtra("bundle");
				int _friendId = bundle.getInt("friendId",friendId);
				if(_friendId != friendId) return;
				int contentType = bundle.getInt("contentType",-1);
				String content = bundle.getString("content");
				presenter.insertMessageToDb(friendMessageDao,friendId,contentType,content, TimeUtils.getCurTimeMills());

			}
		};

	}
	private void handleIntent(){
		if(friendMessageDao == null) friendMessageDao = new FriendMessageDao();
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");
		String content = null;
		if(bundle != null) {
			content = bundle.getString("content");
			userBean = (UserBean) bundle.getParcelable(USER_BEAN);
		}else if(intent != null){
			userBean = (UserBean) intent.getParcelableExtra(USER_BEAN);
		}
			friendId = userBean.getUserId();
			if (TextUtils.isEmpty(userBean.getUserName()))
				presenter.queryUserNameFromDb(friendMessageDao, userBean.getUserId());
			else setTitle(userBean.getUserName());
			if (!TextUtils.isEmpty(content))
				presenter.insertMessageToDb(friendMessageDao, friendId, ContentType.RECEIVE, content, TimeUtils.getCurTimeMills());

	}
	private void initViews() {
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editContent = (EditText)this.findViewById(R.id.content_editText);
		chatRv = (RecyclerView)findViewById(R.id.chat_rv);
		toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
		linearLayoutManager = new LinearLayoutManager(this);
		chatRv.setLayoutManager(linearLayoutManager);
		adapter = new ChatAdapter();
		chatRv.setAdapter(adapter);
		btnSend.setOnClickListener(v-> {
			String content = editContent.getText().toString().trim();
			if(content.length() > 0) {
				editContent.setText("");
				long time = TimeUtils.getCurTimeMills();
				showSendMessage(content,time);
				presenter.insertMessageToDb(friendMessageDao,friendId,ContentType.SEND,content,time);
				presenter.sendMessage(this, content, userBean.getUserId(), true);
			}
		});
		

	}

	private void setTitle(String title){
		toolbarTitle.setText(title);
	}

	@Override protected void onStart() {
		super.onStart();
		if(friendMessageDao == null) friendMessageDao = new FriendMessageDao();
		presenter.queryMessageFromDb(friendMessageDao,friendId);
		IntentFilter filterChat = new IntentFilter("chat");
		filterChat.setPriority(70);
		registerReceiver(mChatReceiver,filterChat);
	}

	@Override protected void onStop() {
		super.onStop();
		friendMessageDao.close();
		friendMessageDao = null;
		unregisterReceiver(mChatReceiver);

	}

	@Override public void onBackPressed() {
		super.onBackPressed();
		finish();
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("friendId",friendId);
		intent.putExtra("latestContent",adapter.getLastItem().getMessage());
		startActivity(intent);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
	}

	


	//--------------------------------------------------------------- 各种信息输出方法 START
	public void showSendMessage(String txt,long time) {
		adapter.addItem(txt,ContentType.SEND,time);
	}

	public void showResponseMessage(String txt,long time) {
		adapter.addItem(txt,ContentType.RECEIVE,time);
	}

	public void showIMInfo_blue(String txt,long time) {
		adapter.addItem(txt,ContentType.FAIL,time);
	}
	public void showSendMessageFail(String txt,long time) {
		adapter.addItem(txt,ContentType.FAIL,time);
	}
	public void showDisconnectMessage(String txt,long time) {
		adapter.addItem(txt,ContentType.DISCONNECT,time);
	}
	public void showReConnectMessage(String txt,long time) {
		adapter.addItem(txt,ContentType.RECONNECT,time);
	}

	@Override public void onSendMessageSuccess() {
		//发送成功不显示错误logo即可,此接口留着以后用
	}

	@Override public void onSendMessageFail(Integer code) {
//		showSendMessageFail();
	}

	@Override public void onInsertMessageToDbFinish(FriendMessageBean friendMessageBean) {
		int contentType = friendMessageBean.getMessageType();
		long time = friendMessageBean.getTimeStamp();
		String content = friendMessageBean.getMessage();
		switch (contentType){
			case ContentType.RECEIVE:
				showResponseMessage(content,time);
				break;
			case ContentType.FAIL:
				showSendMessageFail(content,time);
			case ContentType.DISCONNECT:
				showDisconnectMessage(content,time);
				break;
			case ContentType.RECONNECT:
				showReConnectMessage(content,time);
				break;
		}
	}

	@Override public void onQueryMessageFromDbFinish(List<FriendMessageBean> friendMessageBeen) {
		adapter.setData(friendMessageBeen);
	}

	@Override public void onQueryUserNameFromDbFinish(String userName) {
		setTitle(userName);
	}

	//--------------------------------------------------------------- 各种信息输出方法 END
	
	//--------------------------------------------------------------- inner classes START


	public class ChatAdapter extends RecyclerView.Adapter {
		
		private Context context;
		private List<FriendMessageBean> mData;
		private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");

		public ChatAdapter() {
			mData = new ArrayList<>();
		}
		public void setData(List<FriendMessageBean> friendMessageBeen) {
			mData.clear();
			System.out.println("setData+"  + friendMessageBeen.size());
			mData.addAll(friendMessageBeen);
			this.notifyDataSetChanged();
			chatRv.smoothScrollToPosition(getItemCount());
		}

		public FriendMessageBean getLastItem(){
			return mData.get(mData.size() - 1);
		}

		public synchronized void addItem(String content, int type,long time) {
			FriendMessageBean it = new FriendMessageBean();
			it.setMessage(content);
			it.setMessageType(type);
			it.setTimeStamp(time);
			mData.add(it);
			this.notifyDataSetChanged();
			chatRv.smoothScrollToPosition(getItemCount());
		}


		@Override public int getItemViewType(int position) {
			return mData.get(position).getMessageType();
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
				default://FAIL,CONNECT,RECONNECT
					view = LayoutInflater.from(context).inflate(R.layout.chat_item_fail_layout, parent, false);
					return new FailVH(view);
			}
		}

		@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if(holder == null) return;
			if (holder instanceof SendVH) {
				SendVH sendVH = (SendVH) holder;
				if(sendVH.content != null) sendVH.content.setText(mData.get(position).getMessage());
				if(sendVH.time != null)    sendVH.time.setText(TimeUtils.milliseconds2String(mData.get(position).getTimeStamp(),hhmmDataFormat));
			} else if (holder instanceof ReceiveVH) {
				ReceiveVH receiveVH = (ReceiveVH) holder;
				if(receiveVH.content != null)  receiveVH.content.setText(mData.get(position).getMessage());
				if(receiveVH.time != null) 	   receiveVH.time.setText(TimeUtils.milliseconds2String(mData.get(position).getTimeStamp(),hhmmDataFormat));
			} else if (holder instanceof FailVH) {
				FailVH failVH = (FailVH) holder;
				if (failVH.content != null)    failVH.content.setText(mData.get(position).getMessage());
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

}

