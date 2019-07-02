package com.beginagain.yourthinking;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookRecoAdapter;
import com.beginagain.yourthinking.Item.RecommendBookItem;

import java.util.ArrayList;

public class BookRecommendActivity extends AppCompatActivity {
    Spinner mCategorySpinner;
    TextView mTempTv;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookRecoAdapter recyclerAdapter = new BookRecoAdapter(this, emptyItems, R.layout.activity_main);

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_recommend);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        new JsoupAsyncTask().execute();

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {
            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();
            String title, author, company, isbn, image;
            title = "어린 왕자";
            author = "생떽쥐베리";
            company = "승훈 컴퍼니";
            isbn = "10041004";
            image = "none";
            RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, company, isbn, image);

            newItems.add(recommendBookItem);
            newItems.add(recommendBookItem);
            newItems.add(recommendBookItem);
            newItems.add(recommendBookItem);
            newItems.add(recommendBookItem);
            newItems.add(recommendBookItem);

            return newItems;
        }

        @Override
        protected void onPostExecute(ArrayList<RecommendBookItem> newItems) {
            if (newItems.isEmpty()) {
                Toast.makeText(getApplicationContext(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}
