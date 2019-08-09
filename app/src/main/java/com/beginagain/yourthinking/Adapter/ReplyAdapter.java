package com.beginagain.yourthinking.Adapter;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<BookReplyItem> mReplyList;

    public ReplyAdapter(List<BookReplyItem> mReplyList) {
        this.mReplyList = mReplyList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_board, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        BookReplyItem data = mReplyList.get(position);
        Uri uriReplyImage = Uri.parse(data.getImage());
        holder.replyNameTextView.setText(data.getName());
        holder.replyDateTextView.setText(data.getDate());
        holder.replyContentsTextView.setText(data.getContents());
        Picasso.get().load(uriReplyImage).into(holder.replyImageView);
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
