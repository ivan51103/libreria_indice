package com.biblioteca.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.config.DatabaseInitializer;
import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.sqlite.SQLiteBookCopyRepository;
import com.biblioteca.repository.sqlite.SQLiteBookTitleRepository;
import com.biblioteca.repository.sqlite.SQLiteLocationRepository;
import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookDetailViewModel;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import com.biblioteca.service.CatalogService;
import com.biblioteca.service.InventoryService;
import com.biblioteca.service.SearchService;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InventoryCatalogSQLiteIntegrationTest {
    @TempDir
    Path tempDir;

    private SQLiteBookTitleRepository bookTitleRepository;
    private SQLiteBookCopyRepository bookCopyRepository;
    private SQLiteLocationRepository locationRepository;
    private InventoryService inventoryService;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        ConnectionProvider connectionProvider = new TestConnectionProvider(tempDir.resolve("biblioteca-integration.db"));
        new DatabaseInitializer(connectionProvider).initialize();
        bookTitleRepository = new SQLiteBookTitleRepository(connectionProvider);
        bookCopyRepository = new SQLiteBookCopyRepository(connectionProvider);
        locationRepository = new SQLiteLocationRepository(connectionProvider);
        inventoryService = new InventoryService(bookTitleRepository, bookCopyRepository, locationRepository);
        catalogService = new CatalogService(bookTitleRepository, bookCopyRepository, locationRepository, new SearchService());
    }

    @Test
    void shouldRegisterAndFindBookWithPhysicalLocationFromSQLite() {
        BookTitle bookTitle = bookTitle("Sistemas Distribuidos", "ISBN-SD");
        BookCopy bookCopy = bookCopy("INV-SD", CopyStatus.AVAILABLE);
        Location location = location("LOC-SD");

        inventoryService.registerBook(bookTitle, bookCopy, location);

        PageResult<BookCatalogItemView> catalog = catalogService.getCatalog(null, pageRequest(), false);
        BookDetailViewModel detail = catalogService.getBookDetail(bookTitle.getId(), false);

        assertEquals(1, catalog.getItems().size());
        assertEquals("Sistemas Distribuidos", catalog.getItems().get(0).getTitle());
        assertEquals(1, catalog.getItems().get(0).getAvailableCopies());
        assertNotNull(detail);
        assertEquals("INV-SD", detail.getCopies().get(0).getInventoryCode());
        assertEquals("LOC-SD", detail.getLocations().get(0).getCode());
    }

    @Test
    void shouldApplyStatusVisibilityAfterAdminChangesCopyStatus() {
        BookTitle bookTitle = bookTitle("Libro de Baja Logica", "ISBN-BL");
        BookCopy bookCopy = bookCopy("INV-BL", CopyStatus.AVAILABLE);
        Location location = location("LOC-BL");
        inventoryService.registerBook(bookTitle, bookCopy, location);

        inventoryService.changeCopyStatus(bookCopy.getId(), CopyStatus.REMOVED);

        PageResult<BookCatalogItemView> guestCatalog = catalogService.getCatalog(null, pageRequest(), false);
        PageResult<BookCatalogItemView> adminCatalog = catalogService.getCatalog(null, pageRequest(), true);

        assertEquals(0, guestCatalog.getItems().size());
        assertNull(catalogService.getBookDetail(bookTitle.getId(), false));
        assertEquals(1, adminCatalog.getItems().size());
        assertEquals(0, adminCatalog.getItems().get(0).getAvailableCopies());
        assertEquals(CopyStatus.REMOVED, catalogService.getBookDetail(bookTitle.getId(), true).getCopies().get(0).getStatus());
    }

    @Test
    void shouldSearchPublicCatalogWithPartialTextAfterPersistingChanges() {
        inventoryService.registerBook(bookTitle("Analisis de Algoritmos", "ISBN-AA"), bookCopy("INV-AA", CopyStatus.AVAILABLE), location("LOC-AA"));
        inventoryService.registerBook(bookTitle("Historia Universal", "ISBN-HU"), bookCopy("INV-HU", CopyStatus.AVAILABLE), location("LOC-HU"));

        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setText("algorit");

        PageResult<BookCatalogItemView> result = catalogService.getCatalog(criteria, pageRequest(), false);

        assertEquals(1, result.getItems().size());
        assertEquals("Analisis de Algoritmos", result.getItems().get(0).getTitle());
    }

    private BookTitle bookTitle(String title, String isbn) {
        BookTitle bookTitle = new BookTitle();
        bookTitle.setTitle(title);
        bookTitle.setAuthor("Autor Demo");
        bookTitle.setIsbn(isbn);
        bookTitle.setPublisher("Editorial Demo");
        bookTitle.setYear(2024);
        bookTitle.setCategory("Computacion");
        bookTitle.setCareer("Sistemas");
        return bookTitle;
    }

    private BookCopy bookCopy(String inventoryCode, CopyStatus status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setInventoryCode(inventoryCode);
        bookCopy.setStatus(status);
        return bookCopy;
    }

    private Location location(String code) {
        Location location = new Location();
        location.setRoom("Sala A");
        location.setSection("General");
        location.setShelf("A1");
        location.setLevel("1");
        location.setPosition("01");
        location.setCode(code);
        return location;
    }

    private PageRequest pageRequest() {
        PageRequest request = new PageRequest();
        request.setPage(0);
        request.setSize(20);
        request.setSortField(BookSortField.TITLE);
        request.setDirection(SortDirection.ASC);
        return request;
    }

    private static class TestConnectionProvider extends ConnectionProvider {
        private final String databaseUrl;

        TestConnectionProvider(Path databasePath) {
            this.databaseUrl = "jdbc:sqlite:" + databasePath;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(databaseUrl);
        }

        @Override
        public String getDatabaseUrl() {
            return databaseUrl;
        }
    }
}
