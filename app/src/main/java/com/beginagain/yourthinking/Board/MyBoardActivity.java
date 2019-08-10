package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.beginagain.yourthinking.Adapter.BoardMyAdapter;
import com.beginagain.yourthinking.Adapter.BookBoardAdapter;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
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

import javax.annotation.Nullable;

public class MyBoardActivity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView mSearchRecyclerView;
    public BoardMyAdapter mAdapter;
    private List<BookBoardItem> mSearchList;

    String mName = user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        mSearchRecyclerView = findViewById(R.id.recycler_search);

        mSearchList= new ArrayList<>();

        mStore.collection("board")
                .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                // 실시간 조회하려고 스냅 사용
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String title = (String) dc.getData().get("title");
                            String contents = (String) dc.getData().get("contents");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String author = (String)dc.getData().get("author"); // 8.3
                            String booktitle = (String)dc.getData().get("booktitle"); // 8.3

                            if (mName.equals(name)){
                                BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle);
                                mSearchList.add(data);
                            }
                        }
                        mAdapter = new BoardMyAdapter(mSearchList);
                        mSearchRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        //super.onBackPressed();
    }
}

