package com.sunny.putra.clipco.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Wayan-MECS on 3/23/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "clipboard";
    public static final String TABLE_NAME = "clipboard";
    public static final String KOLOM_ID = "id";
    public static final String KOLOM_TEXT = "teks";
    public static final String KOLOM_TIMESTAMP = "timestamp";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" + KOLOM_ID + " integer primary key,"
                        + KOLOM_TEXT + " text, "
                        + KOLOM_TIMESTAMP + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        if (oldVersion != newVersion) {
            Log.d("Log_SQlite", "new version");
        }
    }

    public boolean insertValue(String timestamp, String teks) {
        LogHelper.print_me("1");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KOLOM_TEXT, teks);
        contentValues.put(KOLOM_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_NAME, null, contentValues);
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public Cursor getAllData() { //WITH DISTINCT
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        Cursor res = db.query(true, TABLE_NAME, new String[]{KOLOM_ID, KOLOM_TEXT, KOLOM_TIMESTAMP}, null, null, KOLOM_TEXT, null, null, null);
        return res;
    }

    public void delDuplicateRow(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + KOLOM_ID + " not in (select min(" + KOLOM_ID + ")" +
                " from " + TABLE_NAME + " group by " + KOLOM_TEXT + ");");
        db.close();
    }

    public void delNullRow(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + KOLOM_TEXT + " IS NULL OR trim(" + KOLOM_TEXT + ") = '';");
        db.close();
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + KOLOM_ID + " = '" + id + "'", null);
        return res;
    }

    public Cursor deleteLastRow() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + KOLOM_ID + " = " +
                "(select max (" + KOLOM_ID + " ) from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getData2() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public int updateRow(String id, String clip) {
        LogHelper.print_me("get keys on dbh = " + id + " | " + clip);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KOLOM_TEXT, clip);

        // updating row
        return db.update(TABLE_NAME, values, KOLOM_ID + " = ?",
                new String[]{id});
    }


    /*public boolean updateRow1(String id, String clip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KOLOM_TEXT, clip);

        db.update(TABLE_NAME, contentValues, KOLOM_TEXT + "= ?", new String[]{(id)});
        LogHelper.print_me("==updated==");
        return true;
    }*/

    /*public int updateRow(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Field1","Bob");
        cv.put("Field2","19");
        db.update(MY_TABLE_NAME, cv, "_id = ?", new String[]{id});
    }*/

    public int deleteRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                KOLOM_ID + " = ? ",
                new String[]{id});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(KOLOM_TIMESTAMP)));
            res.moveToNext();
        }
        return array_list;
    }

}
