package com.beginagain.yourthinking.BookMaraton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.R;

import java.util.HashMap;

public class BookMaratonInfo extends AppCompatActivity {
    private static final String TAG_Title = "title";    // 테이블에서 검색 할때 태그 이름
    private static final String TAG_PageNum = "pageNum";    // 테이블에서 검색 할때 태그 이름
    private static final String TAG_currPageNum = "currentPageNum";
    private String dbName = "maratonDB";
    private String tableName = "BookTable";
    private final String PREFNAME = "Preferences";


    private TextView bookTitle_TV;
    private TextView bookTotalPage_TV;
    private TextView bookCurrentPage_TV;
    private EditText edit_currentPage;
    private Button btnUpdate;

    String selectedBookTitle;
    String selectedBookTotalPage;
    String selectedBookCurrentPage;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton_info);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        init();

        Intent intent = getIntent();
        HashMap<String, String> selectedBook = (HashMap) (intent.getExtras().getSerializable("selectedBook"));

        selectedBookTitle = selectedBook.get(TAG_Title);
        selectedBookTotalPage = selectedBook.get(TAG_PageNum);
        selectedBookCurrentPage = selectedBook.get(TAG_currPageNum);

        bookTitle_TV.setText(selectedBookTitle);
        bookTotalPage_TV.setText(selectedBookTotalPage);
        bookCurrentPage_TV.setText(selectedBookCurrentPage);
    }

    private void init() {
        bookTitle_TV = findViewById(R.id.bookInfo_title);
        bookTotalPage_TV = findViewById(R.id.bookInfo_totalPageNum);
        bookCurrentPage_TV = findViewById(R.id.bookInfo_currentPage);
        edit_currentPage = findViewById(R.id.currentPageNum_Edit);
        btnUpdate = findViewById(R.id.btnUpdate);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 전체 페이지 데이터에서 p 제거
                int totalPage = Integer.parseInt(selectedBookTotalPage.substring(0, selectedBookTotalPage.length() - 1));
                int newCurrentPage;

                // 읽은 페이지를 입력 하지 않은 경우, 기존 읽은 페이지 사용
                if (edit_currentPage.getText().toString().equals("")) {
                    newCurrentPage = Integer.parseInt(selectedBookCurrentPage.substring(0, selectedBookCurrentPage.length() - 1));
                } else {
                    newCurrentPage = Integer.parseInt(edit_currentPage.getText().toString());
                }

                Log.d("test", "전체 페이지 : " + totalPage + ", " + "읽은 페이지 : " + newCurrentPage);

                if (selectedBookTotalPage.equals(selectedBookCurrentPage)) {
                    Toast.makeText(getApplicationContext(), "이미 완료하였습니다", Toast.LENGTH_SHORT).show();
                } else if (newCurrentPage > totalPage) {
                    Toast.makeText(getApplicationContext(), "잘못된 페이지 입력입니다", Toast.LENGTH_SHORT).show();
                    return;
                } else if (totalPage == newCurrentPage) {
                    bookmaraton_info_clear(newCurrentPage);
                } else {
                    Toast.makeText(getApplicationContext(), "수정 완료!", Toast.LENGTH_SHORT).show();
                    updateCurrentPage(dbName, tableName, Integer.toString(newCurrentPage));
                }
                finish();
            }
        });
    }


    public void bookmaraton_info_clear(int newCurrentPage) {
        SQLiteDatabase bookDB = null;
        String bookTitleValue = bookTitle_TV.getText().toString();
        Cursor c = null;

        try {
            bookDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            bookDB.execSQL("UPDATE " + tableName
                    + " SET " + TAG_Title + " = '" + selectedBookTitle + " (완료)" + "', "
                    + TAG_currPageNum + " = '" + newCurrentPage + "' WHERE " + TAG_Title + " = '" + bookTitleValue + "';");

            // 여기서 매번 완료 설정해주고 나서 모든 도서가 완료 되었는지 검사
            c = bookDB.rawQuery("SELECT * FROM " + tableName, null);
            int totalBooksNum = c.getCount();

            c = bookDB.rawQuery("SELECT * FROM " + tableName + " WHERE " + TAG_Title + " LIKE '%(완료)';", null);
            int finishedBooksNum = c.getCount();

            if(totalBooksNum == finishedBooksNum) {
                Toast.makeText(getApplicationContext(), "도서 마라톤을 완주하셨습니다!", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs  = this.getSharedPreferences(PREFNAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMaratonClear", true);
                editor.commit();

            } else {
                Toast.makeText(getApplicationContext(), selectedBookTitle + " Clear!", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteException e) {
            Log.d("Test", "DB 갱신 발생 : " + e.getMessage());
        } finally {
            if (bookDB != null) {
                bookDB.close();
            }
        }
    }

    private void updateCurrentPage(String dbName, String tableName, String newCurrPage) {
        SQLiteDatabase bookDB = null;
        String bookTitleValue = bookTitle_TV.getText().toString();

        try {
            bookDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            bookDB.execSQL("UPDATE " + tableName
                    + " SET " + TAG_currPageNum + " = '" + newCurrPage + "' WHERE " + TAG_Title + " = '" + bookTitleValue + "';");
        } catch (SQLiteException e) {
            Log.d("Test", "DB 갱신 발생 : " + e.getMessage());
        } finally {
            if (bookDB != null) {
                bookDB.close();
            }
        }
    }
}

