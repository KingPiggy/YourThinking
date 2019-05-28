package com.beginagain.yourthinking;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInfoActivity extends AppCompatActivity {
    private Button mGoogleLogoutBtn, mGoogleRevokeBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SharedPreferences.Editor editor;

    final String PREFNAME = "Preferences";

    //사용자 정보 변경은 https://firebase.google.com/docs/auth/android/manage-users?hl=ko

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        editor = settings.edit();

        mGoogleLogoutBtn = (Button) findViewById(R.id.btn_google_logout);
        mGoogleLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    signOut();
                } else {
                    Toast.makeText(getApplicationContext(), "로그인되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGoogleRevokeBtn = (Button) findViewById(R.id.btn_google_revoke);
        mGoogleRevokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });
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
}
