package com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.memory.InMemoryBookCopyRepository;
import com.biblioteca.repository.memory.InMemoryBookTitleRepository;
import com.biblioteca.repository.memory.InMemoryLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {
    private InMemoryBookTitleRepository bookTitleRepository;
    private InMemoryBookCopyRepository bookCopyRepository;
    private InMemoryLocationRepository locationRepository;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        bookTitleRepository = new InMemoryBookTitleRepository();
        bookCopyRepository = new InMemoryBookCopyRepository();
        locationRepository = new InMemoryLocationRepository();
        inventoryService = new InventoryService(bookTitleRepository, bookCopyRepository, locationRepository);
    }

    @Test
    void shouldRegisterBookAndDefaultCopyStatusToAvailable() {
        BookTitle bookTitle = buildBookTitle("Clean Architecture", "9780134494166");
        BookCopy bookCopy = buildBookCopy("INV-1001", null);
        Location location = buildLocation("LOC-1001");

        inventoryService.registerBook(bookTitle, bookCopy, location);

        assertNotNull(bookTitle.getId());
        assertNotNull(bookCopy.getId());
        assertNotNull(location.getId());
        assertEquals(CopyStatus.AVAILABLE, bookCopy.getStatus());
        assertEquals(bookTitle.getId(), bookCopy.getBookTitleId());
        assertEquals(location.getId(), bookCopy.getLocationId());
    }

    @Test
    void shouldRejectDuplicateInventoryCode() {
        inventoryService.registerBook(
                buildBookTitle("Libro Uno", "ISBN-1"),
                buildBookCopy("INV-DUP", CopyStatus.AVAILABLE),
                buildLocation("LOC-1"));

        BusinessException exception = assertThrows(BusinessException.class, () -> inventoryService.registerBook(
                buildBookTitle("Libro Dos", "ISBN-2"),
                buildBookCopy("INV-DUP", CopyStatus.AVAILABLE),
                buildLocation("LOC-2")));

        assertEquals("Ya existe otro ejemplar con ese codigo de inventario.", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateLocationCode() {
        inventoryService.registerBook(
                buildBookTitle("Libro Uno", "ISBN-A"),
                buildBookCopy("INV-A", CopyStatus.AVAILABLE),
                buildLocation("LOC-DUP"));

        BusinessException exception = assertThrows(BusinessException.class, () -> inventoryService.registerBook(
                buildBookTitle("Libro Dos", "ISBN-B"),
                buildBookCopy("INV-B", CopyStatus.AVAILABLE),
                buildLocation("LOC-DUP")));

        assertEquals("Ya existe una ubicacion registrada con ese codigo.", exception.getMessage());
    }

    @Test
    void shouldReuseExistingBookTitleWhenSameIsbn() {
        BookTitle firstTitle = buildBookTitle("Libro Uno", "ISBN-DUP");
        inventoryService.registerBook(
                firstTitle,
                buildBookCopy("INV-X", CopyStatus.AVAILABLE),
                buildLocation("LOC-X"));

        BookTitle secondTitle = buildBookTitle("Libro Dos", "ISBN-DUP");
        BookCopy secondCopy = buildBookCopy("INV-Y", CopyStatus.AVAILABLE);
        Location secondLocation = buildLocation("LOC-Y");

        inventoryService.registerBook(secondTitle, secondCopy, secondLocation);

        assertNotNull(secondCopy.getId());
        assertEquals(firstTitle.getId(), secondCopy.getBookTitleId());
        assertEquals(2, bookCopyRepository.findByBookTitleId(firstTitle.getId()).size());
    }

    @Test
    void shouldUpdateExistingEntryWithoutTriggeringFalseDuplicateChecks() {
        BookTitle bookTitle = buildBookTitle("Libro Editable", "ISBN-EDIT");
        BookCopy bookCopy = buildBookCopy("INV-EDIT", CopyStatus.AVAILABLE);
        Location location = buildLocation("LOC-EDIT");
        inventoryService.registerBook(bookTitle, bookCopy, location);

        bookTitle.setTitle("Libro Editado");
        bookCopy.setNotes("Actualizado");
        location.setShelf("B2");

        inventoryService.updateBookEntry(bookTitle, bookCopy, location);

        assertEquals("Libro Editado", bookTitleRepository.findById(bookTitle.getId()).getTitle());
        assertEquals("Actualizado", bookCopyRepository.findById(bookCopy.getId()).getNotes());
        assertEquals("B2", locationRepository.findById(location.getId()).getShelf());
    }

    private BookTitle buildBookTitle(String title, String isbn) {
        BookTitle bookTitle = new BookTitle();
        bookTitle.setTitle(title);
        bookTitle.setAuthor("Autor Demo");
        bookTitle.setIsbn(isbn);
        bookTitle.setYear(2024);
        bookTitle.setCategory("Programacion");
        bookTitle.setCareer("Sistemas");
        return bookTitle;
    }

    private BookCopy buildBookCopy(String inventoryCode, CopyStatus status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setInventoryCode(inventoryCode);
        bookCopy.setStatus(status);
        return bookCopy;
    }

    private Location buildLocation(String code) {
        Location location = new Location();
        location.setRoom("Sala A");
        location.setSection("General");
        location.setShelf("A1");
        location.setLevel("1");
        location.setPosition("01");
        location.setCode(code);
        return location;
    }
}
