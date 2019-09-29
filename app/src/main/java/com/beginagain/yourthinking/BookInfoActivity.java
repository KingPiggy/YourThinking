package com.beginagain.yourthinking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BoardSearchAdapter;
import com.beginagain.yourthinking.Board.WriteActivity;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookInfoActivity  extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView mImage;
    private Button mButton;
    private TextView mTitle, mAuthor, mPublisher, mDate, mIsbn, mDesc, mNoInfo;
    private String author, image, title, isbn, publisher, desc, date;
    private RecyclerView mInfoRecycler;
    private BoardSearchAdapter mAdapter;
    private List<BookBoardItem> mInfoList;
    private String id = "null";

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        init();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookInfoActivity.this, WriteActivity.class);
                intent.putExtra("Flag","1");
                intent.putExtra("Title", title);
                intent.putExtra("Author",author);
                intent.putExtra("Company",publisher);
                intent.putExtra("Isbn", isbn);
                intent.putExtra("pubDate", date);
                intent.putExtra("Image",image);

                startActivity(intent);
                finish();
            }
        });
    }
    private void init(){

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar_book_info);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInfoRecycler = findViewById(R.id.recycler_view_board_info);
        mInfoRecycler.clearOnChildAttachStateChangeListeners();
        mInfoRecycler.addItemDecoration(new DividerItemDecoration(getApplication(), 1));

        Intent intent = getIntent();
        author = intent.getStringExtra("Author");
        image = intent.getStringExtra("Image");
        title = intent.getStringExtra("Title");
        isbn = intent.getStringExtra("Isbn");
        publisher = intent.getStringExtra("Publisher");
        date = intent.getStringExtra("Date");
        desc = intent.getStringExtra("Desc");

        mTitle = findViewById(R.id.text_view_info_title);
        mAuthor = findViewById(R.id.text_view_info_author);
        mPublisher = findViewById(R.id.text_view_info_publisher);
        mDate = findViewById(R.id.text_view_info_date);
        mIsbn = findViewById(R.id.text_view_info_isbn);
        mNoInfo = findViewById(R.id.text_view_no_info);
        mImage=findViewById(R.id.iv_info_image);
        mDesc=findViewById(R.id.text_view_info_desc);
        mButton=findViewById(R.id.btn_move_board);

        mTitle.setText(title);
        mAuthor.setText(author);
        mPublisher.setText(publisher);
        mIsbn.setText(isbn);
        mDate.setText(date);
        mDesc.setText(desc);
        Picasso.get().load(image).into(mImage);

        mInfoList = new ArrayList<>();
        mStore.collection("board")
                .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                // 실시간 조회하려고 스냅 사용
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String title = (String) dc.getData().get("title");
                            String contents = (String) dc.getData().get("contents");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String author = (String)dc.getData().get("author"); // 8.3
                            String booktitle = (String)dc.getData().get("booktitle"); // 8.3
                            String recommend = String.valueOf(dc.getData().get("recount"));

                            if(mTitle.getText().toString().equals(booktitle)){
                                BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                mInfoList.add(data);
                            }
                        }
                        if(mInfoList.isEmpty()){
                            mNoInfo.setVisibility(View.VISIBLE);
                            mInfoRecycler.setVisibility(View.INVISIBLE);
                        }
                        else {
                            mAdapter = new BoardSearchAdapter(mInfoList);
                            mInfoRecycler.setAdapter(mAdapter);
                        }
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                break;
        }
        return false;
    }
}
