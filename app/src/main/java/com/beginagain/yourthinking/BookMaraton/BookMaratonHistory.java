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
import android.widget.EditText;
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
    ListView history_SearchLV;

    List<HashMap<String, String>> historyList, searchResult_List;
    Cursor c;

    Button btnHistorySearch,btnHistoryList;//919
    EditText HistoryEdit;//919


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
        history_SearchLV = (ListView) findViewById(R.id.historyLV_2);//919
        btnHistorySearch = findViewById(R.id.btn_History_search);//919
        btnHistoryList = findViewById(R.id.btn_History_list);//919
        HistoryEdit =  findViewById(R.id.History_Edit);//919
        historyDB = openDB(dbName_History);
        historyList = new ArrayList<>();
        searchResult_List = new ArrayList<>();

        readHistoryData();

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

                String history_ID = selectedHistory.get("history_ID");
                intent.putExtra("history_ID", history_ID); // String으로 넣어줌
                startActivity(intent);
            }
        });

        history_SearchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Log.d("test", position + "번째 아이템 클릭");

                Intent intent = new Intent(getApplicationContext(), BookMaratonHistory_info.class);
                HashMap<String, String> selectedHistory = searchResult_List.get(position);
                String history_ID = selectedHistory.get("history_ID");
                Log.d("test", "히스토리 아이디 : " + history_ID + " 전달");
                intent.putExtra("history_ID", history_ID);
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

        btnHistorySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //919
                String search_keyWord = HistoryEdit.getText().toString();

                /*if(search_keyWord.length()==0){
                    Toast.makeText(getApplicationContext(),"책 제목을 입력하세요",Toast.LENGTH_SHORT).show();
                }else{*/
                    searchResult_List.removeAll(searchResult_List);
                    search_book(search_keyWord);

                    adapter = new SimpleAdapter(getApplicationContext(),
                            searchResult_List, R.layout.listview_oneitem,
                            new String[]{"title"},
                            new int[]{R.id.ListView_oneitem_title_TV});

                    history_SearchLV.setAdapter(adapter);

                    historyListView.setVisibility(View.INVISIBLE);
                    history_SearchLV.setVisibility(View.VISIBLE);
                //}
                try {

                }catch (SQLiteException e) {
                    Log.d("test", "히스토리 검색 에러 발생 : " + e.getMessage());
                }
            }
        });


        btnHistoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //919
                historyListView.setVisibility(View.VISIBLE);
                history_SearchLV.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void search_book(String search_keyWord) {
        try {
            c = historyDB.rawQuery("SELECT * FROM " + tableName_history + " WHERE booksTitle LIKE '%" + search_keyWord + "%'", null);

            if (c != null & c.moveToFirst()) {
                Log.d("test", "히스토리 디비 검색 성공");

                do {
                    int history_ID = c.getInt(c.getColumnIndex("id"));
                    String booksTitle = c.getString(c.getColumnIndex("booksTitle"));

                    get_targetBooksTitle(history_ID, booksTitle, search_keyWord);

                } while (c.moveToNext());
            }
        } catch (SQLException e) {
            Log.d("test", "searchBook() 에러 : " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void get_targetBooksTitle(int history_ID, String booksTitle, String search_keyWord) {
        String[] titles = booksTitle.split("/");

        for(String title : titles) {
            if(title.contains(search_keyWord)) {
                HashMap<String, String > item = new HashMap<>();
                item.put("title", title);
                item.put("history_ID", Integer.toString(history_ID));
                searchResult_List.add(item);
            }
        }
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

    private void readHistoryData() {
        try {
            c = historyDB.rawQuery("SELECT * FROM " + tableName_history, null);

            if (c != null & c.moveToFirst()) {
                historyList = new ArrayList<>();

                do {
                    int history_ID = c.getInt(c.getColumnIndex("id"));
                    String date = c.getString(c.getColumnIndex("date"));
                    String booksTitle = c.getString(c.getColumnIndex("booksTitle"));
                    int totalBooksNum = c.getInt(c.getColumnIndex("totalBooksNum"));

                    HashMap<String, String> item = new HashMap<String, String>();

                    item.put("history_ID", Integer.toString(history_ID));
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
