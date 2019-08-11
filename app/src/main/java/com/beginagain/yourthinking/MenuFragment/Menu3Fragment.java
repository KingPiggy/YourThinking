package com.beginagain.yourthinking.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookBoardAdapter;
import com.beginagain.yourthinking.Board.SearchBoardActivity;
import com.beginagain.yourthinking.Board.WriteActivity;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu3Fragment extends Fragment implements View.OnClickListener {
    View view;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mMainRecyclerView;
    private BookBoardAdapter mAdapter;
    private List<BookBoardItem> mBoardList = null;
    private List<BookBoardItem> bbList = null; // 89

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabAdd, fabSearch, fabWrite, fabRec;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu3, container, false);

        activity.onFragmentChange(3);

        init();
        // mBoardList.clear();
        fab_open = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);

        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabSearch = (FloatingActionButton) view.findViewById(R.id.fab_search);
        fabWrite = (FloatingActionButton) view.findViewById(R.id.fab_write);
        fabRec = (FloatingActionButton)view.findViewById(R.id.fab_recommend);

        fabAdd.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
        fabWrite.setOnClickListener(this);
        fabRec.setOnClickListener(this);

        return view;

    }

    void init(){

        mMainRecyclerView = view.findViewById(R.id.recycler_Board);
        mBoardList = new ArrayList<>();
        bbList = new ArrayList<>();
        // mBoardList.clear();
        mMainRecyclerView.clearOnChildAttachStateChangeListeners();

        mStore.collection("board")
                .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                // 실시간 조회하려고 스냅 사용
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String title = (String) dc.getData().get("title");
                            String contents = (String) dc.getData().get("contents");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String author = (String)dc.getData().get("author"); // 8.3
                            String booktitle = (String)dc.getData().get("booktitle"); // 8.3

                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle);
                            mBoardList.add(data);
                        }
                        mAdapter = new BookBoardAdapter(mBoardList);
                        mAdapter.notifyDataSetChanged();
                        mMainRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
    public void refresh(){
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_add:
                anim();
                break;
            case R.id.fab_search:
                anim();
                Toast.makeText(getActivity(), "검색", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), SearchBoardActivity.class));
                getActivity().isDestroyed();
                break;
            case R.id.fab_write:
                anim();
                Toast.makeText(getActivity(), "글 쓰기", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), WriteActivity.class));
                getActivity().isDestroyed();
                break;
            case R.id.fab_recommend:
                anim();
                mBoardList.clear();
                mBoardList = new ArrayList<>();
                mStore.collection("board").orderBy("recount", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                    String id = (String) dc.getData().get("id");
                                    String title = (String) dc.getData().get("title");
                                    String contents = (String) dc.getData().get("contents");
                                    String name = (String) dc.getData().get("name");
                                    String date = (String) dc.getData().get("date");
                                    String image = (String) dc.getData().get("image");
                                    String author = (String) dc.getData().get("author");
                                    String booktitle = (String) dc.getData().get("booktitle");

                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle);
                                    mBoardList.add(data);
                                }
                                mAdapter = new BookBoardAdapter(mBoardList);
                                mAdapter.notifyDataSetChanged();
                                mMainRecyclerView.setAdapter(mAdapter);
                            }
                        });
                break;
        }
    }
    public void anim() {
        if (isFabOpen) {
            fabSearch.startAnimation(fab_close);
            fabWrite.startAnimation(fab_close);
            fabRec.startAnimation(fab_close);
            fabSearch.setClickable(false);
            fabWrite.setClickable(false);
            fabRec.setClickable(false);
            isFabOpen = false;
        } else {
            fabSearch.startAnimation(fab_open);
            fabWrite.startAnimation(fab_open);
            fabRec.startAnimation(fab_open);
            fabSearch.setClickable(true);
            fabWrite.setClickable(true);
            fabRec.setClickable(true);
            isFabOpen = true;
        }
    }
}