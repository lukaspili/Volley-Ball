package com.siu.android.volleyball.samples.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lukas on 8/30/13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String NAME = "sample.db";
    public static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE 'ENTRIES' (" +
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'title' TEXT);"); // 1: title

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
