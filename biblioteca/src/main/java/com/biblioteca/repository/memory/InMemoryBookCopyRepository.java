package com.biblioteca.repository.memory;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.repository.BookCopyRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookCopyRepository implements BookCopyRepository {
    private final Map<Long, BookCopy> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public BookCopy findById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<BookCopy> findByBookTitleId(Long bookTitleId) {
        return storage.values().stream()
                .filter(copy -> copy.getBookTitleId().equals(bookTitleId))
                .toList();
    }

    @Override
    public BookCopy findByInventoryCode(String inventoryCode) {
        return storage.values().stream()
                .filter(copy -> inventoryCode.equals(copy.getInventoryCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getId() == null) {
            bookCopy.setId(sequence.incrementAndGet());
        }
        storage.put(bookCopy.getId(), bookCopy);
        return bookCopy;
    }

    @Override
    public void updateStatus(Long copyId, CopyStatus status) {
        BookCopy bookCopy = storage.get(copyId);
        if (bookCopy != null) {
            bookCopy.setStatus(status);
        }
    }

    @Override
    public void delete(Long copyId) {
        storage.remove(copyId);
    }
}
