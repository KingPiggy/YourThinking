package com.beginagain.yourthinking.Item;

public class MaratonPageItem  {
    String id, date, readPage, uId, point;

    public MaratonPageItem(){}


    public MaratonPageItem(String id, String date, String readPage, String uId, String point) {
        this.id = id;
        this.readPage = readPage;
        this.date  = date;
        this.uId = uId;
        this.point = point;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReadPage() {
        return readPage;
    }

    public void setReadPage(String readPage) {
        this.readPage= readPage;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId= uId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point= point;
    }
}