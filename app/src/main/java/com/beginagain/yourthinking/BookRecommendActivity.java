package com.beginagain.yourthinking;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class BookRecommendActivity extends AppCompatActivity {
    Spinner mCategorySpinner;
    TextView mTempTv;

    int categoryNo[] = {100010, 100020, 100030, 100040, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230
            , 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recommend);

        mTempTv = (TextView) findViewById(R.id.temp_recommend);

        mCategorySpinner = (Spinner) findViewById(R.id.spinner_recommend_category);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                mTempTv.setText(item + " : " + categoryNo[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
