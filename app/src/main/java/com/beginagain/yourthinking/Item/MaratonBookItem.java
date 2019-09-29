package com.beginagain.yourthinking.Item;

public class MaratonBookItem{
    String title, author, publisher, isbn, image, id, date, desc, category, totalPage, readPage, user, pubdate;

    public MaratonBookItem(){}

    public MaratonBookItem(String title, String author, String publisher, String isbn, String image, String category, String totalPage, String readPage,String pubdate) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.image = image;
        this.category = category;
        this.totalPage = totalPage;
        this.readPage = readPage;
        this.pubdate = pubdate;
    }
    public MaratonBookItem(String id, String title, String author, String image, String date, String user, String category, String totalPage, String readPage, String publisher, String isbn, String pubdate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.image = image;
        this.date  = date;
        this.user = user;
        this.category = category;
        this.totalPage = totalPage;
        this.readPage = readPage;
        this.publisher = publisher;
        this.isbn = isbn;
        this.pubdate = pubdate;
    }
    public MaratonBookItem(String category, String Image){
        this.category=category;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage= totalPage;
    }

    public String getReadPage() {
        return readPage;
    }

    public void setReadPage(String readPage) {
        this.readPage= readPage;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate= pubdate;
    }
}