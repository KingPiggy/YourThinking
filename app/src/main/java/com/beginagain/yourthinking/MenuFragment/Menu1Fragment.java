package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu1Fragment extends Fragment {
    Button mTempBtn;
    View view;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu1, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Snackbar.make(getActivity().findViewById(android.R.id.content), "환영합니다.", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        return view;
    }

}