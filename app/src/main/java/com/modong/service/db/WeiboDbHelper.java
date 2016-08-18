package com.modong.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 青松 on 2016/8/15.
 */
public class WeiboDbHelper extends SQLiteOpenHelper {

    public WeiboDbHelper(Context context) {
        super(context, WeiboDbConstants.DB_NAME, null, WeiboDbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(WeiboDbConstants.SQL_CREATE_TOKEN_TABLE);
        sqLiteDatabase.execSQL(WeiboDbConstants.SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeiboDbConstants.TABLE_ACCESS_TOKEN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeiboDbConstants.SQL_CREATE_USER_TABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeiboDbConstants.TABLE_ACCESS_TOKEN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeiboDbConstants.SQL_CREATE_USER_TABLE);
        onCreate(sqLiteDatabase);
    }

}
