package com.biblioteca.repository;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.CopyStatus;
import java.util.List;

public interface BookCopyRepository {
    BookCopy findById(Long id);
    List<BookCopy> findByBookTitleId(Long bookTitleId);
    BookCopy findByInventoryCode(String inventoryCode);
    BookCopy save(BookCopy bookCopy);
    void updateStatus(Long copyId, CopyStatus status);
    void delete(Long copyId);
}
