package com.beginagain.yourthinking.Item;

public class BookBoardItem {

    private String id;
    private String title;
    private String contents;
    private String name;
    private String date;
    private String image;
    private String author;
    private String booktitle;
    private String recommend;

    public BookBoardItem() {
    }

    public BookBoardItem(String id, String title, String contents, String name, String date) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
    }

    public BookBoardItem(String id, String title, String contents, String name, String date, String recommend ) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
        this.recommend = recommend;
    }
    public BookBoardItem(String id, String title, String contents, String name, String date, String image, String author, String booktitle, String recommend ){
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
        this.image = image;
        this.author = author;
        this.booktitle = booktitle;
        this.recommend = recommend;
    }
    public BookBoardItem(String id, String title, String contents, String name, String date, String image, String author, String booktitle){
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.date = date;
        this.image = image;
        this.author = author;
        this.booktitle = booktitle;
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

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getBooktitle(){
        return booktitle;
    }

    public void setBooktitle(String booktitle){
        this.booktitle = booktitle;
    }

    public String getRecommend(){
        return recommend;
    }

    public void setRecommend(String recommend){
        this.recommend = recommend;
    }

    @Override
    public String toString() {
        return "BookBoardItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", date='" +date + '\'' +
                ", image='"+image+'\''+
                ", author='"+author+'\''+
                ", booktitle='"+booktitle+'\''+
                '}';
    }
}
