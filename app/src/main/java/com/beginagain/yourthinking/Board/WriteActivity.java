package com.beginagain.yourthinking.Board;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText mWriteTitleText, mWriteContentText;
    private TextView mWriteDateText, mWriteNameText;
    private ImageButton mImageButton;
    private Button mBtnUpload;
    private ImageView imageView;

    private String id, mCurrentPhotoPath;
    private Uri photoURI, albumURI;

    private static final int FROM_ALBUM = 1;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd hh:mm");
    String formatDate = mFormat.format(mDate);

    String mName = user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        id = mStore.collection("board").document().getId();

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

        imageView = findViewById(R.id.iv_write_camera);
        mImageButton = findViewById(R.id.image_button_camera);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog();
            }
        });
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
    }
    private void makeDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(WriteActivity.this);
        alt_bld.setTitle("사진 업로드").setCancelable(
                false).setPositiveButton("앨범선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        selectAlbum();
                    }
                }).setNegativeButton("취소   ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
    public File createImageFile() throws IOException{
        String imgFileName = id + ".jpg";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    //앨범 선택 클릭
    public void selectAlbum(){
        //앨범에서 이미지 가져옴
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    try{
                        File albumFile = null;
                        albumFile = createImageFile();
                        photoURI = data.getData();
                        albumURI = Uri.fromFile(albumFile);
                        galleryAddPic();
                        imageView.setImageURI(photoURI);
                        // setPic();
                        //cropImage();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("알림","앨범에서 가져오기 에러");
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_board:
                if(mWriteContentText.getText().toString().equals("")||mWriteTitleText.getText().toString().equals("")){
                    Toast.makeText(this,"글 또는 내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    final Map<String, Object> post = new HashMap<>();
                    post.put("id", id);
                    post.put("name", mWriteNameText.getText().toString());
                    post.put("title", mWriteTitleText.getText().toString());
                    post.put("contents", mWriteContentText.getText().toString());
                    post.put("date", mWriteDateText.getText().toString());
                    post.put("recount", 0);

                    final AlertDialog.Builder alert_ex = new AlertDialog.Builder(WriteActivity.this);
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
                    alert_ex.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
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
                    alert_ex.setTitle("Your Thinking");
                    AlertDialog alert = alert_ex.create();
                    alert.dismiss();
                    alert.show();

                    String filename = id + "_" + "photo";
                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://beginagains.appspot.com");
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://beginagains.appspot.com").child("BoardPhotos/" + filename);
                    UploadTask uploadTask;

                    Bitmap bitmap = null;
                    if (imageView.getDrawable() != null) {
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, baos);
                        byte[] data = baos.toByteArray();

                        uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.v("알림", "사진 업로드 실패");
                                exception.printStackTrace();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
                    }
                }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}