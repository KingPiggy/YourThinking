package com.beginagain.yourthinking.Board;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookApiAdapter;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WriteBookActivity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Toolbar mToolbar;
    private EditText mBookTitle;
    private ImageButton mBtnSearch;

    private RecyclerView mWriteRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookApiAdapter recyclerAdapter;

    private String title, ISBN, company, author, thumnail, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_book);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        recyclerAdapter = new BookApiAdapter(this, emptyItems, R.layout.activity_book_recommend);

        init();

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BookRecoAsyncTask().execute();
            }
        });
    }
    private void init(){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBookTitle = findViewById(R.id.et_write_book);
        mBtnSearch = findViewById(R.id.btn_write_book);

        mToolbar= findViewById(R.id.toolbar_bookWrite);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWriteRecyclerView = findViewById(R.id.recycler_write_book);
        mWriteRecyclerView.clearOnChildAttachStateChangeListeners();
        mWriteRecyclerView.addItemDecoration(new DividerItemDecoration(getApplication(), 1));
        layoutManager = new LinearLayoutManager(this);
        mWriteRecyclerView.setLayoutManager(layoutManager);
        mWriteRecyclerView.setAdapter(recyclerAdapter);
        mWriteRecyclerView.setNestedScrollingEnabled(false);
    }

    private class BookRecoAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {

            String myKey = "88137ABBC6F43E258E287321232C8A33FF52C15F971D8B66FA5B42B4FBC74016";
            String urlSource = "";
            String queryType = "&queryType=title";
            String iec = "&inputEncoding=utf-8";
            String outputStyle = "json";
            String image = mBookTitle.getText().toString();

            String str, receiveMsg = "";
            // Toast.makeText(WriteActivity.this, image, Toast.LENGTH_SHORT).show();
            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();

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
                    thumnail = intent.getStringExtra("Thumbnail");
                    company = intent.getStringExtra("Company");

                    RecommendBookItem recommendBookItem1 = new RecommendBookItem(title, author, company, ISBN, thumnail, id);
                    newItems.add(recommendBookItem1);

                }
                if(image!=null) {
                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("item");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject bookItem = jarray.getJSONObject(i);

                        String title = bookItem.getString("title");
                        String author = bookItem.getString("author");
                        String publisher = bookItem.getString("publisher");
                        String isbn = bookItem.getString("isbn");
                        String cover = bookItem.getString("coverLargeUrl");
                        // 쓸만한거 : description : 설명, "priceStandard":가격, "translator":"",

                        RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, cover, id);
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
                Toast.makeText(getApplicationContext(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
    private Bitmap GetImageFromURL(String strImageURL) {
        Bitmap imgBitmap = null;

        try {
            URL url = new URL(strImageURL);
            URLConnection conn = url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);

            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgBitmap;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(WriteBookActivity.this);
        alert_ex.setMessage("뒤로 나가시면 데이터가 사라질 수 있습니다. 나가시겠습니까?");

        alert_ex.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("page", "Board");
                startActivity(intent);
            }
        });
        alert_ex.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert_ex.setTitle("Your Thinking");
        AlertDialog alert = alert_ex.create();
        alert.show();
        finish();
    }
}
