package com.beginagain.yourthinking.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.ChatActivity;
import com.beginagain.yourthinking.MakeChatRoomActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Menu2Fragment extends Fragment {
    View view;

    Button mRoomMakeBtn, mRoomSearchBtn;
    ListView mChatRoomListView;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();
    // private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu2, container, false);

        init();

        showChatList();

        mRoomMakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeChatRoomActivity.class);
                startActivity(intent);
            }
        });

        mChatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chatRoomName = ((TextView) view).getText().toString();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatRoomName", chatRoomName);
                startActivity(intent);
            }
        });

        return view;
    }

    void init() {
        mRoomMakeBtn = (Button) view.findViewById(R.id.btn_make_chat_makeroom);
        mRoomSearchBtn = (Button) view.findViewById(R.id.btn_make_chat_searchroom);
        mChatRoomListView = (ListView) view.findViewById(R.id.listview_make_chat_chatlist);
    }

    private void showChatList() {
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, room);
        arrayAdapter.clear();
        mChatRoomListView.setAdapter(arrayAdapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                arrayAdapter.add(dataSnapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayAdapter.notifyDataSetChanged();
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