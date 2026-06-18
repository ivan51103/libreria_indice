package com.biblioteca.service;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.BookCopyRepository;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.repository.LocationRepository;

public class InventoryService {
    // Coordina cambios administrativos sobre libro, ejemplar y ubicacion.
    private final ConnectionProvider connectionProvider;
    private final BookTitleRepository bookTitleRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LocationRepository locationRepository;

    public InventoryService(BookTitleRepository bookTitleRepository,
                            BookCopyRepository bookCopyRepository,
                            LocationRepository locationRepository) {
        this(null, bookTitleRepository, bookCopyRepository, locationRepository);
    }

    public InventoryService(ConnectionProvider connectionProvider,
                            BookTitleRepository bookTitleRepository,
                            BookCopyRepository bookCopyRepository,
                            LocationRepository locationRepository) {
        this.connectionProvider = connectionProvider;
        this.bookTitleRepository = bookTitleRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.locationRepository = locationRepository;
    }

    public void registerBook(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        validateBookTitle(bookTitle);
        validateBookCopy(bookCopy);
        normalizeLocation(location);
        validateLocation(location);
        validateUniqueBookTitle(bookTitle);
        validateUniqueInventoryCode(bookCopy);
        validateUniqueLocationCode(location);

        // El orden importa: primero se persiste la ubicacion, luego la obra y al final el ejemplar.
        if (bookCopy.getStatus() == null) {
            bookCopy.setStatus(CopyStatus.AVAILABLE);
        }
        executeAtomicWrite(connection -> {
            Location storedLocation = saveLocation(location, connection);
            BookTitle storedTitle = saveBookTitle(bookTitle, connection);
            bookCopy.setBookTitleId(storedTitle.getId());
            bookCopy.setLocationId(storedLocation.getId());
            saveBookCopy(bookCopy, connection);
            return null;
        });
    }

    public void updateBook(BookTitle bookTitle) {
        validateBookTitle(bookTitle);
        validateUniqueBookTitle(bookTitle);
        bookTitleRepository.save(bookTitle);
    }

    public void updateCopy(BookCopy bookCopy) {
        validateBookCopy(bookCopy);
        validateUniqueInventoryCode(bookCopy);
        bookCopyRepository.save(bookCopy);
    }

    public void updateBookEntry(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        validateBookTitle(bookTitle);
        validateBookCopy(bookCopy);
        normalizeLocation(location);
        validateLocation(location);
        validateUniqueBookTitle(bookTitle);
        validateUniqueInventoryCode(bookCopy);
        validateUniqueLocationCode(location);

        executeAtomicWrite(connection -> {
            Location storedLocation = saveLocation(location, connection);
            saveBookTitle(bookTitle, connection);
            bookCopy.setLocationId(storedLocation.getId());
            saveBookCopy(bookCopy, connection);
            return null;
        });
    }

    public void changeCopyStatus(Long copyId, CopyStatus status) {
        // El cambio de estado es la base para bajas logicas y control de disponibilidad.
        bookCopyRepository.updateStatus(copyId, status);
    }

    private void validateBookTitle(BookTitle bookTitle) {
        if (bookTitle == null || isBlank(bookTitle.getTitle()) || isBlank(bookTitle.getAuthor())) {
            throw new BusinessException("Titulo y autor son obligatorios.");
        }
        if (bookTitle.getYear() < 0) {
            throw new BusinessException("El anio no puede ser negativo.");
        }
    }

    private void validateBookCopy(BookCopy bookCopy) {
        if (bookCopy == null || isBlank(bookCopy.getInventoryCode())) {
            throw new BusinessException("El codigo de inventario es obligatorio.");
        }
    }

    private void validateLocation(Location location) {
        if (location == null || isBlank(location.getCode())) {
            throw new BusinessException("La ubicacion es obligatoria.");
        }
    }

    private void normalizeLocation(Location location) {
        if (location == null) {
            return;
        }
        if (isBlank(location.getRoom())) {
            location.setRoom("Sin especificar");
        }
        if (isBlank(location.getSection())) {
            location.setSection("Sin especificar");
        }
        if (isBlank(location.getShelf())) {
            location.setShelf("Sin especificar");
        }
    }

    private void validateUniqueBookTitle(BookTitle bookTitle) {
        if (isBlank(bookTitle.getIsbn())) {
            return;
        }
        BookTitle existing = bookTitleRepository.findByIsbn(bookTitle.getIsbn().trim());
        if (existing != null && !existing.getId().equals(bookTitle.getId())) {
            throw new BusinessException("Ya existe otro libro registrado con ese ISBN.");
        }
    }

    private void validateUniqueInventoryCode(BookCopy bookCopy) {
        BookCopy existing = bookCopyRepository.findByInventoryCode(bookCopy.getInventoryCode().trim());
        if (existing != null && !existing.getId().equals(bookCopy.getId())) {
            throw new BusinessException("Ya existe otro ejemplar con ese codigo de inventario.");
        }
    }

    private void validateUniqueLocationCode(Location location) {
        Location existing = locationRepository.findByCode(location.getCode().trim());
        if (existing != null && !existing.getId().equals(location.getId())) {
            throw new BusinessException("Ya existe una ubicacion registrada con ese codigo.");
        }
    }

    private <T> T executeAtomicWrite(java.util.function.Function<java.sql.Connection, T> work) {
        if (connectionProvider == null) {
            return work.apply(null);
        }
        return connectionProvider.withTransaction(work);
    }

    private Location saveLocation(Location location, java.sql.Connection connection) {
        return connection == null ? locationRepository.save(location) : locationRepository.save(location, connection);
    }

    private BookTitle saveBookTitle(BookTitle bookTitle, java.sql.Connection connection) {
        return connection == null ? bookTitleRepository.save(bookTitle) : bookTitleRepository.save(bookTitle, connection);
    }

    private BookCopy saveBookCopy(BookCopy bookCopy, java.sql.Connection connection) {
        return connection == null ? bookCopyRepository.save(bookCopy) : bookCopyRepository.save(bookCopy, connection);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
