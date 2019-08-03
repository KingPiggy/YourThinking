package com.beginagain.yourthinking.Item;

public class BookBoardItem {

    private String id;
    private String title;
    private String contents;
    private String name;
    private String date;
    private String image;

    public BookBoardItem() {
    }

    public BookBoardItem(String id, String title, String contents, String name, String date) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
    }
    public BookBoardItem(String id, String title, String contents, String name, String date, String image){
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }
/**
    @Override
    public String toString() {
        return "BookBoardItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", date='" +date + '\'' +
                '}';
    }**/

    @Override
    public String toString() {
        return "BookBoardItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", date='" +date + '\'' +
                ", count='"+image+'\''+
                '}';
    }
}
