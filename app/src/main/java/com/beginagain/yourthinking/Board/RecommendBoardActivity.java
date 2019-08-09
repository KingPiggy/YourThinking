package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.beginagain.yourthinking.Adapter.BookBoardAdapter;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.MenuFragment.Menu3Fragment;
import com.beginagain.yourthinking.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecommendBoardActivity  extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public RecyclerView recommendRecyclerView;

    public List<BookBoardItem> mRecommendList;
    public BookBoardAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_recommend);

        recommendRecyclerView = findViewById(R.id.recycler_recommend);

        mRecommendList = new ArrayList<>();
        mStore.collection("board").orderBy("recount", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String title = (String) dc.getData().get("title");
                            String contents = (String) dc.getData().get("contents");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String author = (String) dc.getData().get("author");
                            String booktitle = (String) dc.getData().get("booktitle");

                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle);
                            mRecommendList.add(data);
                        }
                        mAdapter = new BookBoardAdapter(mRecommendList);
                        mAdapter.notifyDataSetChanged();
                        recommendRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecommendBoardActivity.this, MainActivity.class);
        startActivity(intent);
	    finish();
    }
}
