package com.tsd.directory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context) {
        super(context, "itcast.db", null, 1);
    }

    // 数据库第一次创建时被调用该方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatebaseHelper", "创建数据库");
        //初始化数据库得表结构，执行一条建表sql语句
        db.execSQL("CREATE TABLE information (_id INTEGER PRIMARY KEY AUTOINCREMENT ,name VARCHAR(20) ,phone VARCHAR(20))");
    }

    // 当数据库的版本号增加时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
