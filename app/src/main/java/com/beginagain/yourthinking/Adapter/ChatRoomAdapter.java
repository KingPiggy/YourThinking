package com.beginagain.yourthinking.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.ChatActivity;
import com.beginagain.yourthinking.Item.ChatRoomItem;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatRoomItem> items;
    ArrayList<Integer> uidCounts;
    int itemLayout;
    int peopleCount = 0;

    private String uid;

    public ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> items, ArrayList<Integer> uidCounts, int itemLayout) {
        this.context = context;
        this.items = items;
        this.uidCounts = uidCounts;
        this.itemLayout = itemLayout;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
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
        myViewHolder.mPeopleCount.setText("참여인원 " + uidCounts.get(position));
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                String chatRoomName = items.get(pos).getRoomTitle();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                }
                else{
                    AlertDialog.Builder alert_ex = new AlertDialog.Builder(context);
                    alert_ex.setMessage("로그인 후 사용가능합니다. 로그인 하시겠습니까?");

                    alert_ex.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent accountIntent = new Intent(context, LoginActivity.class);
                            context.startActivity(accountIntent);
                        }
                    });
                    alert_ex.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert_ex.setTitle("Your Thinking");
                    AlertDialog alert = alert_ex.create();
                    alert.show();

                    return;
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatRoomName", chatRoomName);
                intent.putExtra("count", uidCounts.get(pos));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mRoomName, mBookName, mDesc, mPeopleCount;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_chat_room);

            mRoomName = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_room_title);
            mBookName = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_book_title);
            mDesc = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_room_desc);
            mPeopleCount = (TextView) itemView.findViewById(R.id.text_view_chat_room_item_count);
        }
    }
}
