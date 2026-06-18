package com.biblioteca.repository;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.CopyStatus;
import java.sql.Connection;
import java.util.List;

public interface BookCopyRepository {
    BookCopy findById(Long id);
    List<BookCopy> findByBookTitleId(Long bookTitleId);
    BookCopy findByInventoryCode(String inventoryCode);
    BookCopy save(BookCopy bookCopy);
    default BookCopy save(BookCopy bookCopy, Connection connection) {
        return save(bookCopy);
    }
    void updateStatus(Long copyId, CopyStatus status);
    default void updateStatus(Long copyId, CopyStatus status, Connection connection) {
        updateStatus(copyId, status);
    }
    void delete(Long copyId);
}
