package com.beginagain.yourthinking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Board.MyBoardActivity;
import com.beginagain.yourthinking.MenuFragment.Menu4Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private Button mGoogleLogoutBtn, mGoogleRevokeBtn, mChangeBtn, mMyBoard, mMyChat;
    private TextView mUserName, mUserEmail, mProgressText,mProgressNotText;
    private ProgressBar mProgressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SharedPreferences.Editor editor;
    Uri photoURI;

    private de.hdodenhof.circleimageview.CircleImageView mUserImage;
    private String name, email;
    private Uri profilePhotoUrl;

    public static final String PREFNAME = "Preferences";
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        init();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        mAuth = FirebaseAuth.getInstance();
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
            Glide.with(getApplication())
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                    .into(mUserImage);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        editor = settings.edit();

        mGoogleLogoutBtn = (Button) findViewById(R.id.btn_account_logout);
        mGoogleLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    signOut();
                    finishAffinity();
                } else {
                    Toast.makeText(getApplicationContext(), "로그인되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGoogleRevokeBtn = (Button) findViewById(R.id.btn_account_revoke);
        mGoogleRevokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
                finishAffinity();
            }
        });

        mChangeBtn = (Button) findViewById(R.id.btn_account_photo);
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "실패하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case PICK_IMAGE:
                if (data.getData() != null) {
                    try {
                        photoURI = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://beginagains.appspot.com");

                    String myUid = mAuth.getUid();
                    String filename = myUid + "_" + "photo";
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://beginagains.appspot.com").child("ProfilePhotos/" + filename);
                    UploadTask uploadTask;
                    Uri file = null;
                    file = photoURI;

                    uploadTask = storageRef.putFile(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }

    private void signOut() {
        editor.remove("username");
        editor.remove("useremail");
        editor.remove("userphotoUrl");
        editor.remove("useruid");
        editor.putBoolean("isLogin", false);
        editor.apply();

        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void revokeAccess() {
        editor.remove("username");
        editor.remove("useremail");
        editor.remove("userphotoUrl");
        editor.remove("useruid");
        editor.putBoolean("isLogin", false);
        editor.apply();

        FirebaseUser user = mAuth.getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
/**
        Log.d("test", "마라톤 진행 여부 : " + isBookMaratonOnGoing());

        if(isBookMaratonOnGoing()) {
            updateProgressBar();
            showMaratonProgress();
        } else {
            hide_ProgressBar();
        }
 **/
    }
    private void init() {
        mUserImage = (CircleImageView) findViewById(R.id.image_menu1_profile_image);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_maraton);
        mProgressText = (TextView) findViewById(R.id.text_view_maraton_Progress_Text);
        mProgressNotText = (TextView) findViewById(R.id.text_view_maraton_ProgressNot);
        mUserName = (TextView) findViewById(R.id.text_view_menu1_profile_name);
        mUserEmail = (TextView) findViewById(R.id.text_view_menu1_profile_email);
        mMyBoard = (Button)findViewById(R.id.btn_account_myBoard);
        mMyBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MyBoardActivity.class);
                startActivity(intent);
            }
        });
        mMyChat = (Button)findViewById(R.id.btn_account_myChat);
        mMyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),MyChatActivity.class);
                startActivity(intent);
            }
        });
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_info);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
    }
    public void showMaratonProgress(){
        mProgressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressNotText.setVisibility(View.INVISIBLE);
    }
    public void hide_ProgressBar(){
        mProgressText.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressNotText.setVisibility(View.VISIBLE);
    }
    /**
    private void updateProgressBar() {
        float max = 0;
        float current = 0;
        SQLiteDatabase maratonDB = null;
        Cursor c = null;

        try {
            maratonDB = this.getApplication().openOrCreateDatabase(Menu4Fragment.dbName_Maraton, MODE_PRIVATE, null);
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
            maratonDB = getApplication().openOrCreateDatabase(Menu4Fragment.dbName_Maraton, MODE_PRIVATE, null);
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
     **/
    @Override
    public void onBackPressed() {
        finish();
        Intent intent  = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}