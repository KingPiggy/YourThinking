package com.beginagain.yourthinking.BookMaraton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookApiAdapter;
import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class BookMaratonActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    final String PREFNAME = "Preferences";
    Button mDoneBtn, mBookAddBtn;


    //  '****' 표시 한 곳이 내가 한 부분
    /************************************/
    SQLiteDatabase maratonDB = null;       // 데이터 베이스 객체
    public static ListView listView;                  // 리스트 뷰
    private ListAdapter adapter;                // 리스트 뷰 어댑터
    public static ArrayList<HashMap<String, String>> books;   // 책 객체를 담고 있는 해쉬 맵을 담은 어레이 리스트
    private String dbName = "maratonDB";       // 디비 이름(너가 임의로 지정)
    private String tableName = "BookTable";  // 테이블 이름 (너가 임의로 지정)
    public static final String TAG_Title = "title";    // 테이블에서 검색 할때 태그 이름
    public static final String TAG_PageNum = "pageNum";    // 테이블에서 검색 할때 태그 이름
    public static final String TAG_imgURL = "img_URL";
    public static final String TAG_currentPageNum = "currentPageNum";
    Button btnDelete;


    public static EditText bookTitle_ET;
    private EditText bookPageNum_ET;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> searchResults = new ArrayList<RecommendBookItem>();
    private BookApiAdapter_for_maraton recyclerAdapter = new BookApiAdapter_for_maraton(this, searchResults, R.layout.activity_book_recommend);
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
        bookTitle_ET =  findViewById(R.id.book_maraton_name);
        bookPageNum_ET =  findViewById(R.id.book_maraton_page);
        mRecyclerView = findViewById(R.id.bookmaraton_search_book);
        mDoneBtn = findViewById(R.id.btn_make_maraton_done);
        btnDelete = findViewById(R.id.btn_delete_maratonList);

        // 리싸이클 뷰 설정
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                books.clear();
                adapter = new BookmartonAdapter(books);
                listView.setAdapter(adapter);
            }
        });

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
                String bookTitle = bookTitle_ET.getText().toString();
                String bookPageNum = bookPageNum_ET.getText().toString();

                if (bookTitle.equals("") || bookPageNum.equals("")) {
                    Toast.makeText(getApplicationContext(), "책 이름과 쪽수를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(isAlreayInList(bookTitle)) {
                    Toast.makeText(getApplicationContext(), "이미 등록된 도서입니다.", Toast.LENGTH_SHORT).show();
                }   else {
                    addBookToList(bookTitle, bookPageNum);
                    showList();
                    new BookSearchAPI_Task().execute();
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
                String url = book.get(TAG_imgURL);

                maratonDB.execSQL("INSERT INTO " + tableName
                        + " (title, pageNum, currentPageNum, img_URL)  Values ('" + bookTitle + "', '" + bookPageNum + "', '0', '" + url + "');");

                Log.d("Test", "DB에 데이터 삽입 : (" + bookTitle + ", " + bookPageNum + ", url : " + url + ")");
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
        item.put(TAG_imgURL, null);
        item.put(TAG_currentPageNum, "0");

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
        adapter = new BookmartonAdapter(books);
        listView.setAdapter(adapter);
    }


    private class BookSearchAPI_Task extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {

            //String myKey = "2824AAAF9F8FBF00CAD4BD88F5C3FB4B45E4DD6DBFD7EDD8332E57AFA7A6708C";
            //String myKey = "88137ABBC6F43E258E287321232C8A33FF52C15F971D8B66FA5B42B4FBC74016";
            String myKey = "C4F75A31D86FE91CC363353F5CE11F556024DD100757BB6BEE5CD90FEBF6A332";
            String urlSource = "";
            String queryType = "&queryType=title";
            String iec = "&inputEncoding=utf-8";
            String outputStyle = "json"; // 혹은 json

            String search_target = bookTitle_ET.getText().toString();
            String str, receiveMsg = "";
            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();

            try {
                urlSource = "http://book.interpark.com/api/search.api?key=" + myKey;
                urlSource += "&query=" + search_target +iec +queryType+ "&output=" + outputStyle;
                URL url = new URL(urlSource);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();
                } else {
                    Toast.makeText(getApplicationContext(), "네트워크 환경이 좋지 않습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Intent intent = getIntent();
                if(search_target==null) {
                    String title = intent.getStringExtra("Title");
                    String author = intent.getStringExtra("Author");
                    String ISBN = intent.getStringExtra("ISBN");
                    String thumnail = intent.getStringExtra("Thumbnail");
                    String company = intent.getStringExtra("Company");

                    RecommendBookItem recommendBookItem1 = new RecommendBookItem(title, author, company, ISBN, thumnail);
                    newItems.add(recommendBookItem1);

                }
                if(search_target!=null) {
                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("item");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject bookItem = jarray.getJSONObject(i);

                        String title = bookItem.getString("title");
                        String author = bookItem.getString("author");
                        String publisher = bookItem.getString("publisher");
                        String isbn = bookItem.getString("isbn");
                        String image = bookItem.getString("coverLargeUrl");
                        // 쓸만한거 : description : 설명, "priceStandard":가격, "translator":"",

                        RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image);
                        newItems.add(recommendBookItem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newItems;
        }

        @Override
        protected void onPostExecute(ArrayList<RecommendBookItem> newItems) {
            if (newItems.isEmpty()) {
                Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
            searchResults.clear();
            searchResults.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    /***************************************************************************************************/
}
