package com.beginagain.yourthinking;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookMaratonActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    final String PREFNAME = "Preferences";
    Button mDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maraton);

        mDoneBtn = (Button) findViewById(R.id.btn_make_maraton_done);
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
                editor = settings.edit();
                editor.putBoolean("isMaratonGoing", true);
                editor.apply();
                finish();
            }
        });
    }


}
