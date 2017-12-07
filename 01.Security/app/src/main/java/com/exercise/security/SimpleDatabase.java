package com.exercise.security;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Philip
 * @since 2017-12-07
 */

public class SimpleDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "SimpleDatabase";

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "simpledata";

    public static final String TABLE_LINKS = "links";
    public static final String ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "url";

    private static final String CREATE_TABLE_TUTORIALS =
            "create table " + TABLE_LINKS
                    + " (" + ID + " integer primary key autoincrement, "
                    + COL_TITLE + " text not null, "
                    + COL_URL + " text not null);";

    private static final String DB_SCHEMA = CREATE_TABLE_TUTORIALS;

    public SimpleDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKS);
        onCreate(db);
    }

}
