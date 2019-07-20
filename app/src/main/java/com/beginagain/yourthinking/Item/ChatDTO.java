package com.beginagain.yourthinking.Item;

public class ChatDTO {
    private String userName;
    private String message;
    private String uid;

    public ChatDTO() {
    }

    public ChatDTO(String userName, String message, String uid) {
        this.userName = userName;
        this.message = message;
        this.uid = uid;
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
}
