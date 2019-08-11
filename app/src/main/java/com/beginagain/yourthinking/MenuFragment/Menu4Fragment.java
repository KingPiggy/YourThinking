package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.BookMaraton.BookMaratonActivity;
import com.beginagain.yourthinking.BookMaraton.BookMaratonHistory;
import com.beginagain.yourthinking.BookMaraton.BookMaratonInfo;
import com.beginagain.yourthinking.BookMaraton.BookmartonAdapter;
import com.beginagain.yourthinking.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Menu4Fragment extends Fragment {
    /*************************************/
    private String dbName_Maraton = "maratonDB";
    private String dbName_History = "HistoryDB";
    private String tableName_Maraton = "BookTable";
    private String tableName_History = "HistoryTable";  // 테이블 이름

    private static final String TAG_Title = "title";
    private static final String TAG_PageNum = "pageNum";
    private static final String TAG_currPageNum = "currentPageNum";
    final String PREFNAME = "Preferences";

    SQLiteDatabase maratonDB = null;
    SQLiteDatabase historyDB = null;

    ListView listView;                  // 리스트 뷰
    ListAdapter adapter;                // 리스트 뷰 어댑터
    ArrayList<HashMap<String, String>> books;   // 책 객체를 담고 있는 해쉬 맵을 담은 어레이 리스트
    Cursor c = null;
    TextView maratonDate;
    /***************************************/
    TextView mGuideText;
    Button mMakeBtn, mDeleteBtn, btnFinish, btnHistory;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu4, container, false);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        maratonDB = openDB(dbName_Maraton);
        historyDB = openDB(dbName_History);

        if (isBookMaratonOnGoing()) {
            showOnGoingMode();
            showList();
        } else {
            showAddMode();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (maratonDB != null) {
            maratonDB.close();
        }
        if (historyDB != null) {
            historyDB.close();
        }
    }

    // 북 마라톤 리스트 출력
    private void showList() {
        try {
            maratonDB = this.getActivity().openOrCreateDatabase(dbName_Maraton, MODE_PRIVATE, null);
            c = maratonDB.rawQuery("SELECT * FROM " + tableName_Maraton, null);

            if (c != null & c.moveToFirst()) {
                books = new ArrayList<>();

                do {
                    String title = c.getString(c.getColumnIndex(TAG_Title));
                    String pageNum = c.getString(c.getColumnIndex(TAG_PageNum)) + "p";
                    String currentPage = c.getString(c.getColumnIndex(TAG_currPageNum))+"p";
                    String url = c.getString(c.getColumnIndex(BookMaratonActivity.TAG_imgURL));

                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put(TAG_Title, title);
                    item.put(TAG_PageNum, pageNum);
                    item.put(TAG_currPageNum, currentPage);
                    item.put(BookMaratonActivity.TAG_imgURL, url);

                    books.add(item);
                } while (c.moveToNext());

                adapter = new BookmartonAdapter(books);

                listView.setAdapter(adapter);
            }
        } catch (SQLiteException e) {
            Log.d("test", "DB 읽기 실패 : " + e.getMessage());
        }finally {
            if(c != null) {
                c.close();
            }
        }
    }

    private void showOnGoingMode() {//여기바꿈
        SharedPreferences prefs = this.getActivity().getSharedPreferences(PREFNAME, MODE_PRIVATE);

        mGuideText.setVisibility(View.INVISIBLE);
        mMakeBtn.setVisibility(View.INVISIBLE);
        mDeleteBtn.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        maratonDate.setVisibility(View.VISIBLE);
        btnHistory.setVisibility(View.INVISIBLE);
        btnFinish.setVisibility(View.INVISIBLE);
        String rawDate = prefs.getString("maratonDate", "");
        String formattedDate = formatDate(rawDate);

        maratonDate.setText("기간: " + formattedDate);

        if (prefs.getBoolean("isMaratonClear", false)) {
            btnFinish.setVisibility(View.VISIBLE);
        }
    }

    private String formatDate(String date) {
        String dateFrom = date.substring(0, 8);
        String fromYear = dateFrom.substring(0, 4);
        String fromMonth = dateFrom.substring(4, 6);
        String fromDay = dateFrom.substring(6, 8);

        String dateTo = date.substring(8, 16);
        String toYear = dateTo.substring(0, 4);
        String toMonth = dateTo.substring(4, 6);
        String toDay = dateTo.substring(6, 8);

        return fromYear + "." + fromMonth + "." + fromDay + " ~ " + toYear + "." + toMonth + "." + toDay;
    }

    private void showAddMode() {
        mGuideText.setVisibility(View.VISIBLE);
        mMakeBtn.setVisibility(View.VISIBLE);
        mDeleteBtn.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        maratonDate.setVisibility(View.INVISIBLE);
        btnFinish.setVisibility(View.INVISIBLE);
        btnHistory.setVisibility(View.VISIBLE);
    }

    // DB 오픈 후 DB 객체 반환, 실패시 null 반환
    private SQLiteDatabase openDB(String dbName) {
        try {
            return this.getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        } catch (SQLiteException e) {
            Log.d("Test", "데이터 베이스 열기 실패 : " + e.getMessage());
            return null;
        }
    }

    // 북마라톤이 진행중인지 확인
    private boolean isBookMaratonOnGoing() {
        try {
            maratonDB = this.getActivity().openOrCreateDatabase(dbName_Maraton, MODE_PRIVATE, null);
            c = maratonDB.rawQuery("SELECT * FROM " + tableName_Maraton, null);

            if (c != null & c.moveToFirst()) {
                Log.d("Test", "*********** 북 마라톤 진행중 ***********");
                return true;

            } else {
                Log.d("Test", "******** 진행중인 북 마라톤 없음 ************");
                return false;
            }
        } catch (SQLiteException e) {
            Log.d("Test", "데이터 베이스 읽기 실패 : " + e.getMessage());
        } finally {
            if (c != null)
                c.close();
        }
        return false;
    }

    void init() {
        mGuideText =  view.findViewById(R.id.text_view_menu4_guide);
        mMakeBtn = view.findViewById(R.id.btn_menu4_make);
        mDeleteBtn =  view.findViewById(R.id.btn_menu4_delete);
        listView = view.findViewById(R.id.bookMaratonLV);
        maratonDate = view.findViewById(R.id.maratonDate);
        btnFinish = view.findViewById(R.id.btn_finish);
        btnHistory = view.findViewById(R.id.btn_history);

        maratonDB = openDB(dbName_Maraton);
        maratonDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName_Maraton
                + " (title VARCHAR(20), pageNum VARCHAR(20), currentPageNum VARCHAR(5), img_URL VARCHAR(1000));");

        historyDB = openDB(dbName_History);
        historyDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName_History
                + " (date VARCHAR(20), booksTitle VARCHAR(500), totalBooksNum INTEGER);");

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMaratonDB();
                showAddMode();
            }
        });

        mMakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookMaratonActivity.class);
                startActivity(intent);
                showList();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookMaratonHistory.class);
                startActivity(intent);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getActivity(), "히스토리에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    addMaratonToHistoryDB();
                } catch (SQLiteException se) {
                    Log.d("test", "히스토리에 데이터 삽입 실패 : " + se.getMessage());
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//a-여기
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookMaratonInfo.class);
                HashMap<String, String> selectedBook = books.get(position);
                intent.putExtra("selectedBook", selectedBook);
                startActivity(intent);
            }
        });
    }

    private void deleteMaratonDB() {
        try {
            maratonDB.execSQL("delete from " + tableName_Maraton + ";");
            set_IsMaratonClear_False();
        } catch (SQLiteException e) {
            Log.d("test", "마라톤 DB 삭제 실패 : " + e.getMessage());
        }
    }

    private void addMaratonToHistoryDB() {
        String maratonDate = getMaratonDate();
        String maratonBooksStr = getMaratonBooksTitleStr();
        int totalBooksNum = getTotalBooksNum();

        deleteMaratonDB();

       insertHistoryData(maratonDate, maratonBooksStr, totalBooksNum);
       showAddMode();
    }

    private int getTotalBooksNum() {
        int totalBookNums = 0;
        try {
            Log.d("test", maratonDB + "");
            c = maratonDB.rawQuery("SELECT * FROM " + tableName_Maraton, null);
            totalBookNums = c.getCount();
        } catch (SQLException e) {
            Log.d("test","getTotalBooksNum 에러 : " + e.getMessage());
        } finally {
            if(c != null) {
                c.close();
            }
        }
        return  totalBookNums;
    }

    private String getMaratonBooksTitleStr() {
        String  booksTilte = "";
        try {
            c = maratonDB.rawQuery("SELECT * FROM " + tableName_Maraton, null);

            if (c != null & c.moveToFirst()) {
                do {
                    String title = c.getString(c.getColumnIndex(TAG_Title));
                    booksTilte += title + "/";
                } while (c.moveToNext());
            }
        } catch (SQLException e) {
            Log.d("test", "getMaratonBooksTitleStr 에러 : " + e.getMessage());
        }finally {
            if(c != null) {
                c.close();
            }
        }
        return booksTilte;
    }

    private String getMaratonDate() {
       return maratonDate.getText().toString();
    }

    private void insertHistoryData(String maratonDate, String maratonBooksStr, int totalBooksNum) {
        c = historyDB.rawQuery("SELECT * FROM " + tableName_History, null);

        historyDB.execSQL("INSERT INTO " + tableName_History
                + " (date,  booksTitle, totalBooksNum)  Values ('" + maratonDate + "', '" + maratonBooksStr + "', " + totalBooksNum + ");");
        Log.d("test", "히스토리 DB에 삽입");
        Log.d("test", "(" + maratonDate + ", " + maratonBooksStr + ", " + totalBooksNum + ")");
    }

    private void set_IsMaratonClear_False() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isMaratonClear", false);
        editor.commit();
    }
}