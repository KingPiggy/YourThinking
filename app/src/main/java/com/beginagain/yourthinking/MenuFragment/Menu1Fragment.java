package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beginagain.yourthinking.Board.MyBoardActivity;
import com.beginagain.yourthinking.Board.RecommendBoardActivity;
import com.beginagain.yourthinking.BookRecommendActivity;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.MyChatActivity;
import com.beginagain.yourthinking.R;
import com.beginagain.yourthinking.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu1Fragment extends Fragment {

    private Button mRankedBtn;
    private Button mRecommendBtn, mMyBoardBtn, mMyChatBtn;
    private ImageButton mProfileSettingsBtn;
    private TextView mUserName, mUserEmail;
    private de.hdodenhof.circleimageview.CircleImageView mUserImage;

    private String name, email;
    private Uri profilePhotoUrl;


    View view;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu1, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        init();

        mRankedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookRecommendActivity.class);
                startActivity(intent);
            }
        });

        mProfileSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        mRecommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecommendBoardActivity.class);
                startActivity(intent);
            }
        });

        mMyBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyBoardActivity.class);
                startActivity(intent);
            }
        });

        mMyChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyChatActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void init() {
        mRankedBtn = (Button) view.findViewById(R.id.btn_menu1_ranked_book);
        mUserName = (TextView) view.findViewById(R.id.text_view_menu1_profile_name);
        mUserEmail = (TextView) view.findViewById(R.id.text_view_menu1_profile_email);
        mUserImage = (CircleImageView) view.findViewById(R.id.image_menu1_profile_image);

        mProfileSettingsBtn = (ImageButton) view.findViewById(R.id.image_btn_menu1_profile_settings);
        mRecommendBtn = (Button) view.findViewById(R.id.btn_menu1_recommend);
        mMyChatBtn = (Button) view.findViewById(R.id.btn_menu1_my_chat);
        mMyBoardBtn = (Button) view.findViewById(R.id.btn_menu1_my_board);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            profilePhotoUrl = user.getPhotoUrl();

            mUserName.setText(name);
            mUserEmail.setText(email);
            Picasso.get().load(profilePhotoUrl).into(mUserImage);
        }
    }

}