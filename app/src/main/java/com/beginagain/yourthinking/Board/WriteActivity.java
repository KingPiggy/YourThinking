package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText mWriteTitleText, mWriteContentText, mWriteThumnail;
    private Button mBtnUpload;
    private TextView mWriteDateText, mWriteNameText;

    private String id, editTitle, editContents;
    private String title, ISBN, company, author, thumnail;
    private Editable image = null;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    String formatDate = mFormat.format(mDate);

    // 사진등록을 위한
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookApiAdapter recyclerAdapter = new BookApiAdapter(this, emptyItems, R.layout.activity_book_recommend);
    // 8.2

    String mName = user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mWriteNameText = findViewById(R.id.text_view_write_name);
        mWriteDateText = findViewById(R.id.text_view_write_time);
        mWriteTitleText = findViewById(R.id.et_write_title);
        mWriteContentText = findViewById(R.id.et_write_content);
        mWriteThumnail = findViewById(R.id.et_write_image); // 8.2
        mRecyclerView = findViewById(R.id.recycler_search_book); //8.2
        mBtnUpload = findViewById(R.id.btn_upload_board);

        mWriteNameText.setText(mName);
        mWriteDateText.setText(formatDate);

        // 리사이클러뷰 설정
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        new BookRecoAsyncTask().execute();

        mBtnUpload.setOnClickListener(this);
        mWriteThumnail.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    image=mWriteThumnail.getText();
                    new BookRecoAsyncTask().execute();
                    return true;
                }
                return false;
            }
        });
        mWriteThumnail.setText(image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_board :
                // 생성
                id = mStore.collection("board").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("id", id);
                post.put("name", mWriteNameText.getText().toString());
                post.put("title", mWriteTitleText.getText().toString());
                post.put("contents", mWriteContentText.getText().toString());
                post.put("date", mWriteDateText.getText().toString());
                post.put("image", thumnail);
                post.put("booktitle",title);
                post.put("author", author);
                post.put("recount", 0);

                // id로 필드이름이 같더라도 구별 가능하게 함
                mStore.collection("board").document(id).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WriteActivity.this, "업로드 성공!", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                                intent.putExtra("page", "Board");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WriteActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
    private class BookRecoAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {

            //String myKey = "2824AAAF9F8FBF00CAD4BD88F5C3FB4B45E4DD6DBFD7EDD8332E57AFA7A6708C";
            String myKey = "88137ABBC6F43E258E287321232C8A33FF52C15F971D8B66FA5B42B4FBC74016";
            String urlSource = "";
            String queryType = "&queryType=title";
            String iec = "&inputEncoding=utf-8";
            String outputStyle = "json"; // 혹은 json

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

                    RecommendBookItem recommendBookItem1 = new RecommendBookItem(title, author, company, ISBN, thumnail);
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
                Toast.makeText(getApplicationContext(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}