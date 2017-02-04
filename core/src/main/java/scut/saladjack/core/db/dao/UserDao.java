package scut.saladjack.core.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by saladjack on 17/1/28.
 */

public class UserDao extends BaseDao {


    private static String TABLE = "_user";

    private static final String INDEX = "unique_index_user_id";
    private static final String COLUMN_UID = "user_id";
    private static final String COLUMN_USERNAME = "user_name";
    private static final String COLUMN_USERACCOUNT = "user_account";
    private static final String COLUMN_USERPWD = "user_password";



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
        sb.append(COLUMN_USERACCOUNT + " varchar(50), ");
        sb.append(COLUMN_USERPWD + " varchar(50)");
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

    public UserBean query(int uid) {
        String selection = COLUMN_UID + "=?";
        String[] selectionArgs = new String[]{uid + ""};

        Cursor cursor = query(TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();//将游标移动到第一条数据，使用前必须调用
        UserBean userBean = getUserBean(cursor);
        cursor.close();
        return userBean;
    }

    public UserBean getUserBean(Cursor cursor) {

        UserBean userBean = new UserBean();
        int uid = cursor.getInt(cursor.getColumnIndex(COLUMN_UID));
        String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        String account = cursor.getString(cursor.getColumnIndex(COLUMN_USERACCOUNT));
        String password = cursor.getString(cursor.getColumnIndex(COLUMN_USERPWD));

        userBean.setUserId(uid);
        userBean.setUserName(username);
        userBean.setAccount(account);
        userBean.setPassword(password);

        return userBean;
    }

    public void updateUser(UserBean userBean) {
        replace(TABLE, null, setUserBean(userBean));
    }

    public ContentValues setUserBean(UserBean userBean) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, userBean.getUserId());
        values.put(COLUMN_USERACCOUNT,userBean.getAccount());
        values.put(COLUMN_USERNAME, userBean.getUserName());
        values.put(COLUMN_USERPWD, userBean.getPassword());
        return values;
    }
}
