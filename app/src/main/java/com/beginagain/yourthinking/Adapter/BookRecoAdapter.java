package com.beginagain.yourthinking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookRecoAdapter extends RecyclerView.Adapter<BookRecoAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecommendBookItem> items;
    int itemLayout;

    public BookRecoAdapter(Context context, ArrayList<RecommendBookItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public BookRecoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_book, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecoAdapter.MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        RecommendBookItem item = items.get(myViewHolder.getAdapterPosition());
        myViewHolder.cardView.setTag(position);
        myViewHolder.mTitle.setText(item.getTitle());
        myViewHolder.mAuthor.setText(item.getAuthor());
        myViewHolder.mCompany.setText(item.getCompany());
        myViewHolder.mISBN.setText(item.getIsbn());
        //Picasso.get().load(item.getImage()).into(holder.mThumbnail);

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

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
