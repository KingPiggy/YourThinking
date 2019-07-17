package com.beginagain.yourthinking.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beginagain.yourthinking.Adapter.BookRecommendAdapter;
import com.beginagain.yourthinking.Adapter.MaratonItemAdapter;
import com.beginagain.yourthinking.BookMaratonActivity;
import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;
import com.beginagain.yourthinking.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Menu4Fragment extends Fragment {
    TextView mGuideText;
    Button mMakeBtn, mDeleteBtn;
    Boolean isMaratonGoing;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<MaratonBookItem> emptyItems = new ArrayList<MaratonBookItem>();
    private MaratonItemAdapter recyclerAdapter = new MaratonItemAdapter(getActivity(), emptyItems, R.layout.activity_book_maraton);

    View view;

    SharedPreferences.Editor editor;
    final String PREFNAME = "Preferences";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu4, container, false);

        init();

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFNAME, MODE_PRIVATE);
        editor = settings.edit();
        isMaratonGoing = settings.getBoolean("isMaratonGoing", false);

        // 독서 마라톤 진행 여부 확인해서 진행중이 아니라면 가이드와 버튼 표시
        if (isMaratonGoing == false) {
            mGuideText.setVisibility(View.VISIBLE);
            mMakeBtn.setVisibility(View.VISIBLE);
            mDeleteBtn.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mMakeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BookMaratonActivity.class);
                    startActivity(intent);
                }
            });

        }
        MaratonBookItem temp = new MaratonBookItem("제목이다", 100, 20);

        //리싸이클러뷰 구현해놨으니까
        //BookMaratonActivity.java 에서 북마라톤 생성하면서 MaratonBookItem 생성하면서 emptyItems 리스트에 넣고
        //이 프래그먼트로 어떻게 넘길지 생각하기
        emptyItems.add(temp);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    void init() {
        mGuideText = (TextView) view.findViewById(R.id.text_view_menu4_guide);
        mMakeBtn = (Button) view.findViewById(R.id.btn_menu4_make);
        mDeleteBtn = (Button) view.findViewById(R.id.btn_menu4_delete);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_maratonitem);
    }
}