package com.biblioteca.service;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.BookCopyRepository;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.repository.LocationRepository;
import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookDetailViewModel;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CatalogService {
    private final BookTitleRepository bookTitleRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LocationRepository locationRepository;

    public CatalogService(BookTitleRepository bookTitleRepository,
                          BookCopyRepository bookCopyRepository,
                          LocationRepository locationRepository) {
        this.bookTitleRepository = bookTitleRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.locationRepository = locationRepository;
    }

    public PageResult<BookCatalogItemView> getCatalog(BookSearchCriteria criteria, PageRequest pageRequest, boolean adminView) {
        List<BookTitle> titles = loadMatchingTitles(criteria, pageRequest);
        List<BookCatalogItemView> items = titles.stream()
                .map(bookTitle -> toCatalogItem(bookTitle, adminView))
                .filter(Objects::nonNull)
                .toList();

        return toPage(items, pageRequest);
    }

    public BookDetailViewModel getBookDetail(Long bookTitleId, boolean adminView) {
        BookTitle bookTitle = bookTitleRepository.findById(bookTitleId);
        if (bookTitle == null) {
            return null;
        }

        List<BookCopy> copies = filterCopies(bookCopyRepository.findByBookTitleId(bookTitleId), adminView);
        if (!adminView && copies.isEmpty()) {
            return null;
        }

        List<Location> locations = new ArrayList<>();
        for (BookCopy copy : copies) {
            Location location = locationRepository.findById(copy.getLocationId());
            if (location != null && locations.stream().noneMatch(item -> Objects.equals(item.getId(), location.getId()))) {
                locations.add(location);
            }
        }

        BookDetailViewModel detail = new BookDetailViewModel();
        detail.setBookTitle(bookTitle);
        detail.setCopies(copies);
        detail.setLocations(locations);
        return detail;
    }

    public List<String> getCareers() {
        return bookTitleRepository.findAllCareers();
    }

    private boolean hasSearchCriteria(BookSearchCriteria criteria) {
        if (criteria == null) {
            return false;
        }
        return !isBlank(criteria.getText())
                || (criteria.getCareers() != null && !criteria.getCareers().isEmpty())
                || (criteria.getFirstLetters() != null && !criteria.getFirstLetters().isEmpty());
    }

    private boolean isBlank(String value) {
        return value == null || normalize(value).isBlank();
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    private BookCatalogItemView toCatalogItem(BookTitle bookTitle, boolean adminView) {
        List<BookCopy> copies = filterCopies(bookCopyRepository.findByBookTitleId(bookTitle.getId()), adminView);
        if (copies.isEmpty()) {
            return null;
        }

        long availableCopies = copies.stream()
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .count();

        BookCatalogItemView view = new BookCatalogItemView();
        view.setBookTitleId(bookTitle.getId());
        view.setTitle(bookTitle.getTitle());
        view.setAuthor(bookTitle.getAuthor());
        view.setCoverPath(bookTitle.getCoverPath());
        view.setCategory(bookTitle.getCategory());
        view.setAvailableCopies((int) availableCopies);
        return view;
    }

    private List<BookTitle> loadMatchingTitles(BookSearchCriteria criteria, PageRequest pageRequest) {
        PageRequest fullRequest = new PageRequest();
        fullRequest.setPage(0);
        fullRequest.setSize(10_000);
        fullRequest.setSortField(pageRequest != null ? pageRequest.getSortField() : null);
        fullRequest.setDirection(pageRequest != null ? pageRequest.getDirection() : null);

        PageResult<BookTitle> page = hasSearchCriteria(criteria)
                ? bookTitleRepository.search(criteria, fullRequest)
                : bookTitleRepository.findAll(fullRequest);
        return page.getItems();
    }

    private List<BookCopy> filterCopies(List<BookCopy> copies, boolean adminView) {
        if (adminView) {
            return copies;
        }
        return copies.stream()
                .filter(copy -> copy.getStatus() != CopyStatus.REMOVED)
                .toList();
    }

    private <T> PageResult<T> toPage(List<T> items, PageRequest pageRequest) {
        int size = pageRequest != null && pageRequest.getSize() > 0 ? pageRequest.getSize() : 10;
        int page = pageRequest != null ? Math.max(pageRequest.getPage(), 0) : 0;
        int fromIndex = Math.min(page * size, items.size());
        int toIndex = Math.min(fromIndex + size, items.size());
        int totalPages = size <= 0 ? 1 : (int) Math.ceil((double) items.size() / size);
        return new PageResult<>(items.subList(fromIndex, toIndex), items.size(), totalPages, page);
    }
}
