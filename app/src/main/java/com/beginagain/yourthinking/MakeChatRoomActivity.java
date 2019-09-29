package com.beginagain.yourthinking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private Button mOkayBtn;
    private TextView mTitleText;
    private EditText mEditText;

    private String roomTitle, bookTitle, desc;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private int makeSeq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_chat_room);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        init();

        makeSeq = 1;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_make_chat_room);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    switch (makeSeq) {
                        case 1:
                            if (mEditText.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            roomTitle = mEditText.getText().toString();
                            mTitleText.setText("책 제목을 입력해주세요.");
                            mEditText.setText("");
                            makeSeq++;
                            break;
                        case 2:
                            if (mEditText.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            bookTitle = mEditText.getText().toString();
                            mTitleText.setText("채팅방 소개를 해주세요.");
                            mEditText.setText("");
                            mOkayBtn.setText("완료");
                            makeSeq++;
                            break;
                        case 3:
                            desc = mEditText.getText().toString();
                            mEditText.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                            makeChatRoom();
                            break;
                    }

                    return true;
                }
                return false;
            }
        });

        mOkayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (makeSeq) {
                    case 1:
                        if (mEditText.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        roomTitle = mEditText.getText().toString();
                        mTitleText.setText("책 제목을 입력해주세요.");
                        mEditText.setText("");
                        makeSeq++;
                        break;
                    case 2:
                        if (mEditText.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        bookTitle = mEditText.getText().toString();
                        mTitleText.setText("채팅방 소개를 해주세요.");
                        mEditText.setText("");
                        makeSeq++;
                        break;
                    case 3:
                        desc = mEditText.getText().toString();
                        mEditText.setText("");
                        mOkayBtn.setText("완료");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                        makeChatRoom();
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (makeSeq) {
            case 1:
                // 제목 입력 화면에서 Back
                finish();
                break;
            case 2:
                // 주제(책) 입력 화면에서 Back
                mTitleText.setText("채팅방 제목을 입력해주세요.");
                mEditText.setText("");
                makeSeq--;
                break;
            case 3: //소개 화면에서 Back
                mTitleText.setText("책 제목을 입력해주세요.");
                mEditText.setText("");
                mOkayBtn.setText("다음");
                makeSeq--;
                break;
        }
    }

    private void init() {
        mTitleText = (TextView) findViewById(R.id.text_view_make_chat_room_q);
        mEditText = (EditText) findViewById(R.id.edit_text_make_chat_room_a);
        mOkayBtn = (Button) findViewById(R.id.btn_make_chat_room_exec);
    }

    private void makeChatRoom() {

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
}