package com.beginagain.yourthinking.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.NoticeResultActivity;
import com.beginagain.yourthinking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MainViewHolder> {

    String mName, mTitle, mContents, mDate, mId;

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

        public MainViewHolder(View itemView) {
            super(itemView);
            boardNameTextView = itemView.findViewById(R.id.text_view_notice_item_name);
            boardTitleTextView = itemView.findViewById(R.id.text_view_notice_item_title);
            boardDateTextView = itemView.findViewById(R.id.text_view_notice_item_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    BookBoardItem data = mNoticeList.get(pos);

                    Intent intent = new Intent(view.getContext(), NoticeResultActivity.class);

                    mId = data.getId();
                    mName = data.getName();
                    mTitle = data.getTitle();
                    mContents = data.getContents();
                    mDate = data.getDate();

                    intent.putExtra("Id", mId);
                    intent.putExtra("Name",mName);
                    intent.putExtra("Title", mTitle);
                    intent.putExtra("Contents", mContents);
                    intent.putExtra("Date", mDate);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}