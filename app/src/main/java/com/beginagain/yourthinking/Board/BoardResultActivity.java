package com.beginagain.yourthinking.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.ReplyAdapter;
import com.beginagain.yourthinking.Item.BookReplyItem;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class BoardResultActivity extends AppCompatActivity implements View.OnClickListener{

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TextView mName, mTitle, mContents, mDate, mAuthor, mBookTitle;
    private String name, title, contents, date, id, author, image, bookTitle;
    private String rid, recommendid; // 8.4
    private ImageView mImage;

    private EditText mReplyContentText;
    private TextView mReplyNameText, mRecommnedCount;

    private RecyclerView mReplyRecyclerView;
    private ReplyAdapter mAdapter;

    private List<BookReplyItem> mReplyList; // private static 기존

    long mNow = System.currentTimeMillis();
    public int count =0;
    Date mReDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    String formatDate = mFormat.format(mReDate);

    String mReName = user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_result);

        mReplyRecyclerView = findViewById(R.id.recycler_Reply); // 이거수정

        mReplyList = new ArrayList<>();

        mReplyNameText = (TextView)findViewById(R.id.text_view_reply_name);
        mReplyContentText = (EditText)findViewById(R.id.et_reply_contents);
        mRecommnedCount = (TextView)findViewById(R.id.text_view_result_recommend);

        mReplyNameText.setText(mReName);

        mName = (TextView)findViewById(R.id.text_view_result_name);
        mTitle = (TextView)findViewById(R.id.text_view_result_title);
        mContents = (TextView)findViewById(R.id.text_view_result_contents);
        mDate = (TextView)findViewById(R.id.text_view_result_date);
        mBookTitle = (TextView)findViewById(R.id.text_view_result_book_title);
        mAuthor = (TextView)findViewById(R.id.text_view_result_author);
        mImage = (ImageView)findViewById(R.id.iv_result);

        Intent intent = getIntent();
        id = intent.getStringExtra("Id");
        name = intent.getStringExtra("Name");
        title = intent.getStringExtra("Title");
        contents = intent.getStringExtra("Contents");
        date = intent.getStringExtra("Date");
        author = intent.getStringExtra("Author");
        image = intent.getStringExtra("Image");
        bookTitle = intent.getStringExtra("BookTitle");

        mName.setText(name);
        mTitle.setText(title);
        mContents.setText(contents);
        mDate.setText(date);
        mAuthor.setText(author);
        mBookTitle.setText(bookTitle);
        Picasso.get().load(image).into(mImage);

        mStore.collection("board").document(id)
                .collection("reply").orderBy("date").limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots){
                            String rid = (String) dc.getData().get("rid");
                            String name = (String) dc.getData().get("name");
                            String contents = (String) dc.getData().get("contents");
                            String date = (String) dc.getData().get("date");

                            BookReplyItem data = new BookReplyItem(rid, name, contents, date);

                            mReplyList.add(data);
                        }
                        mAdapter = new ReplyAdapter(mReplyList);
                        mReplyRecyclerView.setAdapter(mAdapter);
                    }
                });
        mStore.collection("board").document(id).collection("recommend")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        int recount = 0;
                        for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            recount++;
                        }
                        mRecommnedCount.setText("추천 : "+recount);
                    }
                });

        findViewById(R.id.btn_reply).setOnClickListener(this);
        findViewById(R.id.btn_board_delete).setOnClickListener(this);
        findViewById(R.id.btn_board_retouch).setOnClickListener(this);
        findViewById(R.id.btn_board_recommend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_reply:
                rid = mStore.collection("board").document(id).collection("reply").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("id", rid);
                post.put("name",mReplyNameText.getText().toString());
                post.put("contents", mReplyContentText.getText().toString());
                post.put("date",formatDate);

                mStore.collection("board").document(id)
                        .collection("reply").document(rid).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(BoardResultActivity.this, "댓글 성공!", Toast.LENGTH_SHORT).show();
                                Intent reintent = new Intent(BoardResultActivity.this, BookBoardActivity.class);
                                startActivity(reintent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BoardResultActivity.this, "댓글 실패!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_board_delete:
                if(mReName.equals(name)) {
                    deleteCollection(mStore.collection("board").document(id).collection("reply"), 50, EXECUTOR);
                    mStore.collection("board").document(id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(BoardResultActivity.this, "작성글 삭제", Toast.LENGTH_SHORT).show();
                                    Intent delIntent = new Intent(BoardResultActivity.this, BookBoardActivity.class);
                                    startActivity(delIntent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }else{
                    Toast.makeText(BoardResultActivity.this,"작성자가 아닙니다." , Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_board_retouch:
                Intent touchIntent = new Intent(BoardResultActivity.this, RetouchBoardActivity.class);
                if(mReName.equals(name)) {
                    touchIntent.putExtra("Id", id);
                    touchIntent.putExtra("Name", name);
                    touchIntent.putExtra("Title", title);
                    touchIntent.putExtra("Contents", contents);
                    touchIntent.putExtra("Date", date);
                    startActivity(touchIntent);
                }else{
                    Toast.makeText(BoardResultActivity.this, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_board_recommend:

                mStore.collection("board").document(id).collection("recommend")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                    String name = (String) dc.getData().get("name");
                                    if(mReName.equals(name)){
                                        count = 1;
                                        break;
                                    }
                                }
                                if(count ==0){
                                    recommendid = mStore.collection("board").document(id).collection("recommend").document().getId();
                                    Map<String, Object> recommendPost = new HashMap<>();
                                    recommendPost.put("id", recommendid);
                                    recommendPost.put("name", mReName);
                                    recommendPost.put("date",formatDate);
                                    mStore.collection("board").document(id)
                                        .collection("recommend").document().set(recommendPost);
                                    Toast.makeText(BoardResultActivity.this, "추천!", Toast.LENGTH_SHORT).show();
                                }
                            }});
                if(count==1){
                    Toast.makeText(BoardResultActivity.this, "이미추천!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    // 컬렉션 삭제위해서 쓰는것, doc 참고
    private Task<Void> deleteCollection(final CollectionReference collection, final int batchSize, Executor executor) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Query query = collection.orderBy(FieldPath.documentId()).limit(batchSize);
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                while (deleted.size() >= batchSize) {
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy(FieldPath.documentId())
                            .startAfter(last.getId())
                            .limit(batchSize);
                    deleted = deleteQueryBatch(query);
                }
                return null;
            }
        });
    }
    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (QueryDocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }
}