package com.shlominet.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtil extends SQLiteOpenHelper{

    public static final String DATA_NAME = "to_do.db";
    public static final String TABLE_NAME = "list";
    public static final String ID = "ID", TITLE = "TITLE", DESC = "DESCRIPTION",
            PRIO = "PRIORITY", DATE = "DATE", COMPLETED = "COMPLETED";
    public static final String CREATE = "CREATE TABLE "+ TABLE_NAME+
            "("+ ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ TITLE+
            " TEXT, "+ DESC+ " TEXT, "+ PRIO+ " TEXT, "+ DATE+ " INTEGER, "+ COMPLETED+ " INTEGER)";
    public static final String SELECTALL = "SELECT * FROM "+ TABLE_NAME+ " ORDER BY "+ DATE;

    public DBUtil(Context context) {
        super(context, DATA_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long saveToSql(String title, String desc, String prio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(DESC, desc);
        cv.put(PRIO, prio);
        cv.put(DATE, System.currentTimeMillis());
        cv.put(COMPLETED, 0);

        long result = db.insert(TABLE_NAME, null, cv);
        return result;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECTALL, null);
        return cursor;
    }

    public long updateComp(long id, boolean complete) {
        ContentValues cv = new ContentValues();
        if(complete) cv.put(COMPLETED, 1);
        else cv.put(COMPLETED, 0);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.update(TABLE_NAME, cv, ID+ "="+ id, null);
        return result;
    }

    public int deleteRow(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, ID+ "="+ id, null);
        return result;
    }
}
