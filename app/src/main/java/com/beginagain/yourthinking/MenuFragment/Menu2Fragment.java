package com.beginagain.yourthinking.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.beginagain.yourthinking.MainActivity;
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
import java.util.ConcurrentModificationException;

import static android.app.Activity.RESULT_OK;

public class Menu2Fragment extends Fragment {
    View view;

    FloatingActionButton mFloatingActionButton;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatRoomItem> chatRoomItems = new ArrayList<>();
    private ArrayList<Integer> uidCounts = new ArrayList<>();

    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu2, container, false);

        init();

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        adapter = new ChatRoomAdapter(getActivity(), chatRoomItems, uidCounts, R.layout.fragment_menu2);
        mRecyclerView.setAdapter(adapter);

        showChatList();

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeChatRoomActivity.class);
                startActivity(intent);
            }
        });

        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    void init() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_make_chat_chatlist);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_chat_add);
    }

    private void showChatList() {
        chatRoomItems.clear();
        uidCounts.clear();

        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();

                ChatRoomItem chatRoomItem = dataSnapshot.child("info").getValue(ChatRoomItem.class);
                int count = (int) dataSnapshot.child("people").getChildrenCount();


                if (chatRoomItem != null) {
                    chatRoomItems.add(chatRoomItem);
                    uidCounts.add(count);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                ChatRoomItem chatRoomItem = dataSnapshot.child("info").getValue(ChatRoomItem.class);
//                String toDeleteTitle = chatRoomItem.getRoomTitle();
//                for (ChatRoomItem a : chatRoomItems) {
//                    if (a.getRoomTitle().equals(toDeleteTitle))
//                        chatRoomItems.remove(a);
//                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}