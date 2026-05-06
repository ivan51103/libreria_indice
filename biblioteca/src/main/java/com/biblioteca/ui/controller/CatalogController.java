package com.biblioteca.ui.controller;

import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookDetailViewModel;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.service.CatalogService;

public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public PageResult<BookCatalogItemView> loadCatalog(BookSearchCriteria criteria, PageRequest pageRequest, boolean adminView) {
        return catalogService.getCatalog(criteria, pageRequest, adminView);
    }

    public BookDetailViewModel loadBookDetail(Long bookTitleId, boolean adminView) {
        return catalogService.getBookDetail(bookTitleId, adminView);
    }
}
