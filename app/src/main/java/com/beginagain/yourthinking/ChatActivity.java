package com.beginagain.yourthinking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.beginagain.yourthinking.Item.ChatDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {
    private String chatRoomName, userName, uid;
    private String chat_user;
    private String chat_message;

    private ListView mListView;
    private EditText mChatEdit;
    private Button mSendBtn;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 위젯 ID 참조
        mListView = (ListView) findViewById(R.id.listview_chat);
        mChatEdit = (EditText) findViewById(R.id.edittext_chat);
        mSendBtn = (Button) findViewById(R.id.btn_chat_send);

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        Intent intent = getIntent();
        chatRoomName = intent.getStringExtra("chatRoomName");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            uid = user.getUid();
        }

        Log.d("hoon", "네임 : " + userName + " uID : " + uid);

        // 채팅 방 입장
        openChat(chatRoomName);

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChatEdit.getText().toString().equals(""))
                    return;

                ChatDTO chat = new ChatDTO(userName, mChatEdit.getText().toString(), uid); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(chatRoomName).child("message").push().setValue(chat); // 데이터 푸쉬
                mChatEdit.setText(""); //입력창 초기화

            }
        });
    }

    private void chatConversation(DataSnapshot dataSnapshot) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage());
        adapter.notifyDataSetChanged();
        Log.d("hoon", "스냅샷 toString : " + dataSnapshot.toString());
        //Log.d("hoon", "겟밸류 : " + dataSnapshot.getValue(ChatDTO.class) );
    }

    private void openChat(String chatName) {
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        // 리스트 어댑터 생성 및 세팅
        mListView.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
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
}
