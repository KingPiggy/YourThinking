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

import com.beginagain.yourthinking.BookInfoActivity;
import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookRecommendAdapter extends RecyclerView.Adapter<BookRecommendAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecommendBookItem> items;
    int itemLayout;
    String title, desc, author, publisher, date, isbn, image;

    public BookRecommendAdapter(Context context, ArrayList<RecommendBookItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public BookRecommendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_book, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecommendAdapter.MyViewHolder myViewHolder, int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
                    int pos = getAdapterPosition();

                    RecommendBookItem data = items.get(pos);

                    Intent intent = new Intent(view.getContext(), BookInfoActivity.class);
                    title = data.getTitle();
                    author = data.getAuthor();
                    publisher = data.getPublisher();
                    image = data.getImage();
                    isbn =data.getIsbn();
                    date = data.getDate();
                    desc = data.getDesc();
                    intent.putExtra("Title", title);
                    intent.putExtra("Publisher", publisher);
                    intent.putExtra("Author", author);
                    intent.putExtra("Image", image);
                    intent.putExtra("Isbn", isbn);
                    intent.putExtra("Date", date);
                    intent.putExtra("Desc", desc);
                    view.getContext().startActivity(intent);


                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
