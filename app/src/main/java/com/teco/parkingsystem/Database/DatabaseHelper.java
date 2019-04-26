package com.teco.parkingsystem.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 24-04-2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "parking_history_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create notes table
        sqLiteDatabase.execSQL(HistoryModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HistoryModel.TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public String InsertParkingPlace(String name, String description, String startTime,
                                   String endTime, String totalTime) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(HistoryModel.COLUMN_NAME, name);
        contentValues.put(HistoryModel.COLUMN_DESCRIPTION, description);
        contentValues.put(HistoryModel.COLUMN_START_TIME, startTime);
        contentValues.put(HistoryModel.COLUMN_END_TIME, endTime);
        contentValues.put(HistoryModel.COLUMN_TOTAL_TIME, totalTime);
        Long id = sqLiteDatabase.insert(HistoryModel.TABLE_NAME, null, contentValues);

        return String.valueOf(id);
    }

    public HistoryModel getHistory(String id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(HistoryModel.TABLE_NAME,
                new String[]{HistoryModel.COLUMN_ID, HistoryModel.COLUMN_NAME
                        , HistoryModel.COLUMN_DESCRIPTION
                        , HistoryModel.COLUMN_START_TIME
                        , HistoryModel.COLUMN_END_TIME
                        , HistoryModel.COLUMN_TOTAL_TIME
                        , HistoryModel.COLUMN_TIMESTAMP},
                HistoryModel.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        // prepare note object
        HistoryModel history = null;
        if(cursor != null && cursor.moveToFirst()) {
            history = new HistoryModel(
                    cursor.getInt(cursor.getColumnIndex(HistoryModel.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_END_TIME)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_TOTAL_TIME)),
                    cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_TIMESTAMP)));

            // close the db connection
            cursor.close();
        }
        return history;
    }

    public List<HistoryModel> getAllHistory() {
        List<HistoryModel> historyList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + HistoryModel.TABLE_NAME + " ORDER BY " +
                HistoryModel.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HistoryModel history = new HistoryModel();
                history.setId(cursor.getInt(cursor.getColumnIndex(HistoryModel.COLUMN_ID)));
                history.setName(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_NAME)));
                history.setDescription(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_DESCRIPTION)));
                history.setStartTime(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_START_TIME)));
                history.setEndTime(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_END_TIME)));
                history.setTotalTime(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_TOTAL_TIME)));
                history.setTimeStamp(cursor.getString(cursor.getColumnIndex(HistoryModel.COLUMN_TIMESTAMP)));

                historyList.add(history);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return historyList;
    }

    public int updateHistory(Long id, String endTime, String totalTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HistoryModel.COLUMN_END_TIME, endTime);
        values.put(HistoryModel.COLUMN_TOTAL_TIME, totalTime);

        // updating row
        return db.update(HistoryModel.TABLE_NAME, values, HistoryModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteHistory(HistoryModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HistoryModel.TABLE_NAME, HistoryModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
