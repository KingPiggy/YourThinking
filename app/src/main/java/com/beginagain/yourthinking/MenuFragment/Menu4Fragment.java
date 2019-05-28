package com.beginagain.yourthinking.MenuFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;

public class Menu4Fragment extends Fragment {

    private Button mGoogleLogoutBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu4, container, false);

        mGoogleLogoutBtn = (Button)view.findViewById(R.id.btn_google_logout);
        mGoogleLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        return inflater.inflate(R.layout.fragment_menu4, container, false);
    }
}