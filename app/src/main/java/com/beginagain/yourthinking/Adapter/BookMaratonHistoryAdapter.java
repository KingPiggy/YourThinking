package com.beginagain.yourthinking.Adapter;

import android.content.Context;
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

import com.beginagain.yourthinking.BookMaraton.BookMaratonActivity;
import com.beginagain.yourthinking.BookMaraton.BookMaratonHistoryInfoActivity;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMaratonHistoryAdapter extends RecyclerView.Adapter<BookMaratonHistoryAdapter.MyViewHolder> {
    Context context;
    List<MaratonBookItem> mMaratonList;
    String title, author, company, ISBN, thumbnail, id, category, totalPage, readPage, date, pubDate;
    int itemLayout;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public BookMaratonHistoryAdapter( List<MaratonBookItem> mMaratonList) {
        this.mMaratonList = mMaratonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maraton_book, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        MaratonBookItem data = mMaratonList.get(position);
        myViewHolder.mTitle.setText(data.getTitle());
        myViewHolder.mTotalPage.setText(data.getTotalPage());
        myViewHolder.mReadPage.setText(data.getReadPage());
        myViewHolder.mDate.setText(data.getDate());
        Picasso.get().load(data.getImage()).into(myViewHolder.mThumbnail);

    }

    @Override
    public int getItemCount() {
        // null error 방지
        if (mMaratonList == null) {
            return 0;
        }
        return this.mMaratonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mReadPage, mTotalPage, mDate;
        public ImageView mThumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.text_view_maraton_item_title);
            mTotalPage = (TextView) itemView.findViewById(R.id.text_view_maraton_item_total_page);
            mReadPage = (TextView) itemView.findViewById(R.id.text_view_maraton_item_read_page);
            mDate = (TextView) itemView.findViewById(R.id.text_view_maraton_item_date);
            mThumbnail = (ImageView)itemView.findViewById(R.id.iv_maraton_item_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    MaratonBookItem data = mMaratonList.get(pos);

                    Intent intent = new Intent(view.getContext(), BookMaratonHistoryInfoActivity.class);

                    final String myUid = user.getUid();

                    id =data.getId();
                    title = data.getTitle();
                    author = data.getAuthor();
                    thumbnail = data.getImage();
                    category = data.getCategory();
                    totalPage = data.getTotalPage();
                    readPage = data.getReadPage();
                    date = data.getDate();
                    pubDate = data.getPubdate();
                    company = data.getPublisher();
                    ISBN = data.getIsbn();

                    intent.putExtra("Id", id);
                    intent.putExtra("User", myUid);
                    intent.putExtra("Title", title);
                    intent.putExtra("Date", date);
                    intent.putExtra("Image", thumbnail);
                    intent.putExtra("Author", author);
                    intent.putExtra("Category", category);
                    intent.putExtra("TotalPage", totalPage);
                    intent.putExtra("ReadPage", readPage);
                    intent.putExtra("Isbn", ISBN);
                    intent.putExtra("pubDate", pubDate);
                    intent.putExtra("Page", "MaratonPage");
                    intent.putExtra("Company", company);
                    view.getContext().startActivity(intent);

                }});

        }
    }
}