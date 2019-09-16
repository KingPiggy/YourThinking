package com.beginagain.yourthinking.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.R;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MainViewHolder> {

    List<BookBoardItem> mNoticeList;

    public NoticeAdapter(List<BookBoardItem> mNoticeList) {
        this.mNoticeList = mNoticeList;

    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        BookBoardItem data = mNoticeList.get(position);
        holder.boardNameTextView.setText(data.getName());
        holder.boardTitleTextView.setText(data.getTitle());
        holder.boardDateTextView.setText(data.getDate());
        holder.boardContentsTextView.setText(data.getContents());
    }

    @Override
    public int getItemCount() {
        if (mNoticeList== null) {
            return 0;
        }
        return mNoticeList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView boardTitleTextView;
        public TextView boardNameTextView;
        public TextView boardDateTextView;
        public TextView boardContentsTextView;

        public MainViewHolder(View itemView) {
            super(itemView);
            boardNameTextView = itemView.findViewById(R.id.text_view_notice_item_name);
            boardTitleTextView = itemView.findViewById(R.id.text_view_notice_item_title);
            boardDateTextView = itemView.findViewById(R.id.text_view_notice_item_time);
            boardContentsTextView = itemView.findViewById(R.id.text_view_notice_item_contents);

        }
    }
}