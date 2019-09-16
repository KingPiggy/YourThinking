package com.beginagain.yourthinking.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Board.BoardResultActivity;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class BookBoardAdapter extends RecyclerView.Adapter<BookBoardAdapter.MainViewHolder> {

    String mName, mTitle, mContents, mDate, mId, mImage, mAuthor, mBooktitle, mRecommend;
     List<BookBoardItem> mSearchList;

    public BookBoardAdapter(List<BookBoardItem> mSearchList) {
        this.mSearchList = mSearchList;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        BookBoardItem data = mSearchList.get(position);
        holder.boardNameTextView.setText(data.getName());
        holder.boardTitleTextView.setText(data.getTitle());
        holder.boardDateTextView.setText(data.getDate());
        holder.boardRecommendTextView.setText(data.getRecommend());
        Picasso.get().load(data.getImage()).into(holder.boardImageView);
    }

    @Override
    public int getItemCount() {
        if (mSearchList == null) {
            return 0;
        }
        return mSearchList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView boardTitleTextView;
        public TextView boardNameTextView;
        public TextView boardDateTextView;
        public TextView boardRecommendTextView;
        public ImageView boardImageView;

        public MainViewHolder(View itemView) {
            super(itemView);
            boardNameTextView = itemView.findViewById(R.id.text_view_board_item_name);
            boardTitleTextView = itemView.findViewById(R.id.text_view_board_item_title);
            boardDateTextView = itemView.findViewById(R.id.text_view_board_item_time);
            boardRecommendTextView = itemView.findViewById(R.id.text_view_board_item_recommend);
            boardImageView = itemView.findViewById(R.id.iv_board_item_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    BookBoardItem data = mSearchList.get(pos);

                    Intent intent = new Intent(view.getContext(), BoardResultActivity.class);

                    mId = data.getId();
                    mName = data.getName();
                    mTitle = data.getTitle();
                    mContents = data.getContents();
                    mDate = data.getDate();
                    mImage = data.getImage();
                    mAuthor = data.getAuthor();
                    mBooktitle = data.getBooktitle();
                    mRecommend = data.getRecommend();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    if(mAuth.getCurrentUser()!=null)
                        intent.putExtra("user", "1");
                    else
                        intent.putExtra("user","0");
                    intent.putExtra("Id", mId);
                    intent.putExtra("Name",mName);
                    intent.putExtra("Title", mTitle);
                    intent.putExtra("Contents", mContents);
                    intent.putExtra("Date", mDate);
                    intent.putExtra("Image", mImage);
                    intent.putExtra("Author", mAuthor);
                    intent.putExtra("BookTitle", mBooktitle);
                    intent.putExtra("Recommend", mRecommend);
                    intent.putExtra("Page","Board");
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}

