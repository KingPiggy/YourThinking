package com.beginagain.yourthinking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MakeChatRoomInfoActivity extends AppCompatActivity {

    EditText mRoomTitleEditText, mBookTitleEditText; // DESC 만들기
    Button mOkayBtn;

    String roomTitle, bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_chat_room_info);

        mRoomTitleEditText = (EditText) findViewById(R.id.edittext_room_info_roomtitle);
        mBookTitleEditText = (EditText) findViewById(R.id.edittext_room_info_booktitle);
        mOkayBtn = (Button) findViewById(R.id.btn_room_info_okay);
        mOkayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomTitle = mRoomTitleEditText.getText().toString();
                bookTitle = mBookTitleEditText.getText().toString();



                finish();
            }
        });
    }


}
