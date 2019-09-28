package com.beginagain.yourthinking;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beginagain.yourthinking.Adapter.ChatPeopleAdapter;
import com.beginagain.yourthinking.Item.ChatDTO;
import com.beginagain.yourthinking.Item.ChatPeople;
import com.beginagain.yourthinking.Item.ChatRoomItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatRoomPeoples extends AppCompatActivity {

    private String userName, uid; // 파이어베이스 추출
    private Uri profilePhotoUrl;
    private String chatRoomName;
    private int count;

    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private ArrayList<ChatPeople> peopleItems = new ArrayList<ChatPeople>();
    private ChatPeopleAdapter recyclerAdapter;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private TextView mTitle;
    private Button mCloseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_peoples);

        Intent intent = getIntent();
        chatRoomName = intent.getStringExtra("chatRoomName");
        count = intent.getIntExtra("count", 0);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9"));
        }

        mRecyclerView = findViewById(R.id.recycler_room_peoples);
        mTitle = findViewById(R.id.text_view_room_peoples_title);
        mCloseBtn =findViewById(R.id.btn_room_peoples_close);

        // 타이틀 인원 수 설정
        String tmpStr = mTitle.getText().toString();
        tmpStr += " " + count;
        mTitle.setText(tmpStr);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            uid = user.getUid();
            profilePhotoUrl = user.getPhotoUrl();
        }

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        openChat(chatRoomName);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void openChat(String chatName) {
        recyclerAdapter = new ChatPeopleAdapter(getApplicationContext(), peopleItems, R.layout.activity_chat_room_peoples);
        mRecyclerView.setAdapter(recyclerAdapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName).child("people").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getChatRoomPeople(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getChatRoomPeople(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getChatRoomPeople(DataSnapshot dataSnapshot) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        ChatPeople chatPeople = dataSnapshot.getValue(ChatPeople.class);

        peopleItems.add(chatPeople);
        recyclerAdapter.notifyDataSetChanged();
    }
}
