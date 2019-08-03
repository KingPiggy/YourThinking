package com.beginagain.yourthinking.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beginagain.yourthinking.ChatActivity;
import com.beginagain.yourthinking.Item.ChatRoomItem;
import com.beginagain.yourthinking.R;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatRoomItem> items;
    int itemLayout;

    public ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_room, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        final ChatRoomItem item = items.get(myViewHolder.getAdapterPosition());

        myViewHolder.cardView.setTag(position);
        myViewHolder.mRoomName.setText(item.getRoomTitle());
        myViewHolder.mBookName.setText(item.getBookTitle());
        myViewHolder.mDesc.setText(item.getDesc());
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hoon","온클릭");
                int pos = (int) view.getTag();
                Log.d("hoon","pos:"+pos);
                String chatRoomName = items.get(pos).getRoomTitle();
                Log.d("hoon","룸네임:"+chatRoomName);

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatRoomName", chatRoomName);
                context.startActivity(intent);
            }
        });
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
        public TextView mRoomName, mBookName, mDesc;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_chat_room);

            mRoomName = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_room_title);
            mBookName = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_book_title);
            mDesc = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_room_desc);
        }


    }
}