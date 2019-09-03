package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beginagain.yourthinking.Board.MyBoardActivity;
import com.beginagain.yourthinking.Board.RecommendBoardActivity;
import com.beginagain.yourthinking.BookRecommendActivity;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.MyChatActivity;
import com.beginagain.yourthinking.NoticeActivity;
import com.beginagain.yourthinking.R;
import com.beginagain.yourthinking.UserInfoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class Menu1Fragment extends Fragment {

    private Button mRankedBtn, mNoticeBtn;
    private Button mRecommendBtn, mMyBoardBtn, mMyChatBtn;
    private ImageButton mProfileSettingsBtn;
    private TextView mUserName, mUserEmail;
    private de.hdodenhof.circleimageview.CircleImageView mUserImage;

    private String name, email;
    private Uri profilePhotoUrl;

    private ProgressBar mProgressBar;
    private TextView mProgressText,mProgressNotText;
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
        mNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
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

    @Override
    public void onResume() {
        super.onResume();

        Log.d("test", "마라톤 진행 여부 : " + isBookMaratonOnGoing());

        if(isBookMaratonOnGoing()) {
            updateProgressBar();
            showMaratonProgress();
        } else {
            hide_ProgressBar();
        }
    }

    private void init() {
        mRankedBtn = (Button) view.findViewById(R.id.btn_menu1_ranked_book);
        mUserName = (TextView) view.findViewById(R.id.text_view_menu1_profile_name);
        mUserEmail = (TextView) view.findViewById(R.id.text_view_menu1_profile_email);
        mUserImage = (CircleImageView) view.findViewById(R.id.image_menu1_profile_image);

        mProfileSettingsBtn = (ImageButton) view.findViewById(R.id.image_btn_menu1_profile_settings);
        mRecommendBtn = (Button) view.findViewById(R.id.btn_menu1_recommend);
        mNoticeBtn = (Button)view.findViewById(R.id.btn_menu1_notice);
        mMyChatBtn = (Button) view.findViewById(R.id.btn_menu1_my_chat);
        mMyBoardBtn = (Button) view.findViewById(R.id.btn_menu1_my_board);
        mProgressBar = (ProgressBar)view.findViewById(R.id.maratonProgressBar);
        mProgressText = (TextView) view.findViewById(R.id.maratonProgressText);
        mProgressNotText = (TextView) view.findViewById(R.id.maratonProgressNot);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            profilePhotoUrl = user.getPhotoUrl();
            String myUid = user.getUid();
            mUserName.setText(name);
            mUserEmail.setText(email);

            FirebaseStorage storage = FirebaseStorage.getInstance("gs://beginagains.appspot.com");
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference  = storageRef.child("ProfilePhotos/" + myUid + "_" + "photo");
            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                    .into(mUserImage);
        }
    }

    public  void showMaratonProgress(){
        mProgressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressNotText.setVisibility(View.INVISIBLE);
    }
    public  void hide_ProgressBar(){
        mProgressText.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressNotText.setVisibility(View.VISIBLE);

    }

    private void updateProgressBar() {
        float max = 0;
        float current = 0;
        SQLiteDatabase maratonDB = null;
        Cursor c = null;

        try {
            maratonDB = this.getActivity().openOrCreateDatabase(Menu4Fragment.dbName_Maraton, MODE_PRIVATE, null);
            c = maratonDB.rawQuery("SELECT * FROM " + Menu4Fragment.tableName_Maraton, null);

            if (c != null & c.moveToFirst()) {
                do {
                    String title = c.getString(c.getColumnIndex("title"));
                    max += c.getInt(c.getColumnIndex(Menu4Fragment.TAG_PageNum));
                    current += c.getInt(c.getColumnIndex(Menu4Fragment.TAG_currPageNum));

                } while (c.moveToNext());
            }

            mProgressBar.setMax((int)max);
            mProgressBar.setProgress((int)current);

            int percertage;

            if(current == 0)
                percertage = 0;
            else
                percertage = (int)((current / max) * 100);

            mProgressText.setText("독서 마라톤 진행률 : " + percertage + "%");
        }catch (SQLiteException e) {
            Log.d("test", "Update Progress Bar 오류 발생 : " + e.getMessage());
        } finally {
            if (maratonDB != null)
                maratonDB.close();
            if(c != null)
                c.close();
        }
    }

    private boolean isBookMaratonOnGoing() {
        SQLiteDatabase maratonDB = null;
        Cursor c = null;
        try {
            maratonDB = this.getActivity().openOrCreateDatabase(Menu4Fragment.dbName_Maraton, MODE_PRIVATE, null);
            c = maratonDB.rawQuery("SELECT * FROM " + Menu4Fragment.tableName_Maraton, null);

            if (c != null & c.moveToFirst()) {
                Log.d("Test", "*********** 북 마라톤 진행중 ***********");
                return true;

            } else {
                Log.d("Test", "******** 진행중인 북 마라톤 없음 ************");
                return false;
            }
        } catch (SQLiteException e) {
            Log.d("Test", "데이터 베이스 읽기 실패 : " + e.getMessage());
        } finally {
            if (c != null)
                c.close();
        }
        return false;
    }
}