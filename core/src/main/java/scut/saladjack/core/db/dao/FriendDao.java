package scut.saladjack.core.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import scut.saladjack.core.bean.FriendBean;

/**
 * Created by SaladJack on 2017/2/6.
 */

public class FriendDao extends BaseDao {

    private static String TABLE = "_friend";

    private static final String INDEX = "unique_index_user_id";
    private static final String COLUMN_UID = "user_id";
    private static final String COLUMN_USERNAME = "user_name";
    private static final String COLUMN_LATESTCONTENT = "user_latest_content";

    /**
     * 建表sql
     *
     * @return sql
     */
    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE + "(");
        sb.append(COLUMN_UID + " INTEGER PRIMARY KEY,");
        sb.append(COLUMN_USERNAME + " varchar(50),");
        sb.append(COLUMN_LATESTCONTENT + " varchar(100)");
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
        sb.append(TABLE + " (" + COLUMN_UID + ")");
        return sb.toString();
    }

    public FriendBean query(int uid) {
        String selection = COLUMN_UID + "=?";
        String[] selectionArgs = new String[]{uid + ""};

        Cursor cursor = query(TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();//将游标移动到第一条数据，使用前必须调用
        FriendBean friendBean = getFriendBean(cursor);
        cursor.close();
        return friendBean;
    }

    public FriendBean getFriendBean(Cursor cursor) {
        int uid = cursor.getInt(cursor.getColumnIndex(COLUMN_UID));
        String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        String latestContent = cursor.getString(cursor.getColumnIndex(COLUMN_LATESTCONTENT));
        return new FriendBean(uid,username,latestContent);
    }

    public void updateFriend(FriendBean friendBean) {
        replace(TABLE, null, setFriendBean(friendBean));
    }

    public ContentValues setFriendBean(FriendBean friendBean) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, friendBean.getId());
        values.put(COLUMN_USERNAME, friendBean.getName());
        values.put(COLUMN_LATESTCONTENT, friendBean.getLatestContent());
        return values;
    }
}
