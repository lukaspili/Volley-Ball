package com.siu.android.volleyball.samples.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.siu.android.volleyball.samples.Application;
import com.siu.android.volleyball.samples.model.Entry;
import com.siu.android.volleyball.samples.util.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 8/30/13.
 */
public class EntryDao {

    public static final List<Entry> getEntries() {

        Cursor cursor = Application.getSQLiteDatabase().query(Entry.TABLE, Entry.COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        List<Entry> entries = new ArrayList<Entry>();

        Entry entry;
        while (!cursor.isAfterLast()) {
            entry = new Entry();
            entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Entry.ID)));
            entry.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Entry.TITLE)));
            entries.add(entry);

            cursor.moveToNext();
        }

        return entries;
    }

    public static final void replaceAll(final List<Entry> entries) {
        runInTransaction(new DatabaseTransaction() {
            @Override
            public void run(SQLiteDatabase db) {
                db.delete(Entry.TABLE, null, null);

                ContentValues contentValues;
                for (Entry entry : entries) {
                    contentValues = new ContentValues();
                    contentValues.put(Entry.TITLE, entry.getTitle());

                    db.insert(Entry.TABLE, null, contentValues);
                }
            }
        });
    }

    public static final void save(Entry entry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Entry.ID, entry.getId());
        contentValues.put(Entry.TITLE, entry.getTitle());

        Application.getSQLiteDatabase().insert(Entry.TABLE, null, contentValues);
    }

    public static void runInTransaction(DatabaseTransaction databaseTransaction) {
        SQLiteDatabase db = Application.getSQLiteDatabase();
        db.beginTransaction();
        try {
            databaseTransaction.run(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            SimpleLogger.e("run in transaction error", e);
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
                SimpleLogger.e("end transaction error", e);
            }
        }
    }

    public interface DatabaseTransaction {
        public void run(SQLiteDatabase db);
    }
}
