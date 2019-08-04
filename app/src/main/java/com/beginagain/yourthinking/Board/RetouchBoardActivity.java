package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RetouchBoardActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private String name, title, contents, date, id ;
    private EditText mTitle, mContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_retouch);

        mTitle = findViewById(R.id.et_retouch_title);
        mContents = findViewById(R.id.et_retouch_content);

        Intent intent = getIntent();
        id = intent.getStringExtra("Id");
        name = intent.getStringExtra("Name");
        title = intent.getStringExtra("Title");
        contents = intent.getStringExtra("Contents");
        date = intent.getStringExtra("Date");

        mTitle.setText(title);
        mContents.setText(contents);

        findViewById(R.id.btn_retouch_board).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mStore.collection("board").document(id)
                .update("title" , mTitle.getText().toString(), "contents",mContents.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RetouchBoardActivity.this, "수정완료!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RetouchBoardActivity.this, BookBoardActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RetouchBoardActivity.this, "수정실패!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
