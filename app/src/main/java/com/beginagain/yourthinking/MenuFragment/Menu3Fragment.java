package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beginagain.yourthinking.Board.RecommendBoardActivity;
import com.beginagain.yourthinking.Board.SearchBoardActivity;
import com.beginagain.yourthinking.Board.BookBoardActivity;
import com.beginagain.yourthinking.R;
import com.beginagain.yourthinking.Board.MyBoardActivity;

public class Menu3Fragment extends Fragment implements View.OnClickListener {
    View view;
    Button mBookBoardBtn, mMyBoardBtn, mSearchBoardBtn, mRecommendBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu3, container, false);

        mBookBoardBtn = (Button) view.findViewById(R.id.btn_in_board);
        mMyBoardBtn = (Button)view.findViewById(R.id.btn_my_board);
        mSearchBoardBtn = (Button)view.findViewById(R.id.btn_search_board);
        mRecommendBtn = (Button)view.findViewById(R.id.btn_search_recommend);

        mBookBoardBtn.setOnClickListener(this);
        mSearchBoardBtn.setOnClickListener(this);
        mMyBoardBtn.setOnClickListener(this);
        mRecommendBtn.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_in_board :
                Intent intent = new Intent(getActivity(), BookBoardActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_search_board:
                Intent intent1 = new Intent(getActivity(), SearchBoardActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_my_board:
                Intent intent2 = new Intent(getActivity(), MyBoardActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_search_recommend:
                Intent intent3 = new Intent(getActivity(), RecommendBoardActivity.class);
                startActivity(intent3);
                break;
        }
    }
}