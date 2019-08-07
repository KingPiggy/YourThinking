package com.beginagain.yourthinking.DB;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.R;

import java.util.ArrayList;

public class BookMaratonDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bookmaraton.db";
    public static final int DATABASE_VERSION = 1;

    public BookMaratonDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MENU " +
                "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "Total TEXT, " +
                "Read TEXT);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists MENU");
        onCreate(db);
    }

    public void insert(SQLiteDatabase db, String title,
                       int total, int read) {
        ContentValues itemValues = new ContentValues();
        itemValues.put("Title", title);
        itemValues.put("Total", total);
        itemValues.put("Read", read);
        db.insert("MENU", null, itemValues);
    }

    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM MENU WHERE _ID =" + id + ";");
        db.close();
    }

    public ArrayList<MaratonBookItem> selectAll() {
        ArrayList<MaratonBookItem> items = new ArrayList<MaratonBookItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MENU", null);
        MaratonBookItem item = null;
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            int total = cursor.getInt(2);
            int read = cursor.getInt(3);
            item = new MaratonBookItem(title, total, read);
            item.setId(cursor.getInt(0));
            items.add(item);
        }
        return items;
    }

}
