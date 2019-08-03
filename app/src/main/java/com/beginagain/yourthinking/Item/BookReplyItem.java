package com.beginagain.yourthinking.Item;

public class BookReplyItem {

    private String id;
    private String name;
    private String contents;
    private String date;

    public BookReplyItem() {
    }

    public BookReplyItem(String id, String name, String contents, String date) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.date = date;

    }

    public String getId(){return id;}

    public void setId(String id){this.id=id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BookReplyItem{" +
                ", id='" + id + '\''+
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", date='" +date + '\'' +
                '}';
    }
}

