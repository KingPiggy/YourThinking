package com.beginagain.yourthinking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.NoticeAdapter;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    String mUser;

    public RecyclerView noticeRecyclerView;
    public List<BookBoardItem> mNoticeList;
    public NoticeAdapter mAdapter;

    private FloatingActionButton fabWrite;

    String mUid=null;
    String supervisor = "dkllRndBmVgPrJlrCl9WLIvqixN2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        Intent intent = getIntent();
        mUser = intent.getStringExtra("user");
        // Toast.makeText(getApplicationContext(), mUser, Toast.LENGTH_SHORT).show();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_notice);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noticeRecyclerView = findViewById(R.id.recycler_notice);
        noticeRecyclerView.clearOnChildAttachStateChangeListeners();
        noticeRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        mNoticeList = new ArrayList<>();
        mStore.collection("notice").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String title = (String) dc.getData().get("title");
                            String contents = (String) dc.getData().get("contents");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");

                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date);
                            mNoticeList.add(data);
                        }
                        mAdapter = new NoticeAdapter(mNoticeList);
                        mAdapter.notifyDataSetChanged();
                        noticeRecyclerView.setAdapter(mAdapter);
                    }
                });

        if(mUser.equals("1")) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            mUid = user.getUid();
        }else{
            mUid="0";
        }
      //  Toast.makeText(getApplicationContext(), mUid, Toast.LENGTH_SHORT).show();

        fabWrite = (FloatingActionButton) findViewById(R.id.fab_notice_write);
        fabWrite.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                Toast.makeText(getApplicationContext(), mUid, Toast.LENGTH_SHORT).show();
                if (mUid.equals(supervisor)) {
                    Toast.makeText(getApplicationContext(), "글 쓰기", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), WriteNoticeActivity.class));
                } else
                    Toast.makeText(getApplicationContext(), "관리자가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NoticeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}