package com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.memory.InMemoryBookCopyRepository;
import com.biblioteca.repository.memory.InMemoryBookTitleRepository;
import com.biblioteca.repository.memory.InMemoryLocationRepository;
import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookDetailViewModel;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {
    private InMemoryBookTitleRepository bookTitleRepository;
    private InMemoryBookCopyRepository bookCopyRepository;
    private InMemoryLocationRepository locationRepository;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        bookTitleRepository = new InMemoryBookTitleRepository();
        bookCopyRepository = new InMemoryBookCopyRepository();
        locationRepository = new InMemoryLocationRepository();
        catalogService = new CatalogService(bookTitleRepository, bookCopyRepository, locationRepository, new SearchService());
    }

    @Test
    void guestShouldNotSeeRemovedOnlyTitles() {
        BookTitle removedTitle = saveTitle("Libro Retirado");
        saveCopy(removedTitle.getId(), saveLocation("LOC-REM"), "INV-REM", CopyStatus.REMOVED);

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(null, pageRequest(), false);

        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void adminShouldSeeRemovedOnlyTitles() {
        BookTitle removedTitle = saveTitle("Libro Retirado");
        saveCopy(removedTitle.getId(), saveLocation("LOC-REM"), "INV-REM", CopyStatus.REMOVED);

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(null, pageRequest(), true);

        assertEquals(1, result.getItems().size());
        assertEquals("Libro Retirado", result.getItems().get(0).getTitle());
    }

    @Test
    void guestDetailShouldHideRemovedCopies() {
        BookTitle title = saveTitle("Libro Mixto");
        Location location = saveLocation("LOC-MIX");
        saveCopy(title.getId(), location, "INV-1", CopyStatus.AVAILABLE);
        saveCopy(title.getId(), location, "INV-2", CopyStatus.REMOVED);

        BookDetailViewModel detail = catalogService.getBookDetail(title.getId(), false);

        assertNotNull(detail);
        assertEquals(1, detail.getCopies().size());
        assertEquals("INV-1", detail.getCopies().get(0).getInventoryCode());
    }

    @Test
    void adminDetailShouldShowRemovedCopies() {
        BookTitle title = saveTitle("Libro Mixto");
        Location location = saveLocation("LOC-MIX");
        saveCopy(title.getId(), location, "INV-1", CopyStatus.AVAILABLE);
        saveCopy(title.getId(), location, "INV-2", CopyStatus.REMOVED);

        BookDetailViewModel detail = catalogService.getBookDetail(title.getId(), true);

        assertNotNull(detail);
        assertEquals(2, detail.getCopies().size());
    }

    @Test
    void availableOnlyShouldKeepOnlyTitlesWithAvailableCopies() {
        BookTitle availableTitle = saveTitle("Disponible");
        BookTitle repairTitle = saveTitle("En Reparacion");
        Location location = saveLocation("LOC-A");
        saveCopy(availableTitle.getId(), location, "INV-A", CopyStatus.AVAILABLE);
        saveCopy(repairTitle.getId(), location, "INV-B", CopyStatus.REPAIR);

        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setAvailableOnly(true);

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(criteria, pageRequest(), false);

        assertEquals(1, result.getItems().size());
        assertEquals("Disponible", result.getItems().get(0).getTitle());
    }

    @Test
    void shouldApplyCategoryAndCareerFiltersFromCatalogService() {
        BookTitle systemsTitle = saveTitle("Estructuras de Datos", "Programacion", "Sistemas");
        BookTitle lawTitle = saveTitle("Derecho Civil", "Derecho", "Derecho");
        Location location = saveLocation("LOC-F");
        saveCopy(systemsTitle.getId(), location, "INV-SYS", CopyStatus.AVAILABLE);
        saveCopy(lawTitle.getId(), location, "INV-LAW", CopyStatus.AVAILABLE);

        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setCategory("programacion");
        criteria.setCareer("sistemas");

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(criteria, pageRequest(), false);

        assertEquals(1, result.getItems().size());
        assertEquals("Estructuras de Datos", result.getItems().get(0).getTitle());
    }

    @Test
    void availableOnlyShouldIgnoreMissingAndRepairCopies() {
        BookTitle mixedTitle = saveTitle("Redes");
        Location location = saveLocation("LOC-N");
        saveCopy(mixedTitle.getId(), location, "INV-MISS", CopyStatus.MISSING);
        saveCopy(mixedTitle.getId(), location, "INV-REP", CopyStatus.REPAIR);

        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setAvailableOnly(true);

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(criteria, pageRequest(), false);

        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void shouldPageCatalogAfterHidingRemovedTitles() {
        BookTitle visibleA = saveTitle("A Visible");
        BookTitle removed = saveTitle("B Retirado");
        BookTitle visibleC = saveTitle("C Visible");
        Location location = saveLocation("LOC-P");
        saveCopy(visibleA.getId(), location, "INV-A", CopyStatus.AVAILABLE);
        saveCopy(removed.getId(), location, "INV-B", CopyStatus.REMOVED);
        saveCopy(visibleC.getId(), location, "INV-C", CopyStatus.AVAILABLE);

        PageRequest request = pageRequest();
        request.setPage(1);
        request.setSize(1);

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(null, request, false);

        assertEquals(2, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
        assertEquals("C Visible", result.getItems().get(0).getTitle());
    }

    @Test
    void guestShouldReceiveNullDetailForFullyRemovedTitle() {
        BookTitle removedTitle = saveTitle("Solo Retirado");
        saveCopy(removedTitle.getId(), saveLocation("LOC-Z"), "INV-Z", CopyStatus.REMOVED);

        assertNull(catalogService.getBookDetail(removedTitle.getId(), false));
    }

    private PageRequest pageRequest() {
        PageRequest request = new PageRequest();
        request.setPage(0);
        request.setSize(20);
        request.setSortField(BookSortField.TITLE);
        request.setDirection(SortDirection.ASC);
        return request;
    }

    private BookTitle saveTitle(String title) {
        return saveTitle(title, "Categoria", "Carrera");
    }

    private BookTitle saveTitle(String title, String category, String career) {
        BookTitle bookTitle = new BookTitle();
        bookTitle.setTitle(title);
        bookTitle.setAuthor("Autor");
        bookTitle.setCategory(category);
        bookTitle.setCareer(career);
        return bookTitleRepository.save(bookTitle);
    }

    private Location saveLocation(String code) {
        Location location = new Location();
        location.setRoom("Sala");
        location.setSection("General");
        location.setShelf("A1");
        location.setCode(code);
        return locationRepository.save(location);
    }

    private void saveCopy(Long titleId, Location location, String inventoryCode, CopyStatus status) {
        BookCopy copy = new BookCopy();
        copy.setBookTitleId(titleId);
        copy.setLocationId(location.getId());
        copy.setInventoryCode(inventoryCode);
        copy.setStatus(status);
        bookCopyRepository.save(copy);
    }
}
