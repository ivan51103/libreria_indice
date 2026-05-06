package com.biblioteca.service;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.repository.BookCopyRepository;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import java.util.List;

public class BookCatalogService {
    private final BookTitleRepository bookTitleRepository;
    private final BookCopyRepository bookCopyRepository;
    private final SearchService searchService;

    public BookCatalogService(BookTitleRepository bookTitleRepository,
                              BookCopyRepository bookCopyRepository,
                              SearchService searchService) {
        this.bookTitleRepository = bookTitleRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.searchService = searchService;
    }

    public PageResult<BookCatalogItemView> listBooks(PageRequest pageRequest) {
        return map(bookTitleRepository.findAll(pageRequest));
    }

    public PageResult<BookCatalogItemView> searchBooks(BookSearchCriteria criteria, PageRequest pageRequest) {
        if (criteria == null || searchService.normalize(criteria.getText()).isBlank()) {
            return listBooks(pageRequest);
        }
        return map(bookTitleRepository.search(criteria, pageRequest));
    }

    private PageResult<BookCatalogItemView> map(PageResult<BookTitle> titles) {
        List<BookCatalogItemView> items = titles.getItems().stream()
                .map(this::toView)
                .toList();
        return new PageResult<>(items, titles.getTotalItems(), titles.getTotalPages(), titles.getPage());
    }

    private BookCatalogItemView toView(BookTitle bookTitle) {
        List<BookCopy> copies = bookCopyRepository.findByBookTitleId(bookTitle.getId());
        int availableCopies = (int) copies.stream()
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .count();

        BookCatalogItemView view = new BookCatalogItemView();
        view.setBookTitleId(bookTitle.getId());
        view.setTitle(bookTitle.getTitle());
        view.setAuthor(bookTitle.getAuthor());
        view.setCoverPath(bookTitle.getCoverPath());
        view.setCategory(bookTitle.getCategory());
        view.setAvailableCopies(availableCopies);
        return view;
    }
}
