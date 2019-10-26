package com.beginagain.yourthinking.MenuFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private EditText editSearch;
    private ImageButton mSearchBtn;
    private int idx;
    private HashMap<String, Integer> map;
    private String keySet[];
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

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr  = editSearch.getText().toString();
                map = new HashMap<>();

                for (ChatRoomItem c : chatRoomItems){
                    if(c.getRoomTitle().contains(searchStr)){
                        map.put(c.getRoomTitle(), chatRoomItems.indexOf(c));
                    }

                }

                idx = 0;
                keySet = new String[map.size()];
                for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
                    keySet[idx] = mapEntry.getKey();
                    idx++;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("검색 결과")        // 제목 설정
                        .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setItems(keySet, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                String selectedTitle = keySet[pos];
                                idx = map.get(selectedTitle);

                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.scrollToPosition(idx);
                                    }
                                });
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                        mSearchBtn.performClick();
                }
                return false;
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
        editSearch = (EditText) view.findViewById(R.id.et_menu2_search);
        mSearchBtn = (ImageButton) view.findViewById(R.id.btn_menu2_search);
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