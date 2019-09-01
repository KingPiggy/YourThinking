package com.beginagain.yourthinking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beginagain.yourthinking.Item.ChatRoomItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MakeChatRoomActivity extends AppCompatActivity {

    EditText mRoomTitleEditText, mBookTitleEditText, mRoomDescEditText; // DESC 만들기
    Button mOkayBtn, mCancelBtn;

    String roomTitle, bookTitle, desc;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_chat_room);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        init();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mRoomTitleEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mBookTitleEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mBookTitleEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mRoomDescEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mRoomDescEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mRoomDescEditText.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });



        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mOkayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomTitle = mRoomTitleEditText.getText().toString();
                bookTitle = mBookTitleEditText.getText().toString();
                desc = mRoomDescEditText.getText().toString();

                final Query myQuery2 = databaseReference.child("chat").child(roomTitle);
                myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Toast.makeText(getApplicationContext(), "중복된 채팅방 이름을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("roomTitle", roomTitle);
                            map.put("bookTitle", bookTitle);
                            map.put("desc", desc);
                            databaseReference.child("chat").child(roomTitle).child("info").updateChildren(map);

                            Toast.makeText(getApplicationContext(), "목록이 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
                            String pageNull = "Chat";
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("page", pageNull);
                            startActivity(intent);

                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("hoon", "onCancelled", databaseError.toException());
                    }
                });


            }
        });
    }

    private void init() {
        mRoomTitleEditText = (EditText) findViewById(R.id.edittext_make_chatroom_roomtitle);
        mBookTitleEditText = (EditText) findViewById(R.id.edittext_make_chatroom_booktitle);
        mRoomDescEditText = (EditText) findViewById(R.id.edittext_make_chatroom_desc);
        mCancelBtn = (Button) findViewById(R.id.btn_make_chatroom_cancel);
        mOkayBtn = (Button) findViewById(R.id.btn_make_chatroom_okay);
    }
}