package com.beginagain.yourthinking;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class BookRecommendActivity extends AppCompatActivity {

    TextView temp;

    String bookInfo1  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recommend);


        Log.d("hoon", "어싱크 실행 전");

        new JsoupAsyncTask().execute();

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
