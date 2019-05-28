package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.beginagain.yourthinking.BookMaratonActivity;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;

public class Menu1Fragment extends Fragment {
    Button mTempBtn;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu1, container, false);

        mTempBtn = (Button)view.findViewById(R.id.btn_temp);
        mTempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

}