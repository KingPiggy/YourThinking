package com.beginagain.yourthinking.BookMaraton;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BookMaratonActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    final String PREFNAME = "Preferences";
    Button mDoneBtn, mBookAddBtn;


    //  '****' 표시 한 곳이 내가 한 부분
    /************************************/
    SQLiteDatabase maratonDB = null;       // 데이터 베이스 객체
    ListView listView;                  // 리스트 뷰
    ListAdapter adapter;                // 리스트 뷰 어댑터
    ArrayList<HashMap<String, String>> books;   // 책 객체를 담고 있는 해쉬 맵을 담은 어레이 리스트
    private String dbName = "maratonDB";       // 디비 이름(너가 임의로 지정)
    private String tableName = "BookTable";  // 테이블 이름 (너가 임의로 지정)
    private static final String TAG_Title = "title";    // 테이블에서 검색 할때 태그 이름
    private static final String TAG_PageNum = "pageNum";    // 테이블에서 검색 할때 태그 이름

    /**********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton);

        /*************************************************/
        books = new ArrayList<>();      // 책 리스트 생성
        listView = (ListView) findViewById(R.id.book_maraton_ListView);

        init();
        showList();
        /**********************************************************/
    }

    @Override
    protected void onResume() {
        super.onResume();
        maratonDB = openDB(dbName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(maratonDB != null) {
            maratonDB.close();
        }
    }

    private void init() {
        maratonDB = openDB(dbName);

        mDoneBtn = (Button) findViewById(R.id.btn_make_maraton_done);

        // 생성하기 버튼 클릭시 db에 마라톤 데이터를 전송
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 마라톤 기간
                String dateFrom = ((TextView) findViewById(R.id.book_maraton_from)).getText().toString();
                String dateTo = ((TextView) findViewById(R.id.book_maraton_to)).getText().toString();

                if (dateFrom.equals("") || dateTo.equals("")) {
                    Toast.makeText(getApplicationContext(), "마라톤 기간을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (dateFrom.length() < 8 || dateTo.length() < 8) {
                    Toast.makeText(getApplicationContext(), "형식을 지켜주세요(ex.YYYY.MM.DD)", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
                    String maratonDate = dateFrom + dateTo;

                    editor = settings.edit();
                    editor.putBoolean("isMaratonGoing", false);
                    editor.putString("maratonDate", maratonDate);

                    editor.commit();

                    sendBooksToDB();

                    Toast.makeText(getApplicationContext(), "독서 마라톤 시작!!", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });

        mBookAddBtn = (Button) findViewById(R.id.btn_maratonbook_add);

        // 책 추가 버튼 클릭시 list에 책 정보 담아서 어댑터에 전달 & 리스트 뷰에 출력
        mBookAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText bookTitleET = (EditText) findViewById(R.id.book_maraton_name);
                EditText bookPageNumET = (EditText) findViewById(R.id.book_maraton_page);
                String bookTitle = bookTitleET.getText().toString();
                String bookPageNum = bookPageNumET.getText().toString();

                if (bookTitle.equals("") || bookPageNum.equals("")) {
                    Toast.makeText(getApplicationContext(), "책 이름과 쪽수를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(isAlreayInList(bookTitle)) {
                    Toast.makeText(getApplicationContext(), "이미 등록된 도서입니다.", Toast.LENGTH_SHORT).show();
                }   else {
                    addBookToList(bookTitle, bookPageNum);
                    showList();
                }
                MaratonBookItem temp = new MaratonBookItem("제목이다", 100, 20);
            }
        });

    }

    private boolean isAlreayInList(String bookTitle) {
        for(HashMap book : books) {
            String addedBookTitle = book.get(TAG_Title).toString();

            if(bookTitle.equals(addedBookTitle))
                return true;
        }
        return false;
    }

    private void sendBooksToDB() {
        try {
            maratonDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            for (int i = 0; i < books.size(); i++) {
                HashMap<String, String> book = books.get(i);
                String bookTitle = book.get(TAG_Title);
                String bookPageNum = book.get(TAG_PageNum);

                maratonDB.execSQL("INSERT INTO " + tableName
                        + " (title, pageNum, currentPageNum)  Values ('" + bookTitle + "', '" + bookPageNum + "', '0');");

                Log.d("Test", "DB에 데이터 삽입 : (" + bookTitle + ", " + bookPageNum + ")");
            }
        } catch (SQLiteException e) {
            Log.d("Test", "데이터 삽입 실패 : " + e.getMessage());
        } finally {
            if (maratonDB != null)
                maratonDB.close();
        }
    }

    /************************************************************************************************/
    private void addBookToList(String bookTitle, String bookPageNum) {

        HashMap<String, String> item = new HashMap<>();
        item.put(TAG_Title, bookTitle);
        item.put(TAG_PageNum, bookPageNum);

        books.add(item);
    }

    private SQLiteDatabase openDB(String dbName) {
        try {
            return this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Log.d("Test", "DB 오픈 실패 : " + e.getMessage());
            return null;
        }
    }

    private void showList() {

        adapter = new SimpleAdapter(this,
                books, R.layout.listview_item,
                new String[]{"title", "pageNum",},
                new int[]{R.id.ListView_oneitem_title_TV, R.id.pageNum});
        listView.setAdapter(adapter);

    }
    /***************************************************************************************************/
}
