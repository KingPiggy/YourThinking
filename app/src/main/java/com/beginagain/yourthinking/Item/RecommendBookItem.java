package com.beginagain.yourthinking.Item;

public class RecommendBookItem {
    String title, author, company, isbn, image;

    public RecommendBookItem(){}

    public RecommendBookItem(String title, String author, String company, String isbn, String image) {
        this.title = title;
        this.author = author;
        this.company = company;
        this.isbn = isbn;
        this.image = image;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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
}
