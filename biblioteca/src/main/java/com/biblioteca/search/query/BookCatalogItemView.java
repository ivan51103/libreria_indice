package com.biblioteca.search.query;

public class BookCatalogItemView {
    private Long bookTitleId;
    private String title;
    private String author;
    private String coverPath;
    private String category;
    private int availableCopies;

    public Long getBookTitleId() {
        return bookTitleId;
    }

    public void setBookTitleId(Long bookTitleId) {
        this.bookTitleId = bookTitleId;
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

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
