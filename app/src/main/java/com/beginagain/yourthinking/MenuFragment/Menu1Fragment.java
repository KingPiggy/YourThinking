package com.beginagain.yourthinking.MenuFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookRecommendAdapter;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Menu1Fragment extends Fragment {

    View view;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    MainActivity activity;

    Spinner mCategorySpinner, mSiteSpinner, mSearchSpinner;
    Button mBestSellerBtn, mNewBooksBtn;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookRecommendAdapter recyclerAdapter = new BookRecommendAdapter(activity, emptyItems, R.layout.fragment_menu1);

    int intetpark_CategoryNo[] = {119, 120, 104, 116, 111, 103, 115, 101, 105};
    int aladin_CategoryNo[] = {8516, 1237, 798, 987, 51095, 517, 1322, 1, 74};
    int lib_CategoryNo[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    int interparkCategory = 119;
    int aladinCategory = 8516;
    int libCategory = 1;

    int flag = 0;
    int searchFlag = 0;

    int isBestSeller = 0, isNewBooks = 0;

    private Context context;

    EditText editBookSearch;
    private ImageButton btnBookSearch;
    String spinnercount = "0";
    String text;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formatDate = mFormat.format(mDate);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu1, container, false);
        context = container.getContext();
        init();

        isBestSeller = 1; // 초기 시작은 베스트셀러 아이템 보여줌


        mSiteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos == 0) { // 인터파크
                    flag = 0;
                    isBestSeller = 1;
                    isNewBooks = 0;
                    mCategorySpinner.setSelection(0);
                    mNewBooksBtn.setText("새로나온 책");
                    new BookRecoAsyncTask().execute();
                } else if (pos == 1) { // 알라딘
                    flag = 1;
                    mCategorySpinner.setSelection(0);
                    mNewBooksBtn.setText("새로나온 책");
                    new BookRecoAsyncTask().execute();
                } else if (pos == 2) { // 공공도서관
                    flag = 2;
                    mCategorySpinner.setSelection(0);
                    mNewBooksBtn.setText("스테디 셀러");
                    new BookRecoAsyncTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (flag == 0) {
                    interparkCategory = intetpark_CategoryNo[pos];
                } else if (flag == 1) {
                    aladinCategory = aladin_CategoryNo[pos];
                } else if (flag == 2){
                    libCategory = lib_CategoryNo[pos];
                }
                new BookRecoAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                if (position == 0) {
                    spinnercount="0";
                } else if (position == 1) {
                    spinnercount="1";
                } else if (position == 2) {
                    spinnercount="2";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        new BookRecoAsyncTask().execute();

        return view;
    }

    View.OnClickListener menuBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_recommend_bestseller:
                    isBestSeller = 1;
                    isNewBooks = 0;
                    mBestSellerBtn.setSelected(true);
                    mNewBooksBtn.setSelected(false);
                    mBestSellerBtn.setTextColor(Color.BLACK);
                    mNewBooksBtn.setTextColor(Color.GRAY);
                    new BookRecoAsyncTask().execute();
                    break;
                case R.id.btn_recommend_new:
                    isBestSeller = 0;
                    isNewBooks = 1;
                    mBestSellerBtn.setSelected(false);
                    mNewBooksBtn.setSelected(true);
                    mBestSellerBtn.setTextColor(Color.GRAY);
                    mNewBooksBtn.setTextColor(Color.BLACK);
                    new BookRecoAsyncTask().execute();
                    break;
            }
        }
    };

    private void init() {
        mBestSellerBtn = (Button) view.findViewById(R.id.btn_recommend_bestseller);
        mNewBooksBtn = (Button) view.findViewById(R.id.btn_recommend_new);
        mCategorySpinner = (Spinner) view.findViewById(R.id.spinner_recommend_category);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_recommend);
        mSiteSpinner = (Spinner) view.findViewById(R.id.spinner_site_category);
        editBookSearch = (EditText) view.findViewById(R.id.et_book_search);
        btnBookSearch = (ImageButton) view.findViewById(R.id.btn_book_search_data);
        mSearchSpinner = (Spinner)view.findViewById(R.id.spinner_book_search);

        mBestSellerBtn.setSelected(true);

        btnBookSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = editBookSearch.getText().toString();
                if (editBookSearch.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    switch (spinnercount) {
                        case "0":
                            flag = 0;
                            searchFlag = 0;
                            isBestSeller = 2;
                            isNewBooks = 2;
                            new BookRecoAsyncTask().execute();

                            break;
                        case "1":
                            flag = 0;
                            searchFlag = 1;
                            isBestSeller = 2;
                            isNewBooks = 2;
                            new BookRecoAsyncTask().execute();

                            break;
                        case "2":
                            flag = 0;
                            searchFlag = 2;
                            isBestSeller = 2;
                            isNewBooks = 2;
                            new BookRecoAsyncTask().execute();

                            break;
                    }
                }
            }
        });


        mCategorySpinner.setSelection(0);
        mBestSellerBtn.setOnClickListener(menuBtnListener);
        mNewBooksBtn.setOnClickListener(menuBtnListener);

    }

    private class BookRecoAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {
            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();
            if (flag == 0) { // 인터파크 도서 검색
                String myKey = "2824AAAF9F8FBF00CAD4BD88F5C3FB4B45E4DD6DBFD7EDD8332E57AFA7A6708C";
                String urlSource = "";
                String outputStyle = "json";
                String str, receiveMsg = "";
                try {

                    if (isBestSeller == 1) {
                        urlSource = "http://book.interpark.com/api/bestSeller.api?key=" + myKey;
                        urlSource += "&categoryId=" + Integer.toString(interparkCategory) + "&output=" + outputStyle;
                    } else if (isNewBooks == 1) {
                        urlSource = "http://book.interpark.com/api/newBook.api?key=" + myKey;
                        urlSource += "&categoryId=" + Integer.toString(interparkCategory) + "&output=" + outputStyle;
                    } else if (searchFlag == 0) {
                        urlSource += "http://book.interpark.com/api/search.api?key=" + myKey + "&query=" + text+"&maxResults=50";
                        urlSource += "&output=" + outputStyle;
                    }else if (searchFlag == 1) {
                        urlSource += "http://book.interpark.com/api/search.api?key=" + myKey + "&query=" + text+"&queryType=author"+"&maxResults=50";
                        urlSource += "&output=" + outputStyle;
                    }else if (searchFlag == 2) {
                        urlSource += "http://book.interpark.com/api/search.api?key=" + myKey + "&query=" + text+"&queryType=publisher"+"&maxResults=50";
                        urlSource += "&output=" + outputStyle;
                    }
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
                        String author = "저자 : " + bookItem.getString("author");
                        String publisher = "출판사 : " + bookItem.getString("publisher");
                        String isbn = "ISBN : " + bookItem.getString("isbn");
                        String image = bookItem.getString("coverLargeUrl");
                        String date = "출판일 : " + bookItem.getString("pubDate");
                        String desc = bookItem.getString("description");

                        RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image, date, desc);
                        newItems.add(recommendBookItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (flag == 1) { // 알라딘 도서검색
                String AladinMyKey = "ttbsmallman08101505001";
                String urlSource = "";
                String outputStyle = "&Output=JS";
                String query = "&QueryType=";
                String str, receiveMsg = "";

                try {
                    urlSource = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=" + AladinMyKey + query;
                    if (isBestSeller == 1) {
                        urlSource += "Bestseller";
                    } else if (isNewBooks == 1) {
                        urlSource += "ItemNewAll";
                    }
                    urlSource += "&MaxResults=30&start=1" + "&SearchTarget=Book" + outputStyle + "&CategoryId=" + Integer.toString(aladinCategory) + "&Version=20131101";

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
                        String author = "저자 : " + bookItem.getString("author");
                        String publisher = "출판사 : " + bookItem.getString("publisher");
                        String isbn = "ISBN : " + bookItem.getString("isbn");
                        String image = bookItem.getString("cover");
                        String date = "출판일 : " + bookItem.getString("pubDate");
                        String desc = bookItem.getString("description");
                        if (publisher.equals("알라딘 이벤트")) {

                        } else {
                            RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image, date, desc);
                            newItems.add(recommendBookItem);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (flag == 2) {
                String LibMyKey = "cc993dfdb2bc12abfdad42caf1f79c39b8d59925ec7d53f2f49230cd81e39574";
                String urlSource = "";
                String outputStyle = "&format=json";
                String query = "&startDt=2019-01-01&endDt=";
                String query_steady = "&startDt=2000-01-01&endDt=";
                String top30 = "&pageNo=1&pageSize=30";
                String str, receiveMsg = "";

                try {

                    if (isBestSeller == 1) {
                        urlSource = "http://www.data4library.kr/api/loanItemSrch?authKey=" + LibMyKey + query + formatDate + top30;
                        urlSource += "&kdc=" + Integer.toString(libCategory) + outputStyle;
                    } else if (isNewBooks == 1) {
                        //   urlSource =  "http://data4library.kr/api/hotTrend?authKey=" + LibMyKey+"&searchDt="+formatDate;
                        //  urlSource+=outputStyle;
                        urlSource = "http://www.data4library.kr/api/loanItemSrch?authKey=" + LibMyKey + query_steady + formatDate + top30;
                        urlSource += "&kdc=" + Integer.toString(libCategory) + outputStyle;
                    }


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

                    JSONArray jarray = new JSONObject(receiveMsg).getJSONObject("response").getJSONArray("docs");
                    String image;
                    for (int i = 0; i < jarray.length(); i++) {

                        JSONObject bookItem = jarray.getJSONObject(i).getJSONObject("doc");

                        String title = bookItem.getString("bookname");
                        String author = "저자 : " + bookItem.getString("authors");
                        String publisher = "출판사 : " + bookItem.getString("publisher");
                        String isbn = "ISBN13 : " + bookItem.getString("isbn13");
                        if (bookItem.get("bookImageURL").equals("")) {
                            image = "0";
                        } else {
                            image = bookItem.getString("bookImageURL");
                        }
                        String date = "출판일 : " + bookItem.getString("publication_year");
                        String desc = "도서관에서 제공을 하지 않습니다.";

                        RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image, date, desc);
                        newItems.add(recommendBookItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return newItems;
        }

        @Override
        protected void onPostExecute(ArrayList<RecommendBookItem> newItems) {
            if (newItems.isEmpty()) {
                Toast.makeText(getActivity(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

}