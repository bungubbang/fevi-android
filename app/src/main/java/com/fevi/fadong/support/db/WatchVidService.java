package com.fevi.fadong.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by 1000742 on 15. 6. 30..
 */
public class WatchVidService {


    private static SQLiteDatabase db;

    private static WatchVidService ourInstance;

    public synchronized static WatchVidService getInstance(Context context) {
        if(ourInstance == null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            db = databaseHelper.getWritableDatabase();
            ourInstance = new WatchVidService();
        }
        return ourInstance;
    }

    private WatchVidService() {}

    private static final String TABLE_NAME = "WATCH_VID";


    public boolean exist(String memberId, String vid) {
        Cursor cursor = db.query(TABLE_NAME, new String[]{"member_id", "vid"}, "member_id=? AND vid=?", new String[]{memberId, vid}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getCount();
            return count > 0;
        }
        return false;

    }

    public void insert(String memberId, String vid) {
        ContentValues cv = new ContentValues();
        cv.put("member_id", memberId);
        cv.put("vid", vid);
        long insert = db.insert(TABLE_NAME, null, cv);
        Log.e("insert", "result : " + insert);
    }
}
