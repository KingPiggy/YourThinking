package com.beginagain.yourthinking.Item;

public class RecommendBookItem {
    String title, author, publisher, isbn, image, id, date, desc, category;

    public RecommendBookItem(){}

    public RecommendBookItem(String title, String author, String publisher, String isbn, String image) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.image = image;
    }
    public RecommendBookItem(String title, String author, String publisher, String isbn, String image, String id) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.image = image;
        this.id = id;
    }
    public RecommendBookItem(String title, String author, String publisher, String isbn, String image, String date, String desc) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.image = image;
        this.id = id;
        this.date = date;
        this.desc = desc;
    }
    public RecommendBookItem(String title, String author, String image, String category) {
        this.title = title;
        this.author = author;
        this.image = image;
        this.category = category;
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
}