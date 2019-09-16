package com.beginagain.yourthinking.MenuFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookRecommendAdapter;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Menu1Fragment extends Fragment {

    View view;

    MainActivity activity;

    Spinner mCategorySpinner;
    Button mBestSellerBtn, mNewBooksBtn;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookRecommendAdapter recyclerAdapter = new BookRecommendAdapter(activity, emptyItems, R.layout.fragment_menu1);

    int categoryNo[] = {100, 200, 300, 400};
    int selectedCategory = 100;

    int isBestSeller = 0, isNewBooks = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu1, container, false);
        init();

        isBestSeller = 1; // 초기 시작은 베스트셀러 아이템 보여줌

        mBestSellerBtn.setOnClickListener(menuBtnListener);
        mNewBooksBtn.setOnClickListener(menuBtnListener);

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                selectedCategory = categoryNo[position];
                new BookRecoAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
/**
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        new BookRecoAsyncTask().execute();
 **/
        return view;
    }

    View.OnClickListener menuBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_recommend_bestseller:
                    isBestSeller = 1;
                    isNewBooks = 0;
                    Toast.makeText(activity, "베스트셀러 목록입니다.", Toast.LENGTH_SHORT).show();
                    new BookRecoAsyncTask().execute();
                    break;
                case R.id.btn_recommend_new:
                    isBestSeller = 0;
                    isNewBooks = 1;
                    Toast.makeText(activity, "신간 도서 목록입니다.", Toast.LENGTH_SHORT).show();
                    new BookRecoAsyncTask().execute();
                    break;
            }
        }
    };

    private void init() {
        mBestSellerBtn = (Button) view.findViewById(R.id.btn_recommend_bestseller);
        mNewBooksBtn = (Button) view.findViewById(R.id.btn_recommend_new);
        mCategorySpinner = (Spinner) view.findViewById(R.id.spinner_recommend_category);
       // mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_recommend);
    }

    private class BookRecoAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {

            String myKey = "2824AAAF9F8FBF00CAD4BD88F5C3FB4B45E4DD6DBFD7EDD8332E57AFA7A6708C";

            String urlSource = "";
            String outputStyle = "json"; // 혹은 json

            String str, receiveMsg = "";

            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();

            try {
                if (isBestSeller == 1) {
                    urlSource = "http://book.interpark.com/api/bestSeller.api?key=" + myKey;
                } else if (isNewBooks == 1) {
                    urlSource = "http://book.interpark.com/api/newBook.api?key=" + myKey;
                }

                urlSource += "&categoryId=" + Integer.toString(selectedCategory) + "&output=" + outputStyle;

                URL url = new URL(urlSource);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();
                } else {
                    Toast.makeText(activity, "네트워크 환경이 좋지 않습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("item");

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject bookItem = jarray.getJSONObject(i);

                    String title = bookItem.getString("title");
                    String author = bookItem.getString("author");
                    String publisher = bookItem.getString("publisher");
                    String isbn = "ISBN : " + bookItem.getString("isbn");
                    String image = bookItem.getString("coverLargeUrl");
                    // 쓸만한거 : description : 설명, "priceStandard":가격, "translator":"",

                    RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image);
                    newItems.add(recommendBookItem);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newItems;
        }

        @Override
        protected void onPostExecute(ArrayList<RecommendBookItem> newItems) {
            if (newItems.isEmpty()) {
                Toast.makeText(activity, "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}