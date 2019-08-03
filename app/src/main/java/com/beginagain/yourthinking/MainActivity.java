package com.beginagain.yourthinking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.beginagain.yourthinking.MenuFragment.Menu1Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu2Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu3Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu4Fragment;
import com.beginagain.yourthinking.MenuFragment.Menu5Fragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Menu1Fragment menu1Fragment = new Menu1Fragment();
    private Menu2Fragment menu2Fragment = new Menu2Fragment();
    private Menu3Fragment menu3Fragment = new Menu3Fragment();
    private Menu4Fragment menu4Fragment = new Menu4Fragment();
    private Menu5Fragment menu5Fragment = new Menu5Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_main_frame, menu1Fragment).commitAllowingStateLoss();

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
                    case R.id.navigation_menu5: {
                        transaction.replace(R.id.layout_main_frame, menu5Fragment).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });


    }
}
