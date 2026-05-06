package com.biblioteca.repository.memory;

import com.biblioteca.domain.BookTitle;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookTitleRepository implements BookTitleRepository {
    private final Map<Long, BookTitle> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public PageResult<BookTitle> findAll(PageRequest pageRequest) {
        return toPage(new ArrayList<>(storage.values()), pageRequest);
    }

    @Override
    public BookTitle findById(Long id) {
        return storage.get(id);
    }

    @Override
    public BookTitle findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        return storage.values().stream()
                .filter(book -> isbn.equals(book.getIsbn()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public PageResult<BookTitle> search(BookSearchCriteria criteria, PageRequest pageRequest) {
        List<BookTitle> filtered = storage.values().stream()
                .filter(book -> matches(book, criteria))
                .toList();
        return toPage(filtered, pageRequest);
    }

    @Override
    public BookTitle save(BookTitle bookTitle) {
        if (bookTitle.getId() == null) {
            bookTitle.setId(sequence.incrementAndGet());
        }
        storage.put(bookTitle.getId(), bookTitle);
        return bookTitle;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    private boolean matches(BookTitle book, BookSearchCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        return contains(book.getTitle(), criteria.getText())
                && contains(book.getAuthor(), criteria.getAuthor())
                && contains(book.getCategory(), criteria.getCategory())
                && contains(book.getCareer(), criteria.getCareer());
    }

    private boolean contains(String field, String criteria) {
        String normalizedCriteria = normalize(criteria);
        if (normalizedCriteria.isBlank()) {
            return true;
        }
        return normalize(field).contains(normalizedCriteria);
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    private PageResult<BookTitle> toPage(List<BookTitle> books, PageRequest pageRequest) {
        List<BookTitle> sorted = new ArrayList<>(books);
        sorted.sort(buildComparator(pageRequest));

        int size = pageRequest != null && pageRequest.getSize() > 0 ? pageRequest.getSize() : 10;
        int page = pageRequest != null ? Math.max(pageRequest.getPage(), 0) : 0;
        int fromIndex = Math.min(page * size, sorted.size());
        int toIndex = Math.min(fromIndex + size, sorted.size());
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) sorted.size() / size);

        return new PageResult<>(sorted.subList(fromIndex, toIndex), sorted.size(), totalPages, page);
    }

    private Comparator<BookTitle> buildComparator(PageRequest pageRequest) {
        BookSortField sortField = pageRequest != null && pageRequest.getSortField() != null
                ? pageRequest.getSortField()
                : BookSortField.TITLE;

        Comparator<BookTitle> comparator = switch (sortField) {
            case AUTHOR -> Comparator.comparing(BookTitle::getAuthor, Comparator.nullsLast(String::compareToIgnoreCase));
            case YEAR -> Comparator.comparingInt(BookTitle::getYear);
            case CATEGORY -> Comparator.comparing(BookTitle::getCategory, Comparator.nullsLast(String::compareToIgnoreCase));
            case TITLE -> Comparator.comparing(BookTitle::getTitle, Comparator.nullsLast(String::compareToIgnoreCase));
        };

        SortDirection direction = pageRequest != null && pageRequest.getDirection() != null
                ? pageRequest.getDirection()
                : SortDirection.ASC;
        return direction == SortDirection.DESC ? comparator.reversed() : comparator;
    }
}
