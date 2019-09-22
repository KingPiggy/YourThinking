package com.beginagain.yourthinking.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.BookReplyItem;
import com.beginagain.yourthinking.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<BookReplyItem> mReplyList;
    private Context context;

    public ReplyAdapter(List<BookReplyItem> mReplyList, Context context) {
        this.mReplyList = mReplyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_board, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        BookReplyItem data = mReplyList.get(position);
        holder.replyNameTextView.setText(data.getName());
        holder.replyDateTextView.setText(data.getDate());
        holder.replyContentsTextView.setText(data.getContents());

        String myUid = data.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://beginagains.appspot.com");
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference  = storageRef.child("ProfilePhotos/" + myUid + "_" + "photo");
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                .skipMemoryCache(true)// 메모리 캐시 저장 off
                .centerCrop (). crossFade ()
                .into(holder.replyImageView);

        //Picasso.get().load(uriReplyImage).into(holder.replyImageView);
    }

    @Override
    public int getItemCount() {
        return mReplyList.size();
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {

        private TextView replyNameTextView;
        private TextView replyDateTextView;
        private TextView replyContentsTextView;
        private ImageView replyImageView;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            replyNameTextView = itemView.findViewById(R.id.text_view_reply_item_name);
            replyContentsTextView = itemView.findViewById(R.id.text_view_reply_item_content);
            replyDateTextView = itemView.findViewById(R.id.text_view_reply_item_date);
            replyImageView = itemView.findViewById(R.id.iv_reply_image);
        }
    }
}
