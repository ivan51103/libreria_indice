package com.biblioteca.repository;

import com.biblioteca.domain.BookTitle;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import java.sql.Connection;
import java.util.List;

public interface BookTitleRepository {
    PageResult<BookTitle> findAll(PageRequest pageRequest);
    BookTitle findById(Long id);
    BookTitle findByIsbn(String isbn);
    PageResult<BookTitle> search(BookSearchCriteria criteria, PageRequest pageRequest);
    List<String> findAllCareers();
    BookTitle save(BookTitle bookTitle);
    default BookTitle save(BookTitle bookTitle, Connection connection) {
        return save(bookTitle);
    }
    void delete(Long id);
}
