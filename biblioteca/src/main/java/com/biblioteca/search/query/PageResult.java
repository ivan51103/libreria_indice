package com.biblioteca.search.query;

import java.util.List;

public class PageResult<T> {
    private final List<T> items;
    private final long totalItems;
    private final int totalPages;
    private final int page;

    public PageResult(List<T> items, long totalItems, int totalPages, int page) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.page = page;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }
}
