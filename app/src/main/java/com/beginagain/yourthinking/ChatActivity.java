package com.beginagain.yourthinking;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.ChatItemAdapter;
import com.beginagain.yourthinking.Board.BoardResultActivity;
import com.beginagain.yourthinking.Board.MyBoardActivity;
import com.beginagain.yourthinking.Board.SearchBoardActivity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String chatRoomName;
    private String userName, uid; // 파이어베이스 추출
    private Uri profilePhotoUrl;
    private String message;

    private String time;
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("M/d a hh:mm");
    //    private SimpleDateFormat mFormat = new SimpleDateFormat("y/M/d a hh:mm");

    private EditText mChatEdit;
    private Button mSendBtn, mExitBtn;
    private TextView mRoomTitleTextView;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatDTO> chatItems = new ArrayList<ChatDTO>();
    private ChatItemAdapter recyclerAdapter;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private int count;

    String pageNull=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        Intent intent = getIntent();
        chatRoomName = intent.getStringExtra("chatRoomName");
        count = intent.getIntExtra("count", 1);

        mRoomTitleTextView.setText(chatRoomName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            uid = user.getUid();
            profilePhotoUrl = user.getPhotoUrl();
        }

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        // 채팅 방 입장
        openChat(chatRoomName);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = "";
                message = mChatEdit.getText().toString();
                if (message.equals(""))
                    return;

                time = getTime();
                String strPhotoUri = profilePhotoUrl.toString();

                ChatDTO chat = new ChatDTO(userName, message, uid, time, strPhotoUri);
                databaseReference.child("chat").child(chatRoomName).child("message").push().setValue(chat);
                mChatEdit.setText("");

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(uid, uid);
                databaseReference.child("chat").child(chatRoomName).child("people").updateChildren(map);

                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put(chatRoomName, chatRoomName);
                databaseReference.child("mychat").child(uid).updateChildren(map2);
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("채팅에서 퇴장하시겠습니까?")        // 제목 설정
                        .setMessage("지금까지 자신의 채팅 내역이 사라집니다.")        // 메세지 설정
                        .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정
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
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                // 채팅방 삭제
                                final Query myQuery2 = databaseReference.child("chat").child(chatRoomName).child("message").equalTo(uid);
                                myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (count == 1 || count == 0) {
                                            DatabaseReference deleteRef = databaseReference.child("chat");
                                            deleteRef.child(chatRoomName).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                // 참여인원에서 삭제
                                final Query myQuery3 = databaseReference.child("chat").child(chatRoomName).child("people").equalTo(uid);
                                myQuery3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DatabaseReference deleteRef = databaseReference.child("chat").child(chatRoomName).child("people");
                                        deleteRef.child(uid).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                // 참여한 채팅에서 삭제
                                final Query myQuery4 = databaseReference.child("mychat").child(uid).equalTo(chatRoomName);
                                myQuery4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DatabaseReference deleteRef = databaseReference.child("mychat").child(uid);
                                        deleteRef.child(chatRoomName).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("hoon", "onCancelled", databaseError.toException());
                                    }
                                });

                                Toast.makeText(getApplicationContext(), "채팅방이 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
                                pageNull = "Chat";
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("page", pageNull);
                                startActivity(intent);

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

        checkPeopleCount();

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        });
    }

    @Override
    public void onBackPressed() {

        pageNull = "Chat";
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page", pageNull);
        startActivity(intent);

        finish();
    }

    private void init() {
        // 위젯 ID 참조
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mChatEdit = (EditText) findViewById(R.id.edittext_chat);
        mSendBtn = (Button) findViewById(R.id.btn_chat_send);
        mExitBtn = (Button) findViewById(R.id.btn_chat_exit);
        mRoomTitleTextView = (TextView)findViewById(R.id.text_view_chat_room_title);
    }

    private int checkPeopleCount() {
        for (ChatDTO c : chatItems) {

        }

        return 0;
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void openChat(String chatName) {
        recyclerAdapter = new ChatItemAdapter(getApplicationContext(), chatItems, R.layout.activity_chat);
        mRecyclerView.setAdapter(recyclerAdapter);

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

    private void chatConversation(DataSnapshot dataSnapshot) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);

        chatItems.add(chatDTO);
        recyclerAdapter.notifyDataSetChanged();
    }

}