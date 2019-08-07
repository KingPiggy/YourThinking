package com.beginagain.yourthinking.Item;

public class MaratonBookItem {
    String title;
    int total, read;
    boolean state;

    int id;

    public MaratonBookItem() {
    }

    public MaratonBookItem(String title, int total, int read) {
        this.title = title;
        this.total = total;
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }



    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }
}
