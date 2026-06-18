package com.biblioteca.repository.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.config.DatabaseInitializer;
import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SQLiteRepositoryTest {
    @TempDir
    Path tempDir;

    private SQLiteBookTitleRepository bookTitleRepository;
    private SQLiteBookCopyRepository bookCopyRepository;
    private SQLiteLocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        ConnectionProvider connectionProvider = new TestConnectionProvider(tempDir.resolve("biblioteca-test.db"));
        new DatabaseInitializer(connectionProvider).initialize();
        bookTitleRepository = new SQLiteBookTitleRepository(connectionProvider);
        bookCopyRepository = new SQLiteBookCopyRepository(connectionProvider);
        locationRepository = new SQLiteLocationRepository(connectionProvider);
    }

    @Test
    void shouldPersistAndUpdateBookTitleLocationAndCopy() {
        BookTitle bookTitle = bookTitleRepository.save(bookTitle("Arquitectura Limpia", "Robert C. Martin", "ISBN-1"));
        Location location = locationRepository.save(location("LOC-1"));
        BookCopy bookCopy = bookCopyRepository.save(bookCopy(bookTitle.getId(), location.getId(), "INV-1", CopyStatus.AVAILABLE));

        assertNotNull(bookTitle.getId());
        assertNotNull(location.getId());
        assertNotNull(bookCopy.getId());
        assertEquals("Arquitectura Limpia", bookTitleRepository.findById(bookTitle.getId()).getTitle());
        assertEquals("LOC-1", locationRepository.findByCode("LOC-1").getCode());
        assertEquals("INV-1", bookCopyRepository.findByInventoryCode("INV-1").getInventoryCode());

        bookTitle.setPublisher("Pearson");
        location.setShelf("B2");
        bookCopy.setNotes("Edicion actualizada");

        bookTitleRepository.save(bookTitle);
        locationRepository.save(location);
        bookCopyRepository.save(bookCopy);
        bookCopyRepository.updateStatus(bookCopy.getId(), CopyStatus.MISSING);

        assertEquals("Pearson", bookTitleRepository.findByIsbn("ISBN-1").getPublisher());
        assertEquals("B2", locationRepository.findById(location.getId()).getShelf());
        assertEquals("Edicion actualizada", bookCopyRepository.findById(bookCopy.getId()).getNotes());
        assertEquals(CopyStatus.MISSING, bookCopyRepository.findById(bookCopy.getId()).getStatus());
    }

    @Test
    void shouldSearchIgnoringCaseAndAccentsWithFiltersAndPagination() {
        bookTitleRepository.save(bookTitle("Introduccion a la Programacion", "Ana Garcia", "ISBN-A"));
        bookTitleRepository.save(bookTitle("Patrones de Diseno", "Erich Gamma", "ISBN-B"));
        bookTitleRepository.save(bookTitle("Bases de Datos", "Álvaro Méndez", "ISBN-C"));

        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setText("datos");
        criteria.setAuthor("alvaro");
        criteria.setCategory("programacion");
        criteria.setCareer("sistemas");

        PageResult<BookTitle> result = bookTitleRepository.search(criteria, pageRequest(0, 10, BookSortField.TITLE, SortDirection.ASC));

        assertEquals(1, result.getTotalItems());
        assertEquals("Bases de Datos", result.getItems().get(0).getTitle());
        assertEquals("Álvaro Méndez", result.getItems().get(0).getAuthor());
    }

    @Test
    void shouldOrderAndPageFindAllResults() {
        bookTitleRepository.save(bookTitle("C", "Autor C", "ISBN-C"));
        bookTitleRepository.save(bookTitle("A", "Autor A", "ISBN-A"));
        bookTitleRepository.save(bookTitle("B", "Autor B", "ISBN-B"));

        PageResult<BookTitle> result = bookTitleRepository.findAll(pageRequest(1, 1, BookSortField.TITLE, SortDirection.ASC));

        assertEquals(3, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getPage());
        assertEquals("B", result.getItems().get(0).getTitle());
    }

    @Test
    void shouldDeleteCopyAndBookTitleWhenRequested() {
        BookTitle bookTitle = bookTitleRepository.save(bookTitle("Libro Temporal", "Autor", "ISBN-T"));
        Location location = locationRepository.save(location("LOC-T"));
        BookCopy bookCopy = bookCopyRepository.save(bookCopy(bookTitle.getId(), location.getId(), "INV-T", CopyStatus.AVAILABLE));

        bookCopyRepository.delete(bookCopy.getId());
        bookTitleRepository.delete(bookTitle.getId());

        assertNull(bookCopyRepository.findById(bookCopy.getId()));
        assertNull(bookTitleRepository.findById(bookTitle.getId()));
    }

    private BookTitle bookTitle(String title, String author, String isbn) {
        BookTitle bookTitle = new BookTitle();
        bookTitle.setTitle(title);
        bookTitle.setAuthor(author);
        bookTitle.setIsbn(isbn);
        bookTitle.setPublisher("Editorial Demo");
        bookTitle.setYear(2024);
        bookTitle.setCategory("Programacion");
        bookTitle.setCareer("Sistemas");
        bookTitle.setDescription("Descripcion");
        bookTitle.setCoverPath("/covers/demo.png");
        return bookTitle;
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

    private BookCopy bookCopy(Long bookTitleId, Long locationId, String inventoryCode, CopyStatus status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBookTitleId(bookTitleId);
        bookCopy.setLocationId(locationId);
        bookCopy.setInventoryCode(inventoryCode);
        bookCopy.setStatus(status);
        bookCopy.setNotes("Disponible en sala");
        return bookCopy;
    }

    private PageRequest pageRequest(int page, int size, BookSortField sortField, SortDirection direction) {
        PageRequest request = new PageRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSortField(sortField);
        request.setDirection(direction);
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
