package com.beginagain.yourthinking.MenuFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beginagain.yourthinking.Adapter.BookBoardAdapter;
import com.beginagain.yourthinking.Board.WriteActivity;
import com.beginagain.yourthinking.Item.BookBoardItem;
import com.beginagain.yourthinking.LoginActivity;
import com.beginagain.yourthinking.MainActivity;
import com.beginagain.yourthinking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menu3Fragment extends Fragment implements View.OnClickListener {
    View view;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView mMainRecyclerView;
    private BookBoardAdapter mAdapter;
    private List<BookBoardItem> mBoardList = null;
    private List<BookBoardItem> bbList = null;

    private FloatingActionButton fabWrite;

    MainActivity activity;

    // search 기능
    private EditText editSearch;
    private ImageButton btnSearch;
    private String text;
    private String spinnercount =null;

    private Button btnRecTotal, btnRecMonth, btnHome;

    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    SimpleDateFormat mFormat = new SimpleDateFormat("MM-");
    String formatDate = mFormat.format(mDate);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu3, container, false);

        activity.onFragmentChange(3);
        init();

        fabWrite = (FloatingActionButton) view.findViewById(R.id.fab_write);
        fabWrite.setOnClickListener(this);

        return view;
    }
    void init(){
        mMainRecyclerView = view.findViewById(R.id.recycler_Board);
        editSearch = (EditText)view.findViewById(R.id.et_board_search);
        btnSearch = (ImageButton)view.findViewById(R.id.btn_board_search_data);
        btnRecTotal = (Button)view.findViewById(R.id.btn_recommend_all);
        btnRecMonth = (Button)view.findViewById(R.id.btn_recommend_month);
        btnHome = (Button)view.findViewById(R.id.btn_home);
        btnHome.setSelected(true);

        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_board_search);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),R.array.search,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnercount = Integer.toString(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btnSearch.setOnClickListener(this);
        btnRecTotal.setOnClickListener(this);
        btnRecMonth.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        mBoardList = new ArrayList<>();
        bbList = new ArrayList<>();
        // mBoardList.clear();

        mStore.collection("board")
                .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                // 실시간 조회하려고 스냅 사용
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                            String id = (String) dc.getData().get("id");
                            String contents = (String) dc.getData().get("contents");
                            String title = (String) dc.getData().get("title");
                            String name = (String) dc.getData().get("name");
                            String date = (String) dc.getData().get("date");
                            String image = (String) dc.getData().get("image");
                            String author = (String) dc.getData().get("author");
                            String booktitle = (String) dc.getData().get("booktitle");
                            String recommend = String.valueOf(dc.getData().get("recount"));

                            BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                            mBoardList.add(data);
                        }
                        mAdapter = new BookBoardAdapter(mBoardList);
                        mAdapter.notifyDataSetChanged();
                        mMainRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
    public void refresh(){
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_write:
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                if(mAuth.getCurrentUser()!=null) {
                    Toast.makeText(getActivity(), "글 쓰기", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    getActivity().isDestroyed();
                }else {
                    AlertDialog.Builder alert_ex = new AlertDialog.Builder(getActivity());
                    alert_ex.setMessage("로그인 후 사용가능합니다. 로그인 하시겠습니까?");

                    alert_ex.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent accountIntent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(accountIntent);
                            getActivity().isDestroyed();
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
                }
                break;
            case R.id.btn_home:
                btnRecTotal.setSelected(false);
                btnRecMonth.setSelected(false);
                btnHome.setSelected(true);
                btnHome.setTextColor(Color.BLACK);
                btnRecTotal.setTextColor(Color.GRAY);
                btnRecMonth.setTextColor(Color.GRAY);
                mBoardList.clear();
                mBoardList = new ArrayList<>();
                mStore.collection("board")
                        .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                        // 실시간 조회하려고 스냅 사용
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                    String id = (String) dc.getData().get("id");
                                    String contents = (String) dc.getData().get("contents");
                                    String title = (String) dc.getData().get("title");
                                    String name = (String) dc.getData().get("name");
                                    String date = (String) dc.getData().get("date");
                                    String image = (String) dc.getData().get("image");
                                    String author = (String) dc.getData().get("author");
                                    String booktitle = (String) dc.getData().get("booktitle");
                                    String recommend = String.valueOf(dc.getData().get("recount"));

                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                    mBoardList.add(data);
                                }
                                mAdapter = new BookBoardAdapter(mBoardList);
                                mAdapter.notifyDataSetChanged();
                                mMainRecyclerView.setAdapter(mAdapter);
                            }
                        });
                break;
            case R.id.btn_recommend_all:
                btnHome.setTextColor(Color.GRAY);
                btnRecTotal.setTextColor(Color.BLACK);
                btnRecMonth.setTextColor(Color.GRAY);
                btnRecTotal.setSelected(true);
                btnRecMonth.setSelected(false);
                btnHome.setSelected(false);
                mBoardList.clear();
                mBoardList = new ArrayList<>();
                mStore.collection("board").orderBy("recount", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                    String id = (String) dc.getData().get("id");
                                    String title = (String) dc.getData().get("title");
                                    String contents = (String) dc.getData().get("contents");
                                    String name = (String) dc.getData().get("name");
                                    String date = (String) dc.getData().get("date");
                                    String image = (String) dc.getData().get("image");
                                    String author = (String) dc.getData().get("author");
                                    String booktitle = (String) dc.getData().get("booktitle");
                                    String recommend = String.valueOf(dc.getData().get("recount"));

                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                    mBoardList.add(data);
                                }
                                mAdapter = new BookBoardAdapter(mBoardList);
                                mAdapter.notifyDataSetChanged();
                                mMainRecyclerView.setAdapter(mAdapter);
                            }
                        });
                break;
            case R.id.btn_recommend_month:
                btnHome.setTextColor(Color.GRAY);
                btnRecTotal.setTextColor(Color.GRAY);
                btnRecMonth.setTextColor(Color.BLACK);
                btnRecTotal.setSelected(false);
                btnRecMonth.setSelected(true);
                btnHome.setSelected(false);
                mBoardList.clear();
                mBoardList = new ArrayList<>();
                mStore.collection("board").orderBy("recount", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                    String id = (String) dc.getData().get("id");
                                    String date = (String) dc.getData().get("date");
                                    String recommend = String.valueOf(dc.getData().get("recount"));
                                    int intRec = Integer.parseInt(recommend);

                                    if(date.contains(formatDate)&&intRec>0) {
                                        String title = (String) dc.getData().get("title");
                                        String contents = (String) dc.getData().get("contents");
                                        String name = (String) dc.getData().get("name");
                                        String image = (String) dc.getData().get("image");
                                        String author = (String) dc.getData().get("author");
                                        String booktitle = (String) dc.getData().get("booktitle");

                                        BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                        mBoardList.add(data);
                                    }
                                }
                                mAdapter = new BookBoardAdapter(mBoardList);
                                mAdapter.notifyDataSetChanged();
                                mMainRecyclerView.setAdapter(mAdapter);
                            }
                        });
                break;
            case R.id.btn_board_search_data:
                text = editSearch.getText().toString();
                if(editSearch.getText().toString().length()==0){
                    Toast.makeText(getContext(),"아무것도 없어요",Toast.LENGTH_SHORT).show();
                }else{
                    mBoardList = new ArrayList<>();
                    mStore.collection("board")
                            .orderBy("date", Query.Direction.DESCENDING).limit(10000)
                            // 실시간 조회하려고 스냅 사용
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    for (QueryDocumentSnapshot dc : queryDocumentSnapshots) {
                                        String id = (String) dc.getData().get("id");
                                        String title = (String) dc.getData().get("title");
                                        String contents = (String) dc.getData().get("contents");
                                        String name = (String) dc.getData().get("name");
                                        String date = (String) dc.getData().get("date");
                                        String image = (String) dc.getData().get("image");
                                        String author = (String)dc.getData().get("author"); // 8.3
                                        String booktitle = (String)dc.getData().get("booktitle"); // 8.3
                                        String recommend = String.valueOf(dc.getData().get("recount"));

                                        switch (spinnercount) {
                                            case "0":
                                                if(text.toLowerCase().contains(name)||text.toUpperCase().contains(name)||text.contains(name)){
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }else if(name.toLowerCase().contains(text)||name.toUpperCase().contains(text)||name.contains(text)) {
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }
                                                break;
                                            case "1":
                                                if(text.toLowerCase().contains(title)||text.toUpperCase().contains(title)||text.contains(title)){
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }else if(title.toLowerCase().contains(text)||title.toUpperCase().contains(text)||title.contains(text)) {
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date, image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }
                                                break;
                                            case "2":
                                                if(text.toLowerCase().contains(contents)||text.toUpperCase().contains(contents)||text.contains(contents)){
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }else if(contents.toLowerCase().contains(text)||contents.toUpperCase().contains(text)||contents.contains(text)) {
                                                    BookBoardItem data = new BookBoardItem(id, title, contents, name, date ,image, author, booktitle, recommend);
                                                    mBoardList.add(data);
                                                }
                                                break;
                                        }
                                    }
                                    if(mBoardList.size()==0){
                                        Toast.makeText(getContext(), "찾을수가 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    mAdapter = new BookBoardAdapter(mBoardList);
                                    mAdapter.notifyDataSetChanged();
                                    mMainRecyclerView.setAdapter(mAdapter);
                                }
                            });
                }
        break;
        }
    }
}