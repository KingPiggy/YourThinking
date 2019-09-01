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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookMaratonHistory extends AppCompatActivity {
    SQLiteDatabase historyDB;
    private String dbName_History = "HistoryDB";
    private String tableName_history = "HistoryTable";  // 테이블 이름
    TextView maratonDateTV;
    TextView totalBooksNumTV;
    Button btnHistoryDel;
    ListAdapter adapter;
    ListView historyListView;
    List<HashMap<String, String>> historyList;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton_history);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        init();
    }

    private void init() {
        historyListView = (ListView) findViewById(R.id.historyLV);
        maratonDateTV = findViewById(R.id.history_Date);
        totalBooksNumTV = findViewById(R.id.history_totalBooksNum);
        btnHistoryDel = findViewById(R.id.btn_History_Del);
        historyDB = openDB(dbName_History);
        historyList = new ArrayList<>();

        readHistoryData(historyList);

        adapter = new SimpleAdapter(this,
                historyList, R.layout.listview_history_item,
                new String[]{"date", "totalBooksNum"},
                new int[]{R.id.history_Date, R.id.history_totalBooksNum});
        historyListView.setAdapter(adapter);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Log.d("test", position + "번째 아이템 클릭");

                Intent intent = new Intent(getApplicationContext(), BookMaratonHistory_info.class);
                HashMap<String, String> selectedHistory = historyList.get(position);
                intent.putExtra("selectedHistory", selectedHistory);
                startActivity(intent);
            }
        });

        btnHistoryDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    historyDB.execSQL("DELETE FROM " + tableName_history);
                }catch (SQLiteException e) {
                    Log.d("test", "히스토리 삭제 에러 발생 : " + e.getMessage());
                }
                Toast.makeText(getApplicationContext(), "히스토리 삭제 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyDB = openDB(dbName_History);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (historyDB != null) {
            historyDB.close();
        }
    }

    private void readHistoryData(List<HashMap<String, String>> list) {
        try {
            c = historyDB.rawQuery("SELECT * FROM " + tableName_history, null);

            if (c != null & c.moveToFirst()) {
                historyList = new ArrayList<>();

                do {
                    String date = c.getString(c.getColumnIndex("date"));
                    String booksTitle = c.getString(c.getColumnIndex("booksTitle"));
                    int totalBooksNum = c.getInt(c.getColumnIndex("totalBooksNum"));

                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("date", date);
                    item.put("booksTitle", booksTitle);
                    item.put("totalBooksNum", "전체 : " + totalBooksNum + "권");

                    historyList.add(item);

                } while (c.moveToNext());
            }
        } catch (SQLException e) {
            Log.d("test", "readHistoryData() 에러 : " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private SQLiteDatabase openDB(String dbName) {
        try {
            return this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Log.d("Test", "DB 오픈 실패 : " + e.getMessage());
            return null;
        }
    }
}
