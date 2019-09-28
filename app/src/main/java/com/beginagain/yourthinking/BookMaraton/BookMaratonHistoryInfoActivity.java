package com.beginagain.yourthinking.BookMaraton;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookMaratonPageAdapter;
import com.beginagain.yourthinking.Board.WriteActivity;
import com.beginagain.yourthinking.Item.MaratonPageItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class BookMaratonHistoryInfoActivity extends AppCompatActivity {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Toolbar mToolbar;
    private ImageView mImage;
    private TextView mTitle, mAuthor, mDate, mTotalPage, mDoing;
    private EditText mReadPage, mPointPage;
    private Button mMoveBoard;
    private ImageButton mBtnMakeHistory;
    private RecyclerView mRecyclerView;
    private int count =0;

    private String id, userUid, title, date, image, author, category, totalpage, readpage, page,rid, publisher, isbn, pubDate;

    long mNow = System.currentTimeMillis();
    String pageNull=null;
    Date mReDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    String formatDate = mFormat.format(mReDate);

    private BookMaratonPageAdapter mAdapter;
    private List<MaratonPageItem> mMaratonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton_history_info);

        init();
        setStore();

    }
    private void init(){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        id= intent.getStringExtra("Id");
        userUid = intent.getStringExtra("User");
        title = intent.getStringExtra("Title");
        date = intent.getStringExtra("Date");
        image = intent.getStringExtra("Image");
        author = intent.getStringExtra("Author");
        category = intent.getStringExtra("Category");
        totalpage = intent.getStringExtra("TotalPage");
        readpage = intent.getStringExtra("ReadPage");
        publisher =intent.getStringExtra("Company");
        isbn = intent.getStringExtra("Isbn");
        page = intent.getStringExtra("Page");
        pubDate = intent.getStringExtra("pubDate");

        mToolbar = findViewById(R.id.toolbar_maraton_info);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = findViewById(R.id.iv_maraton_info_image);
        mTitle = findViewById(R.id.text_view_maraton_info_title);
        mAuthor = findViewById(R.id.text_view_maraton_info_author);
        mDate = findViewById(R.id.text_view_maraton_info_date);
        mDoing = findViewById(R.id.text_view_maraton_info_doing);
        mTotalPage = findViewById(R.id.text_view_maraton_info_totalpage);
        mBtnMakeHistory = findViewById(R.id.btn_maraton_info_make_history);
        mReadPage = findViewById(R.id.et_maraton_info_readpage);
        mPointPage = findViewById(R.id.et_maraton_info_point);

        mRecyclerView = findViewById(R.id.recycler_maraton_info_date);
        mRecyclerView.clearOnChildAttachStateChangeListeners();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplication(),1));
        mMaratonList = new ArrayList<>();
        mMoveBoard = findViewById(R.id.btn_maraton_info_move_board);
        mMoveBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookMaratonHistoryInfoActivity.this, WriteActivity.class);
                intent.putExtra("Flag", "1");
                intent.putExtra("Title", title);
                intent.putExtra("User", user);
                intent.putExtra("Date",date);
                intent.putExtra("Image", image);
                intent.putExtra("Author",author);
                intent.putExtra("Category", category);
                intent.putExtra("Company", publisher);
                intent.putExtra("Isbn",isbn);
                intent.putExtra("pubDate",pubDate);
                startActivity(intent);
            }
        });

        mBtnMakeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReadPage.getText().toString() != null) {
                    rid = mStore.collection("maraton").document(id).collection("page").document().getId();
                    Map<String, Object> post = new HashMap<>();
                    post.put("id", rid);
                    post.put("page", mReadPage.getText().toString());
                    post.put("date", formatDate);
                    post.put("uid", user.getUid());
                    post.put("point", mPointPage.getText().toString());

                    int readP = Integer.parseInt(mReadPage.getText().toString());
                    int totalP = Integer.parseInt(totalpage);

                    if(readP>totalP) {
                        Toast.makeText(BookMaratonHistoryInfoActivity.this, "올바른 페이지를 적어주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        mStore.collection("maraton").document(id)
                                .collection("page").document(rid).set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(BookMaratonHistoryInfoActivity.this, "페이지입력완료", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent intent = new Intent(BookMaratonHistoryInfoActivity.this, MainActivity.class);
                                        intent.putExtra("page", page);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                } else {
                    Toast.makeText(BookMaratonHistoryInfoActivity.this, "페이지를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTitle.setText(title);
        mAuthor.setText(author);
        mDate.setText(date);
        mTotalPage.setText(totalpage+"페이지");
        Picasso.get().load(image).into(mImage);

    }
    private void setStore(){
        mStore.collection("maraton").document(id)
                .collection("page").orderBy("date").limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots){
                            String rid = (String) dc.getData().get("id");
                            String page = (String) dc.getData().get("page");
                            String date = (String) dc.getData().get("date");
                            String uid = (String) dc.getData().get("uid");
                            String point = (String)dc.getData().get("point");

                            MaratonPageItem data = new MaratonPageItem(rid, date, page, uid, point);
                            mMaratonList.add(data);
                            if(page.equals(totalpage)) {
                                count ++;
                            }
                            mDoing.setText(count+"회독 진행 완료");
                            mDoing.setTextColor(Color.GREEN);
                        }
                        mRecyclerView.removeAllViews();
                        mAdapter = new BookMaratonPageAdapter(mMaratonList, getApplicationContext());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
}
