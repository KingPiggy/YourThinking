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
import android.view.MenuItem;

import com.beginagain.yourthinking.MenuFragment.Menu1Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu2Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu3Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu4Fragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Menu1Fragment menu1Fragment = new Menu1Fragment();
    private Menu2Fragment menu2Fragment = new Menu2Fragment();
    private Menu3Fragment menu3Fragment = new Menu3Fragment();
    private Menu4Fragment menu4Fragment = new Menu4Fragment();

    String page = null;

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager.popBackStack();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#82b3c9")); // deep
        }

        // BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // transaction.replace(R.id.layout_main_frame, menu1Fragment).commitAllowingStateLoss();

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
                        break;
                    }
                    case R.id.navigation_menu2: {
                        transaction.replace(R.id.layout_main_frame, menu2Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_menu3: {
                        transaction.replace(R.id.layout_main_frame, menu3Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_menu4: {
                        transaction.replace(R.id.layout_main_frame, menu4Fragment).commitAllowingStateLoss();
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
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
        alert_ex.setMessage("정말로 종료하시겠습니까?");

        alert_ex.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert_ex.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alert_ex.setTitle("Your Thinking");
        AlertDialog alert = alert_ex.create();
        alert.show();
    }
}
