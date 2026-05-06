package com.biblioteca.search.query;

public class PageRequest {
    private int page;
    private int size;
    private BookSortField sortField;
    private SortDirection direction;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public BookSortField getSortField() {
        return sortField;
    }

    public void setSortField(BookSortField sortField) {
        this.sortField = sortField;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}
