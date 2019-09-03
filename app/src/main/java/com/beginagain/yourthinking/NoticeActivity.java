package com.beginagain.yourthinking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BoardRecommendAdapter;
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

public class NoticeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public RecyclerView noticeRecyclerView;

    public List<BookBoardItem> mNoticeList;
    public NoticeAdapter mAdapter;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabAdd, fabWrite;

    String mUid = user.getUid();
    String supervisor = "dkllRndBmVgPrJlrCl9WLIvqixN2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        noticeRecyclerView = findViewById(R.id.recycler_notice);

        mNoticeList= new ArrayList<>();
        mStore.collection("notice").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
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
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabWrite = (FloatingActionButton) findViewById(R.id.fab_write);
        fabAdd.setOnClickListener(this);
        fabWrite.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_add:
                anim();
                break;
            case R.id.fab_write:
                anim();
                if(mUid.equals(supervisor)) {
                    Toast.makeText(getApplicationContext(), "글 쓰기", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), WriteNoticeActivity.class));
                }else
                    Toast.makeText(getApplicationContext(), "관리자가 아닙니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void anim() {
        if (isFabOpen) {
            fabWrite.startAnimation(fab_close);
            fabWrite.setClickable(false);
            isFabOpen = false;
        } else {
            fabWrite.startAnimation(fab_open);
            fabWrite.setClickable(true);
            isFabOpen = true;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NoticeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
