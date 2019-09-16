package com.beginagain.yourthinking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText mWriteTitleText, mWriteContentText;
    private Button mBtnUpload;
    private TextView mWriteDateText, mWriteNameText;

    private String id;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    String formatDate = mFormat.format(mDate);

    String mName = user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mWriteNameText = findViewById(R.id.text_view_notice_write_name);
        mWriteDateText = findViewById(R.id.text_view_notice_write_time);
        mWriteTitleText = findViewById(R.id.et_write_notice_title);
        mWriteContentText = findViewById(R.id.et_write_notice_content);
        mBtnUpload = findViewById(R.id.btn_upload_notice);

        mWriteNameText.setText(mName);
        mWriteDateText.setText(formatDate);

        mBtnUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_notice:
                // 생성
                id = mStore.collection("notice").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("id", id);
                post.put("name", mWriteNameText.getText().toString());
                post.put("title", mWriteTitleText.getText().toString());
                post.put("contents", mWriteContentText.getText().toString());
                post.put("date", mWriteDateText.getText().toString());

                // id로 필드이름이 같더라도 구별 가능하게 함
                mStore.collection("notice").document(id).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WriteNoticeActivity.this, "업로드 성공!", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(WriteNoticeActivity.this, NoticeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WriteNoticeActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}