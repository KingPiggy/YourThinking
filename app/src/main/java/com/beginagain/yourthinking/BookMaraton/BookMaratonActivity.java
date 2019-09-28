package com.beginagain.yourthinking.BookMaraton;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookApiAdapter;
import com.beginagain.yourthinking.Adapter.BookMaratonAdapter;
import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class BookMaratonActivity extends AppCompatActivity {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private RecyclerView mMaratonRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<MaratonBookItem> emptyItems = new ArrayList<MaratonBookItem>();
    private BookMaratonAdapter recyclerAdapter;

    private ImageButton mBtnSearch;
    private EditText mBookTitle;

    private String title, ISBN, company, author, thumnail, id, categoryId, totalPage, readPage, pubdate, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton_add);


        id = mStore.collection("maraton").document().getId();
        recyclerAdapter = new BookMaratonAdapter(this, emptyItems, R.layout.activity_book_maraton_add);
        Init();
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BookSearchAsyncTask().execute();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
    }

    public void Init() {
        mBookTitle = findViewById(R.id.et_maraton_search);
        mBtnSearch = findViewById(R.id.btn_maraton_search);

        mMaratonRecyclerView = findViewById(R.id.recycler_maraton_search);
        mMaratonRecyclerView.clearOnChildAttachStateChangeListeners();
        mMaratonRecyclerView.addItemDecoration(new DividerItemDecoration(getApplication(), 1));
        layoutManager = new LinearLayoutManager(this);
        mMaratonRecyclerView.setLayoutManager(layoutManager);
        mMaratonRecyclerView.setAdapter(recyclerAdapter);
        mMaratonRecyclerView.setNestedScrollingEnabled(false);
    }

    private class BookSearchAsyncTask extends AsyncTask<Void, Void, ArrayList<MaratonBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MaratonBookItem> doInBackground(Void... params) {

            String myKey = "2824AAAF9F8FBF00CAD4BD88F5C3FB4B45E4DD6DBFD7EDD8332E57AFA7A6708C";
            String urlSource = "";
            String queryType = "&queryType=title";
            String iec = "&inputEncoding=utf-8";
            String outputStyle = "json";
            String image = mBookTitle.getText().toString();

            String str, receiveMsg = "";
            // Toast.makeText(WriteActivity.this, image, Toast.LENGTH_SHORT).show();
            ArrayList<MaratonBookItem> newItems = new ArrayList<MaratonBookItem>();

            try {

                urlSource = "http://book.interpark.com/api/search.api?key=" + myKey;
                urlSource += "&query=" + image +iec +queryType+ "&output=" + outputStyle;
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
                if(image==null) {
                    title = intent.getStringExtra("Title");
                    author = intent.getStringExtra("Author");
                    ISBN = intent.getStringExtra("ISBN");
                    date = intent.getStringExtra("");
                    thumnail = intent.getStringExtra("Thumbnail");
                    company = intent.getStringExtra("Company");
                    categoryId = intent.getStringExtra("Category");
                    totalPage = intent.getStringExtra("TotalPage");
                    readPage = intent.getStringExtra("ReadPage");
                    pubdate = intent.getStringExtra("pubDate");

                    MaratonBookItem maratonBookItem= new MaratonBookItem(title, author, company, ISBN, thumnail, categoryId, totalPage, readPage, pubdate);
                    newItems.add(maratonBookItem);

                }
                if(image!=null) {
                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("item");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject bookItem = jarray.getJSONObject(i);

                        String title = bookItem.getString("title");
                        String author = bookItem.getString("author");
                        String publisher = bookItem.getString("publisher");
                        String isbn = bookItem.getString("isbn");
                        String pubData = bookItem.getString("pubDate");
                        String cover = bookItem.getString("coverLargeUrl");
                        String category = bookItem.getString("categoryId");
                        String TotalPage = "총page";
                        String readPage = "읽은page";
                        // 쓸만한거 : description : 설명, "priceStandard":가격, "translator":"",

                        MaratonBookItem maratonBookItem= new MaratonBookItem(title, author, publisher, isbn, cover, category,TotalPage, readPage, pubData);
                        newItems.add(maratonBookItem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newItems;
        }

        @Override
        protected void onPostExecute(ArrayList<MaratonBookItem> newItems) {
            if (newItems.isEmpty()) {
                Toast.makeText(getApplicationContext(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

}