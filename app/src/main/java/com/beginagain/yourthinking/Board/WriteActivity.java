package com.beginagain.yourthinking.Board;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookApiAdapter;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText mWriteTitleText, mWriteContentText;
    private String id;
    private Button mBtnUpload;
    private TextView mWriteDateText, mWriteNameText;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd hh:mm");
    String formatDate = mFormat.format(mDate);

    // 사진등록을 위한

    String mName = user.getDisplayName();

    // private static final int PICK_FROM_ALBUM = 1;
    // private Boolean isPermission = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

       // tedPermission();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mWriteNameText = findViewById(R.id.text_view_write_name);
        mWriteDateText = findViewById(R.id.text_view_write_time);
        mWriteTitleText = findViewById(R.id.et_write_title);
        mWriteContentText = findViewById(R.id.et_write_content);
        mBtnUpload = findViewById(R.id.btn_upload_board);

        mWriteNameText.setText(mName);
        mWriteDateText.setText(formatDate);

        mBtnUpload.setOnClickListener(this);
/**
        findViewById(R.id.image_button_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isPermission) goToAlbum();
                else
                    Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });
        final ImageView imageView = findViewById(R.id.iv_write_camera);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        AlertDialog.Builder alert_ex = new AlertDialog.Builder(WriteActivity.this);
                        alert_ex.setMessage("정말로 삭제하시겠습니까?");

                        alert_ex.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageView.setImageBitmap(null);
                            }
                        });
                        alert_ex.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert_ex.setTitle("Your Thinking");
                        AlertDialog alert = alert_ex.create();
                        alert.show();
                        break;
                    }
                }
                return true;
            }
        });
**/
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_board :
                // 생성
                id = mStore.collection("board").document().getId();
                final Map<String, Object> post = new HashMap<>();
                post.put("id", id);
                post.put("name", mWriteNameText.getText().toString());
                post.put("title", mWriteTitleText.getText().toString());
                post.put("contents", mWriteContentText.getText().toString());
                post.put("date", mWriteDateText.getText().toString());
                post.put("recount", 0);
                /**
                post.put("image", thumnail);
                post.put("booktitle",title);
                post.put("author", author);
                 **/
                AlertDialog.Builder alert_ex = new AlertDialog.Builder(WriteActivity.this);
                alert_ex.setMessage("책을 등록하시겠습니까?");

                alert_ex.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStore.collection("board").document(id).set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(WriteActivity.this, WriteBookActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WriteActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                /**
                alert_ex.setNeutralButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStore.collection("board").document(id).set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                                        intent.putExtra("page", "Board");
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WriteActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                 **/
                alert_ex.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                        intent.putExtra("page", "Board");
                        startActivity(intent);
                        finish();
                    }
                });
                alert_ex.setTitle("Your Thinking");
                AlertDialog alert = alert_ex.create();
                alert.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

















    /**
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
    }
    private void setImage() {
        ImageView imageView = findViewById(R.id.iv_write_camera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        imageView.setImageBitmap(originalBm);
        tempFile = null;
    }
**/
