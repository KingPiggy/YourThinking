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

import com.beginagain.yourthinking.R;
import com.beginagain.yourthinking.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Menu4Fragment extends Fragment {

    View view;
    Button mUserInfoBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu4, container, false);

        mUserInfoBtn = (Button) view.findViewById(R.id.btn_menu4_userinfo);
        mUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}