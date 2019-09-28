package com.beginagain.yourthinking.Item;

public class ChatRoomItem {
    private String roomTitle, bookTitle, desc;
    private int roomPeopleSetting;
    public  ChatRoomItem(){

    }

    public ChatRoomItem(String roomTitle, String bookTitle, String desc, int roomPeopleSetting) {
        this.roomTitle = roomTitle;
        this.bookTitle = bookTitle;
        this.desc = desc;
        this.roomPeopleSetting = roomPeopleSetting;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRoomPeopleSetting() {
        return roomPeopleSetting;
    }

    public void setRoomPeopleSetting(int roomPeopleSetting) {
        this.roomPeopleSetting = roomPeopleSetting;
    }
}
