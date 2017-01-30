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
import android.graphics.Color;
import android.os.Bundle;
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

	private Button btnLogout = null;

	private EditText editId = null;
	private EditText editContent = null;
	private TextView viewMyid = null;
	private Button btnSend = null;
	
	private ListView chatInfoListView;
	private MyAdapter chatInfoListAdapter;
	private FriendBean friendBean;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chat_activity);
		presenter = new ChatPresenter(this);
		friendBean = (FriendBean) getIntent().getSerializableExtra(FRIEND_BEAN);
		initViews();
		initOthers();
	}
	private void initViews() {
		btnLogout = (Button)this.findViewById(R.id.logout_btn);
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editId = (EditText)this.findViewById(R.id.id_editText);
		editContent = (EditText)this.findViewById(R.id.content_editText);
		viewMyid = (TextView)this.findViewById(R.id.myid_view);
		chatInfoListView = (ListView)this.findViewById(R.id.chat_lv);
		chatInfoListAdapter = new MyAdapter(this);
		chatInfoListView.setAdapter(chatInfoListAdapter);
		btnSend.setOnClickListener(v-> {
			String content = editContent.getText().toString().trim();
			if(content.length() > 0) {
				showSendMessage(editContent.getText().toString().trim());
				presenter.sendMessage(this, content, Integer.parseInt(editId.getText().toString().trim()), true);
			}
		});
	}


	private void initOthers() {
		editId.setText(""+friendBean.getId());
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
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.black);
	}
	@Override public void showResponseMessage(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.black);
	}

	@Override public void showIMInfo_blue(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.blue);
	}
	@Override public void showIMInfo_brightred(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.brightred);
	}
	@Override public void onDisconnect(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.red);
	}
	@Override public void showIMInfo_green(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.green);
	}

	@Override public void onSendMessageSuccess() {
		//发送成功不显示错误logo即可,此接口留着以后用
	}

	@Override public void onSendMessageFail(Integer code) {

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
         
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
            mData = new ArrayList<>();
        }
        
        public void addItem(String content, ChatInfoColorType color) {
        	Map<String, Object> it = new HashMap<String, Object>();
        	it.put("__content__", content);
			it.put("__time__",hhmmDataFormat.format(new Date()));
        	it.put("__color__", color);
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
        public Object getItem(int arg0) 
        {
            return null;
        }
 
        @Override
        public long getItemId(int arg0) 
        {
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();  
                convertView = mInflater.inflate(R.layout.chat_item_layout, null);
                holder.content = (TextView)convertView.findViewById(R.id.chat_content_tv);
				holder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
             
            holder.content.setText((String)mData.get(position).get("__content__"));
			holder.time.setText((String)mData.get(position).get("__time__"));
            ChatInfoColorType colorType = (ChatInfoColorType)mData.get(position).get("__color__");
            switch(colorType) {
	            case blue:
	            	holder.content.setTextColor(Color.rgb(0,0,255));  
	            	break;
	            case brightred:
	            	holder.content.setTextColor(Color.rgb(255,0,255));  
	            	break;
	            case red:
	            	holder.content.setTextColor(Color.rgb(255,0,0));  
	            	break;
	            case green:
	            	holder.content.setTextColor(Color.rgb(0,128,0));  
	            	break;
	            case black:
	            default:
	            	holder.content.setTextColor(Color.rgb(0, 0, 0));  
	            	break;
            }
             
            return convertView;
        }
        
        public final class ViewHolder {
            public TextView content;
			public TextView time;
		}
    }
	
	/**
	 * 信息颜色常量定义。
	 */
	public enum ChatInfoColorType {
    	black,
    	blue,
    	brightred,
    	red,
    	green,
    }
	//--------------------------------------------------------------- inner classes END
}
