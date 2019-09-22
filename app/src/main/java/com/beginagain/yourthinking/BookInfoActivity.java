package com.beginagain.yourthinking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.beginagain.yourthinking.Board.WriteActivity;
import com.squareup.picasso.Picasso;

public class BookInfoActivity  extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView mImage;
    private Button mButton;
    private TextView mTitle, mAuthor, mPublisher, mDate, mIsbn, mDesc;
    private String author, image, title, isbn, publisher, desc, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        init();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookInfoActivity.this, WriteActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void init(){

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar_book_info);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        author = intent.getStringExtra("Author");
        image = intent.getStringExtra("Image");
        title = intent.getStringExtra("Title");
        isbn = intent.getStringExtra("Isbn");
        publisher = intent.getStringExtra("Publisher");
        date = intent.getStringExtra("Date");
        desc = intent.getStringExtra("Desc");

        mTitle = findViewById(R.id.text_view_info_title);
        mAuthor = findViewById(R.id.text_view_info_author);
        mPublisher = findViewById(R.id.text_view_info_publisher);
        mDate = findViewById(R.id.text_view_info_date);
        mIsbn = findViewById(R.id.text_view_info_isbn);
        mImage=findViewById(R.id.iv_info_image);
        mDesc=findViewById(R.id.text_view_info_desc);
        mButton=findViewById(R.id.btn_move_board);

        mTitle.setText(title);
        mAuthor.setText(author);
        mPublisher.setText(publisher);
        mIsbn.setText(isbn);
        mDate.setText(date);
        mDesc.setText(desc);
        Picasso.get().load(image).into(mImage);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                break;
        }
        return false;
    }
}
