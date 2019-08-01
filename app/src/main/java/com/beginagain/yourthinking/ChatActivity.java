package com.beginagain.yourthinking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.beginagain.yourthinking.Item.ChatDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {
    private String chatRoomName, userName, uid, time;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private ListView mListView;
    private EditText mChatEdit;
    private Button mSendBtn, mExitBtn;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    ArrayAdapter<String> adapter;

    Boolean isChatExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 위젯 ID 참조
        mListView = (ListView) findViewById(R.id.listview_chat);
        mChatEdit = (EditText) findViewById(R.id.edittext_chat);
        mSendBtn = (Button) findViewById(R.id.btn_chat_send);
        mExitBtn = (Button) findViewById(R.id.btn_chat_exit);

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

                time = getTime();

                ChatDTO chat = new ChatDTO(userName, mChatEdit.getText().toString(), uid, time); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(chatRoomName).child("message").push().setValue(chat); // 데이터 푸쉬
                mChatEdit.setText(""); //입력창 초기화

                isChatExist = true;
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());     // 여기서 this는 Activity의 this
                builder.setTitle("채팅에서 퇴장하시겠습니까?")        // 제목 설정
                        .setMessage("지금까지 자신의 채팅 내역이 사라집니다.")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {

                                // 자신의 채팅 삭제
                                final Query myQuery = databaseReference.child("chat").child(chatRoomName).child("message").orderByChild("userName").equalTo(userName);
                                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot myQuery : dataSnapshot.getChildren()) {
                                            myQuery.getRef().removeValue();
                                        }

                                        isChatExist = false;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                // 채팅방 삭제
                                final Query myQuery2 = databaseReference.child("chat").child(chatRoomName).child("message");
                                myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount() == 0 || !isChatExist){
                                            DatabaseReference  deleteRef = databaseReference.child("chat");
                                            deleteRef.child(chatRoomName).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void chatConversation(DataSnapshot dataSnapshot) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        // 채팅 뿌려주는 부분
        adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage() + "\n" + chatDTO.getTime());
        adapter.notifyDataSetChanged();
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
