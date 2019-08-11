package com.beginagain.yourthinking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.R;

import java.util.ArrayList;


public class MaratonItemAdapter extends RecyclerView.Adapter<MaratonItemAdapter.MyViewHolder> {
    Context context;
    ArrayList<MaratonBookItem> items;
    int itemLayout;

    public MaratonItemAdapter(Context context, ArrayList<MaratonBookItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_maraton_book, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        MaratonBookItem item = items.get(myViewHolder.getAdapterPosition());
        myViewHolder.cardView.setTag(position);
        myViewHolder.mTitle.setText(item.getTitle());
        myViewHolder.mTotal.setText(Integer.toString(item.getTotal()));
        myViewHolder.mRead.setText(Integer.toString(item.getRead()));

        //Picasso.get().load(item.getImage()).into(myViewHolder.mThumbnail);

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
        public TextView mTitle, mTotal, mRead;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_maraton_item);
            mTitle = (TextView) itemView.findViewById(R.id.text_view_maraton_item_title);
            mTotal = (TextView) itemView.findViewById(R.id.text_view_maraton_item_total);
            mRead = (TextView) itemView.findViewById(R.id.text_view_maraton_item_read);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "hi " + mTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
