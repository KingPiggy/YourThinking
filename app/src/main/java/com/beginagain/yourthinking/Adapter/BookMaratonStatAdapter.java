package com.beginagain.yourthinking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beginagain.yourthinking.Item.MaratonBookItem;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookMaratonStatAdapter  extends RecyclerView.Adapter<BookMaratonStatAdapter.MyViewHolder> {
    Context context;
    List<MaratonBookItem> items;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public BookMaratonStatAdapter(List<MaratonBookItem> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maraton_stat, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        MaratonBookItem item = items.get(position);

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
        public ImageView mThumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail = (ImageView)itemView.findViewById(R.id.iv_maraton_item_stat);


        }
    }
}