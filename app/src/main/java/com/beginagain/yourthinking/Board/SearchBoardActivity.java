package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BoardSearchAdapter;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchBoardActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView searchRecyclerView;
    public BoardSearchAdapter mAdapter;
    private List<BookBoardItem> mSearchList;
    EditText editSearch;
    private ImageButton btnSearch;
    String text;
    String spinnercount =null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_search);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchRecyclerView=findViewById(R.id.recycler_search_user);
        searchRecyclerView.clearOnChildAttachStateChangeListeners();
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(getApplication(), 1));
        editSearch = (EditText)findViewById(R.id.et_search);
        btnSearch = (ImageButton)findViewById(R.id.btn_search_data);

        Spinner spinner = (Spinner)findViewById(R.id.spinner_search);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.search,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnercount = Integer.toString(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        text = editSearch.getText().toString();
        // Toast.makeText(getApplicationContext(),spinnercount, Toast.LENGTH_SHORT).show();
        if(editSearch.getText().toString().length()==0){
            Toast.makeText(SearchBoardActivity.this,"아무것도 없어요",Toast.LENGTH_SHORT).show();
        }else{
            mSearchList = new ArrayList<>();
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

                                switch (spinnercount) {
                                    case "0":
                                        if(text.toLowerCase().contains(name)){
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else if(name.toLowerCase().contains(text)) {
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "찾을수가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case "1":
                                        if(text.toLowerCase().contains(title)){
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else if(title.toLowerCase().contains(text)) {
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "찾을수가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case "2":
                                        if(text.toLowerCase().contains(contents)){
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else if(contents.toLowerCase().contains(text)) {
                                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                            mSearchList.add(data);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "찾을수가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                            mAdapter = new BoardSearchAdapter(mSearchList);
                            searchRecyclerView.setAdapter(mAdapter);
                        }
                    });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page","Board");
        startActivity(intent);
        finish();
    }
}