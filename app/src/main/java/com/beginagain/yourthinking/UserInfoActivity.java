package com.beginagain.yourthinking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;

public class UserInfoActivity extends AppCompatActivity {
    private Button mGoogleLogoutBtn, mGoogleRevokeBtn;
    private ImageButton mChangeBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SharedPreferences.Editor editor;

    public static final String PREFNAME = "Preferences";
    public static final int PICK_IMAGE = 1;

    Uri photoURI;

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

        mChangeBtn = (ImageButton) findViewById(R.id.btn_google_change_photo);
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
    public void onBackPressed() {

        String pageNull = "Recommend";
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page", pageNull);
        startActivity(intent);

        finish();
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
}
