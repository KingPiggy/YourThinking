package com.beginagain.yourthinking.Item;

public class BookReplyItem {

    private String id;
    private String name;
    private String contents;
    private String date;
    private String image;
    private String uid;

    public BookReplyItem() {
    }

    public BookReplyItem(String id, String name, String contents, String date, String image, String uid) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.date = date;
        this.image = image;
        this.uid = uid;
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

    public String getImage(){return image;}

    public void setImage(){this.image = image;}

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "BookReplyItem{" +
                ", id='" + id + '\''+
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", date='" +date + '\'' +
                ",image='"+image + '\'' +
                '}';
    }
}

