package com.beginagain.yourthinking.BookMaraton;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beginagain.yourthinking.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookMaratonHistory_info extends AppCompatActivity {
    private Intent intent;
    private String dbName_History = "HistoryDB";
    private String tableName_history = "HistoryTable";
    private String tableName_History = "HistoryTable";  // 테이블 이름
    private HashMap<String, String> selectedHistory;
    private SQLiteDatabase historyDB;
    private int HISTORY_ID;

    TextView historyDate;
    ListView historyInfoLV;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton_history_info);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        init();

        showHistoryInfo();
    }

    private SQLiteDatabase openDB(String dbName) {
        try {
            return this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        } catch (SQLiteException e) {
            Log.d("Test", "데이터 베이스 열기 실패 : " + e.getMessage());
            return null;
        }
    }

    private void init() {
        historyDB = openDB(dbName_History);
        intent = getIntent();
        historyDate = findViewById(R.id.historyInfoDate_TV);
        historyInfoLV = findViewById(R.id.historyInfoList_LV);
        HISTORY_ID = Integer.parseInt(intent.getStringExtra("history_ID"));

        Log.d("test", "전달받은 히스토리아이디 : " + HISTORY_ID);

        selectedHistory = getHistory(HISTORY_ID);


    }

    private HashMap<String, String> getHistory(int history_id) {
        Cursor c = null;
        HashMap<String, String> item = null;

        try {
            c = historyDB.rawQuery("SELECT * FROM " + tableName_history + " WHERE ID = " + history_id + ";", null);

            if (c != null & c.moveToFirst()) {
                do {
                    String date = c.getString(c.getColumnIndex("date"));
                    String booksTitle = c.getString(c.getColumnIndex("booksTitle"));

                    Log.d("test", "현재 히스토리의 책 제목 : " + booksTitle);
                    item = new HashMap<>();

                    item.put("date", date);
                    item.put("booksTitle", booksTitle);

                } while (c.moveToNext());
            }
        } catch (SQLException e) {
            Log.d("test", "readHistoryData() 에러 : " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return item;
    }

    private void showHistoryInfo() {
        String date = selectedHistory.get("date");
        historyDate.setText(date);
        String rawBooksTitle = selectedHistory.get("booksTitle");

        String[] formattedBooksTitle = formatBooksTilte(rawBooksTitle);

        List<HashMap<String, String>> booksList = new ArrayList<>();

        for(String title : formattedBooksTitle) {
            HashMap<String, String> book = new HashMap<>();
            book.put("title", title);

            booksList.add(book);
        }

        adapter = new SimpleAdapter(getApplicationContext(),
                booksList, R.layout.listview_oneitem,
                new String[]{"title"},
                new int[]{R.id.ListView_oneitem_title_TV});

        historyInfoLV.setAdapter(adapter);
    }

    private String[] formatBooksTilte(String rawBooksTitle) {
        return rawBooksTitle.split("/");
    }
}
