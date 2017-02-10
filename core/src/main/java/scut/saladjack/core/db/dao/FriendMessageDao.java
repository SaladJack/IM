package scut.saladjack.core.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import scut.saladjack.core.bean.FriendMessageBean;

/**
 * Created by SaladJack on 2017/2/9.
 */

public class FriendMessageDao extends BaseDao {
    private static String TABLE = "_friendmessage";

    private static final String INDEX = "unique_index_friend_message_id";
    private static final String COLUMN_FID = "friend_id";
    private static final String COLUMN_MESSAGETYPE = "friend_messsage_type";
    private static final String COLUMN_MESSAGE = "friend_message";
    private static final String COLUMN_TIMESTAMP = "timestamp";



    /**
     * 建表sql
     *
     * @return sql
     */
    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE + "(");
        sb.append(COLUMN_FID + " INTEGER,");
        sb.append(COLUMN_MESSAGETYPE + " INTEGER, ");
        sb.append(COLUMN_MESSAGE + " varchar(50), ");
        sb.append(COLUMN_TIMESTAMP + " SIGNED BIGINT,");
        sb.append("PRIMARY KEY(" + COLUMN_FID + "," + COLUMN_TIMESTAMP + ")");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 建立索引
     *
     * @return sql
     */
    public static String createIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE UNIQUE INDEX " + INDEX + " ON ");
        sb.append(TABLE + " (" + COLUMN_FID + "," + COLUMN_TIMESTAMP + ")");
        return sb.toString();
    }

    public List<FriendMessageBean> queryFriendMessageList(int uid) {
        String selection = COLUMN_FID + "=?";
        String[] selectionArgs = new String[]{uid + ""};
        Cursor cursor = query(TABLE, null, selection, selectionArgs, null, null, null);
        List<FriendMessageBean> list = new ArrayList<>();

        if (cursor.getCount() == 0) return list;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            list.add(getFriendMessageBean(cursor));

        cursor.close();
        return list;
    }

    public FriendMessageBean getFriendMessageBean(Cursor cursor) {

        FriendMessageBean friendMessageBean = new FriendMessageBean();
        int friendId = cursor.getInt(cursor.getColumnIndex(COLUMN_FID));
        int messageType = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGETYPE));
        String message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE));
        Long timeStamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));

        friendMessageBean.setFriendId(friendId);
        friendMessageBean.setMessageType(messageType);
        friendMessageBean.setMessage(message);
        friendMessageBean.setTimeStamp(timeStamp);

        return friendMessageBean;
    }

    public void insertFriendMessage(FriendMessageBean friendMessageBean){
        insert(TABLE,null,setFriendMessageBean(friendMessageBean));
    }

    public void updateFriendMessage(FriendMessageBean friendMessageBean) {
        replace(TABLE, null, setFriendMessageBean(friendMessageBean));
    }

    public ContentValues setFriendMessageBean(FriendMessageBean friendMessageBean) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FID, friendMessageBean.getFriendId());
        values.put(COLUMN_MESSAGETYPE,friendMessageBean.getMessageType());
        values.put(COLUMN_MESSAGE,friendMessageBean.getMessage());
        values.put(COLUMN_TIMESTAMP, friendMessageBean.getTimeStamp());
        return values;
    }

}
