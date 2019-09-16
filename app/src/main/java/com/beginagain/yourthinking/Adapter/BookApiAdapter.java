package com.beginagain.yourthinking.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Board.WriteActivity;
import com.beginagain.yourthinking.Board.WriteBookActivity;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookApiAdapter extends RecyclerView.Adapter<BookApiAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecommendBookItem> items;
    String title, author, company, ISBN, thumbnail, id;
    int itemLayout;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public BookApiAdapter(Context context, ArrayList<RecommendBookItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_book, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        RecommendBookItem item = items.get(myViewHolder.getAdapterPosition());
        myViewHolder.cardView.setTag(position);
        myViewHolder.mTitle.setText(item.getTitle());
        myViewHolder.mAuthor.setText(item.getAuthor());
        myViewHolder.mCompany.setText(item.getPublisher());
        myViewHolder.mISBN.setText(item.getIsbn());
        Picasso.get().load(item.getImage()).into(myViewHolder.mThumbnail);

    }

    @Override
    public int getItemCount() {
        // null error 방지
        if (items == null) {
            return 0;
        }
        return this.items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mAuthor, mCompany, mISBN;
        public ImageView mThumbnail;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_recommend_item);
            mTitle = (TextView) itemView.findViewById(R.id.text_view_recommend_item_title);
            mAuthor = (TextView) itemView.findViewById(R.id.text_view_recommend_item_author);
            mCompany = (TextView) itemView.findViewById(R.id.text_view_recommend_item_company);
            mISBN = (TextView) itemView.findViewById(R.id.text_view_recommend_item_isbn);
            mThumbnail = (ImageView)itemView.findViewById(R.id.iv_recommend_item_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    RecommendBookItem data = items.get(pos);

                    //Intent intent = new Intent(view.getContext(), WriteBookActivity.class);

                    title = data.getTitle();
                    author = data.getAuthor();
                    company = data.getPublisher();
                    ISBN = data.getIsbn();
                    thumbnail = data.getImage();
                    id = data.getId();

                    AlertDialog.Builder alert_ex = new AlertDialog.Builder(context);
                    alert_ex.setMessage("이 책을 등록하시겠습니까?");

                    alert_ex.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mStore.collection("board").document(id)
                                    .update("author" , author, "booktitle",title, "image", thumbnail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "완료!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.putExtra("page", "Board");
                                            context.startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "작성실패!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    alert_ex.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert_ex.setTitle("Your Thinking");
                    AlertDialog alert = alert_ex.create();
                    alert.show();
                }
            });

        }
    }
}