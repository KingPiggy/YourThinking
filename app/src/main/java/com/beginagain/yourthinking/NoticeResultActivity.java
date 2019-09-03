package com.beginagain.yourthinking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.ReplyAdapter;
import com.beginagain.yourthinking.Item.BookReplyItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class NoticeResultActivity extends AppCompatActivity implements View.OnClickListener{

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TextView mName, mTitle, mContents, mDate;
    private String name, title, contents, date, id;
    private String rid; // 8.4
    private ImageView mImage;

    private EditText mReplyContentText;
    private TextView mReplyNameText, mRecommnedCount;

    private RecyclerView mReplyRecyclerView;
    private ReplyAdapter mAdapter;

    private Uri replyImage;

    private List<BookReplyItem> mReplyList; // private static 기존

    long mNow = System.currentTimeMillis();
    Date mReDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    String formatDate = mFormat.format(mReDate);

    String mReName = user.getDisplayName();
    String mReplyImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_result);

        replyImage = user.getPhotoUrl();
        mReplyImage = replyImage.toString();

        mReplyRecyclerView = findViewById(R.id.recycler_notice_Reply);
        mReplyList = new ArrayList<>();

        mReplyNameText = (TextView)findViewById(R.id.text_view_notice_reply_name);
        mReplyContentText = (EditText)findViewById(R.id.et_notice_reply_contents);
        mRecommnedCount = (TextView)findViewById(R.id.text_view_notice_result_recommend);

        mReplyNameText.setText(mReName);

        mName = (TextView)findViewById(R.id.text_view_notice_result_name);
        mTitle = (TextView)findViewById(R.id.text_view_notice_result_title);
        mContents = (TextView)findViewById(R.id.text_view_notice_result_contents);
        mDate = (TextView)findViewById(R.id.text_view_notice_result_date);

        Intent intent = getIntent();
        id = intent.getStringExtra("Id");
        name = intent.getStringExtra("Name");
        title = intent.getStringExtra("Title");
        contents = intent.getStringExtra("Contents");
        date = intent.getStringExtra("Date");

        mName.setText(name);
        mTitle.setText(title);
        mContents.setText(contents);
        mDate.setText(date);

        mStore.collection("notice").document(id)
                .collection("reply").orderBy("date").limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots){
                            String rid = (String) dc.getData().get("rid");
                            String name = (String) dc.getData().get("name");
                            String contents = (String) dc.getData().get("contents");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String uid = (String) dc.getData().get("uid");

                            BookReplyItem data = new BookReplyItem(rid, name, contents, date, image, uid);

                            mReplyList.add(data);
                        }
                        mAdapter = new ReplyAdapter(mReplyList, getApplicationContext());
                        mReplyRecyclerView.setAdapter(mAdapter);
                    }
                });
        mStore.collection("notice").document(id).collection("recommend")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        int recount = 0;
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            recount++;
                        }
                        mRecommnedCount.setText("추천 "+recount);
                        mStore.collection("notice").document(id).update("recount",recount);
                    }
                });
        findViewById(R.id.btn_notice_reply).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_notice_reply:
                rid = mStore.collection("notice").document(id).collection("reply").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("id", rid);
                post.put("name", mReplyNameText.getText().toString());
                post.put("contents", mReplyContentText.getText().toString());
                post.put("date", formatDate);
                post.put("image", mReplyImage);

                mStore.collection("notice").document(id)
                        .collection("reply").document(rid).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(NoticeResultActivity.this, "댓글 성공!", Toast.LENGTH_SHORT).show();
                                // Intent reintent = new Intent(BoardResultActivity.this, BoardResultActivity.class);
                                // startActivity(reintent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NoticeResultActivity.this, "댓글 실패!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
        finish();
    }
}