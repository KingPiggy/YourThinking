package com.beginagain.yourthinking;

import android.app.Application;

public class MyVariable extends Application {
    private int state;
    private String tag = "Hoon";

    @Override
    public void onCreate() {
        state = 0;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setState(int state){
        this.state = state;
    }

    public int getState(){
        return state;
    }

    public String getTag(){
        return tag;
    }
}
