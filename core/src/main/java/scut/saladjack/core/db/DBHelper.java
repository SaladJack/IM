package scut.saladjack.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import scut.saladjack.core.db.dao.FriendDao;
import scut.saladjack.core.db.dao.FriendMessageDao;
import scut.saladjack.core.db.dao.UserDao;

/**
 * Created by saladjack on 17/1/28.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "im.db";
    private static final int DATABASEVERSION = 1;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        System.out.println("DbHelper onCreate");
        //用户表
        db.execSQL(UserDao.createTable());
        db.execSQL(UserDao.createIndex());
        //朋友表
        db.execSQL(FriendDao.createTable());
        db.execSQL(FriendDao.createIndex());
        //消息表
        db.execSQL(FriendMessageDao.createTable());
        db.execSQL(FriendMessageDao.createIndex());


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("ALTER TABLE note ADD COLUMN marktes integer");//增减一项 保存用户数据
        //onCreate(db);
    }
}

