package com.beginagain.yourthinking.Item;

import android.net.Uri;

public class ChatDTO {
    private String userName;
    private String message;
    private String uid;
    private String time;
    private String image;

    public ChatDTO() {
    }

    public ChatDTO(String userName, String message, String uid, String time, String image) {
        this.userName = userName;
        this.message = message;
        this.uid = uid;
        this.time = time;
        this.image = image;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


