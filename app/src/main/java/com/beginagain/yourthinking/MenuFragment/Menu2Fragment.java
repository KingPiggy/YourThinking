package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.beginagain.yourthinking.Adapter.ChatRoomAdapter;
import com.beginagain.yourthinking.Item.ChatRoomItem;
import com.beginagain.yourthinking.MakeChatRoomActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class Menu2Fragment extends Fragment {
    View view;

    Button mRoomMakeBtn, mRoomSearchBtn;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatRoomItem> chatRoomItems = new ArrayList<>();

    public static final int code = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu2, container, false);

        init();

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        showChatList();

        mRoomMakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeChatRoomActivity.class);
                startActivity(intent);
            }
        });

//        adapter.notifyDataSetChanged();

        //                startActivityForResult(intent, code);

        return view;
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == RESULT_OK){
//            switch (requestCode){
//                case 1000:
//                    Log.d("hoon", "응답성공");
//                    showChatList();
//                    break;
//            }
//        }
//
//    }

    void init() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_make_chat_chatlist);
        mRoomMakeBtn = (Button) view.findViewById(R.id.btn_make_chat_makeroom);
        mRoomSearchBtn = (Button) view.findViewById(R.id.btn_make_chat_searchroom);
    }

    private void showChatList() {
        adapter = new ChatRoomAdapter(getActivity(), chatRoomItems, R.layout.fragment_menu2);
        chatRoomItems.clear();
        mRecyclerView.setAdapter(adapter);

        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("hoon", "스냅샷 테스트 겟키: " + dataSnapshot.getKey());
                Log.d("hoon", "스냅샷 테스트 : " + dataSnapshot.child(dataSnapshot.getKey()).getKey());
                Log.d("hoon", "스냅샷 테스트 : ");
                String roomTitle = dataSnapshot.getKey();
                ChatRoomItem chatRoomItem = new ChatRoomItem(roomTitle, "gd", "gd");
                chatRoomItems.add(chatRoomItem);
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//                chatRoomItems.add(roomInfo);
//                adapter.notifyDataSetChanged();
    }
}