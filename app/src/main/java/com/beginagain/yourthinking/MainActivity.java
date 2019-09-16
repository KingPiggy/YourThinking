package com.beginagain.yourthinking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.MenuFragment.Menu1Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu2Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu3Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu4Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Menu1Fragment menu1Fragment = new Menu1Fragment();
    private Menu2Fragment menu2Fragment = new Menu2Fragment();
    private Menu3Fragment menu3Fragment = new Menu3Fragment();
    private Menu4Fragment menu4Fragment = new Menu4Fragment();

    Toolbar up_toolbar;

    TextView mToolbarText;
    String page = null;

    private FirebaseAuth mAuth;

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 만들 툴바를 가져와 셋팅한다
        up_toolbar = (Toolbar) findViewById(R.id.up_toolbar);
        mToolbarText = (TextView)findViewById(R.id.text_view_pageTitle);

        setSupportActionBar(up_toolbar);

        mAuth = FirebaseAuth.getInstance();

        fragmentManager.popBackStack();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Intent intent = getIntent();
        page = intent.getStringExtra("page");
        if (page != null) {
            if (page.equals("Chat")) {
                onFragmentChange(2);
            } else if (page.equals("Board")) {
                onFragmentChange(3);
            } else if(page.equals("Recommend")){
                onFragmentChange(1);
            }
        } else {
            transaction.replace(R.id.layout_main_frame, menu1Fragment).commitAllowingStateLoss();
        }
        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_menu1: {
                        transaction.replace(R.id.layout_main_frame, menu1Fragment).commitAllowingStateLoss();
                        mToolbarText.setText("도서추천");
                        break;
                    }
                    case R.id.navigation_menu2: {
                        transaction.replace(R.id.layout_main_frame, menu2Fragment).commitAllowingStateLoss();
                        mToolbarText.setText("채팅방");
                        break;
                    }
                    case R.id.navigation_menu3: {
                        transaction.replace(R.id.layout_main_frame, menu3Fragment).commitAllowingStateLoss();
                        mToolbarText.setText("게시판");
                        break;
                    }
                    case R.id.navigation_menu4: {
                        if(mAuth.getCurrentUser()!=null) {
                            transaction.replace(R.id.layout_main_frame, menu4Fragment).commitAllowingStateLoss();
                            mToolbarText.setText("도서마라톤");
                        }else{
                            AlertDialog.Builder alert_ex = new AlertDialog.Builder(MainActivity.this);
                            alert_ex.setMessage("로그인 후 사용가능합니다. 로그인 하시겠습니까?");

                            alert_ex.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent accountIntent = new Intent(getApplication(), LoginActivity.class);
                                    startActivity(accountIntent);
                                    finish();
                                }
                            });
                            alert_ex.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onFragmentChange(1);
                                }
                            });
                            alert_ex.setTitle("Your Thinking");
                            AlertDialog alert = alert_ex.create();
                            alert.show();
                            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            // startActivity(intent);
                            //finish();

                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void onFragmentChange(int index) {
        if(index==1){
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, menu1Fragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_menu1);
        }
        if (index == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, menu2Fragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_menu2);
        }
        if (index == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, menu3Fragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_menu3);
        }
        if (index == 4) {
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, menu4Fragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_menu4);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // 툴바에 삽입된 메뉴에 대해서 이벤트 처리

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account_icon:
                if(mAuth.getCurrentUser()!=null) {
                    Toast.makeText(getApplicationContext(), "사용자관리", Toast.LENGTH_SHORT).show();
                    Intent accountIntent = new Intent(getApplication(), AccountActivity.class);
                    startActivity(accountIntent);
                }else{
                    AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
                    alert_ex.setMessage("로그인 후 사용가능합니다. 로그인 하시겠습니까?");

                    alert_ex.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent accountIntent = new Intent(getApplication(), LoginActivity.class);
                                startActivity(accountIntent);
                                finish();
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
                return true;
            case R.id.notice_icon:
               // Toast.makeText(getApplicationContext(), "공지사항", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, NoticeActivity.class);
                if(mAuth.getCurrentUser()!=null)
                    intent.putExtra("user", "1");
                else
                    intent.putExtra("user","0");
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
        alert_ex.setMessage("정말로 종료하시겠습니까?");

        alert_ex.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
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
}
