package com.beginagain.yourthinking.Item;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatPeople {
    private String userName;
    private String uid;
    private String image;

    public ChatPeople() {
    }

    public ChatPeople(String userName, String uid, String image) {
        this.userName = userName;
        this.uid = uid;
        this.image = image;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("uid", uid);
        result.put("image", image);
        return result;
    }
}


