package com.beginagain.yourthinking.MenuFragment;

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
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookMaratonStatAdapter;
import com.beginagain.yourthinking.Adapter.BookRecommendAdapter;
import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.Collections;
import java.util.List;

public class MenuTop1Fragment  extends Fragment {

    View view;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    MainActivity activity;

    String image, cat;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecommendBookItem> emptyItems = new ArrayList<RecommendBookItem>();
    private BookRecommendAdapter recyclerAdapter = new BookRecommendAdapter(activity, emptyItems, R.layout.fragment_menu_top1);

    private String category;

    int philCount=0, reliCount=0, scoialCount=0, natCount=0, tecCount=0, artCount=0, langCount=0,novelCount=0, hisCount=0, etcCount=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_top1, container, false);

        init();
        final String myUId = user.getUid();
        mStore.collection("maraton")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots){
                            String uid = (String) dc.getData().get("uid");
                            if(myUId.equals(uid)){
                                image = (String) dc.getData().get("image");
                                cat = (String)dc.getData().get("category");
                                if(cat.equals("119")||cat.equals("8516")){
                                    philCount++;
                                }else if(cat.equals("120")||cat.equals("1237")||cat.equals("2")){
                                    reliCount++;
                                }else if(cat.equals("104")||cat.equals("798")||cat.equals("3")){
                                    scoialCount++;
                                }else if(cat.equals("116")||cat.equals("987")||cat.equals("4")){
                                    natCount++;
                                }else if(cat.equals("111")||cat.equals("51095")||cat.equals("5")){
                                    tecCount++;
                                }else if(cat.equals("103")||cat.equals("517")||cat.equals("6")){
                                    artCount++;
                                }else if(cat.equals("115")||cat.equals("1322")||cat.equals("7")){
                                    langCount++;
                                }else if(cat.equals("101")||cat.equals("1")||cat.equals("8")){
                                    novelCount++;
                                }else if(cat.equals("105")||cat.equals("74")||cat.equals("9")){
                                    hisCount++;
                                }else{
                                    etcCount++;
                                }
                            }
                        }
                       Max();
                    }
                });

        return view;
    }
    private void init() {
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }
    private void Max() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(philCount);
        list.add(reliCount);
        list.add(scoialCount);
        list.add(natCount);
        list.add(tecCount);
        list.add(artCount);
        list.add(langCount);
        list.add(novelCount);
        list.add(hisCount);
        list.add(etcCount);
        Integer MAX = Collections.max(list);

        if(MAX==philCount){
            //category
        }else if(MAX==reliCount){

        }else if(MAX==scoialCount){

        }else if(MAX==natCount){

        }else if(MAX==tecCount){

        }else if(MAX==artCount){

        }else if(MAX==langCount){

        }else if(MAX==novelCount){

        }else if(MAX==hisCount){

        }else if(MAX==etcCount){

        }

    }
    private class BookMaratonAsyncTask extends AsyncTask<Void, Void, ArrayList<RecommendBookItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecommendBookItem> doInBackground(Void... params) {
            ArrayList<RecommendBookItem> newItems = new ArrayList<RecommendBookItem>();
                String myKey = "88137ABBC6F43E258E287321232C8A33FF52C15F971D8B66FA5B42B4FBC74016";
                String urlSource = "";
                String outputStyle = "json";
                String str, receiveMsg = "";
                try {
                    urlSource = "http://book.interpark.com/api/bestSeller.api?key=" + myKey;
                    urlSource += "&categoryId=" + "&output=" + outputStyle;

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
                    }else {
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
                        String date= "출판일 : " + bookItem.getString("pubDate");
                        String desc = bookItem.getString("description");

                        RecommendBookItem recommendBookItem = new RecommendBookItem(title, author, publisher, isbn, image,date, desc);
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
                Toast.makeText(getActivity(), "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            emptyItems.clear();
            emptyItems.addAll(newItems);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}

