package com.fevi.fadong.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 1000742 on 15. 6. 30..
 */
public class WatchVidService {

    private SQLiteDatabase db;

    private static final String TABLE_NAME = "WATCH_VID";

    public WatchVidService(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
    }

    public boolean exist(String memberId, String vid) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE 'member_id'='" + memberId + "' AND 'vid'='" + vid + "'", null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        return count > 0;
    }

    public void insert(String memberId, String vid) {
        ContentValues cv = new ContentValues();
        cv.put("member_id", memberId);
        cv.put("vid", vid);
        db.insert(TABLE_NAME, null, cv);
    }
}
