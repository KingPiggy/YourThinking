package com.beginagain.yourthinking.MenuFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.beginagain.yourthinking.MakeChatRoomInfoActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Menu2Fragment extends Fragment {
    View view;

    Button mRoomMakeBtn, mRoomSearchBtn;
    ListView mChatRoomListView;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu2, container, false);

        init();

        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, room);
        mChatRoomListView.setAdapter(arrayAdapter);

        mRoomMakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeChatRoomInfoActivity.class);
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
}