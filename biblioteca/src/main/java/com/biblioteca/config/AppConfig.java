package com.biblioteca.config;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.BookCopyRepository;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.repository.LocationRepository;
import com.biblioteca.repository.UserRepository;
import com.biblioteca.repository.sqlite.SQLiteBookCopyRepository;
import com.biblioteca.repository.sqlite.SQLiteBookTitleRepository;
import com.biblioteca.repository.sqlite.SQLiteLocationRepository;
import com.biblioteca.repository.sqlite.SQLiteUserRepository;
import com.biblioteca.security.AuthenticationService;
import com.biblioteca.security.PasswordService;
import com.biblioteca.security.User;
import com.biblioteca.security.UserRole;
import com.biblioteca.service.AuthorizationService;
import com.biblioteca.service.BookCatalogService;
import com.biblioteca.service.CatalogService;
import com.biblioteca.service.InventoryService;
import com.biblioteca.service.SearchService;

public class AppConfig {
    // Centraliza el cableado de infraestructura, repositorios y servicios.
    private final ConnectionProvider connectionProvider;
    private final DatabaseInitializer databaseInitializer;
    private final UserRepository userRepository;
    private final BookTitleRepository bookTitleRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LocationRepository locationRepository;
    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;
    private final SearchService searchService;
    private final CatalogService catalogService;
    private final BookCatalogService bookCatalogService;
    private final InventoryService inventoryService;
    private final AuthorizationService authorizationService;

    public AppConfig() {
        this.connectionProvider = new ConnectionProvider();
        this.databaseInitializer = new DatabaseInitializer(connectionProvider);
        // La base debe existir antes de construir repositorios que la consultan.
        this.databaseInitializer.initialize();
        this.userRepository = new SQLiteUserRepository(connectionProvider);
        this.bookTitleRepository = new SQLiteBookTitleRepository(connectionProvider);
        this.bookCopyRepository = new SQLiteBookCopyRepository(connectionProvider);
        this.locationRepository = new SQLiteLocationRepository(connectionProvider);
        this.passwordService = new PasswordService();
        this.authenticationService = new AuthenticationService(userRepository, passwordService);
        this.searchService = new SearchService();
        this.catalogService = new CatalogService(bookTitleRepository, bookCopyRepository, locationRepository, searchService);
        this.bookCatalogService = new BookCatalogService(bookTitleRepository, bookCopyRepository, searchService);
        this.inventoryService = new InventoryService(bookTitleRepository, bookCopyRepository, locationRepository);
        this.authorizationService = new AuthorizationService();

        seedSampleData();
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public DatabaseInitializer getDatabaseInitializer() {
        return databaseInitializer;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public BookCatalogService getBookCatalogService() {
        return bookCatalogService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    private void seedSampleData() {
        seedAdminUser();

        // El sembrado es idempotente: permite enriquecer bases existentes sin duplicar ISBN, ubicaciones ni inventario.
        Location engineeringShelf = seedLocation("Sala General", "Ingenieria", "A1", "2", "05", "SG-ING-A1-2-05");
        Location programmingShelf = seedLocation("Sala General", "Programacion", "B3", "1", "02", "SG-PRO-B3-1-02");
        Location databaseShelf = seedLocation("Sala General", "Bases de Datos", "C2", "1", "07", "SG-BD-C2-1-07");
        Location networksShelf = seedLocation("Sala Tecnica", "Redes", "R1", "3", "01", "ST-RED-R1-3-01");
        Location humanitiesShelf = seedLocation("Sala Humanidades", "Historia", "H4", "1", "04", "SH-HIS-H4-1-04");

        BookTitle cleanCode = seedBookTitle(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Prentice Hall",
                2008,
                "Programacion",
                "Ingenieria en Sistemas",
                "Buenas practicas para escribir codigo mantenible.",
                "covers/clean-code.jpg");
        BookTitle cleanArchitecture = seedBookTitle(
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                "Prentice Hall",
                2017,
                "Arquitectura de Software",
                "Ingenieria en Sistemas",
                "Principios para organizar sistemas de software mantenibles.",
                "covers/clean-architecture.jpg");
        BookTitle patterns = seedBookTitle(
                "Patrones de Diseno",
                "Erich Gamma",
                "9780201633610",
                "Addison-Wesley",
                1994,
                "Arquitectura de Software",
                "Ingenieria en Sistemas",
                "Catalogo de patrones de diseno orientados a objetos.",
                "covers/patrones-diseno.jpg");
        BookTitle databases = seedBookTitle(
                "Fundamentos de Bases de Datos",
                "Abraham Silberschatz",
                "9780073523323",
                "McGraw-Hill",
                2010,
                "Bases de Datos",
                "Ingenieria en Sistemas",
                "Modelo relacional, SQL, diseno y administracion de bases de datos.",
                "covers/fundamentos-bases-datos.jpg");
        BookTitle networks = seedBookTitle(
                "Redes de Computadoras",
                "Andrew S. Tanenbaum",
                "9780132126953",
                "Pearson",
                2011,
                "Redes",
                "Ingenieria en Sistemas",
                "Conceptos de comunicacion, protocolos y arquitectura de redes.",
                "covers/redes-computadoras.jpg");
        BookTitle history = seedBookTitle(
                "Historia Universal Contemporanea",
                "Jose Luis Comellas",
                "9788432130562",
                "Rialp",
                2005,
                "Historia",
                "Bachillerato General",
                "Panorama de procesos politicos y sociales contemporaneos.",
                "covers/historia-universal.jpg");

        seedBookCopy(cleanCode, programmingShelf, "INV-0001", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(cleanCode, programmingShelf, "INV-0002", CopyStatus.REPAIR, "En reparacion de lomo");
        seedBookCopy(cleanArchitecture, engineeringShelf, "INV-0004", CopyStatus.AVAILABLE, "Ejemplar de prestamo interno");
        seedBookCopy(cleanArchitecture, programmingShelf, "INV-0005", CopyStatus.MISSING, "No localizado en revision de inventario");
        seedBookCopy(patterns, engineeringShelf, "INV-0003", CopyStatus.AVAILABLE, "Buen estado");
        seedBookCopy(databases, databaseShelf, "INV-0006", CopyStatus.AVAILABLE, "Incluye material complementario");
        seedBookCopy(databases, databaseShelf, "INV-0007", CopyStatus.REMOVED, "Baja logica por deterioro");
        seedBookCopy(networks, networksShelf, "INV-0008", CopyStatus.REPAIR, "Pendiente de reencuadernacion");
        seedBookCopy(history, humanitiesShelf, "INV-0009", CopyStatus.AVAILABLE, "Edicion de sala");
    }

    private Location seedLocation(String room, String section, String shelf, String level, String position, String code) {
        Location existing = locationRepository.findByCode(code);
        if (existing != null) {
            return existing;
        }

        Location location = new Location();
        location.setRoom(room);
        location.setSection(section);
        location.setShelf(shelf);
        location.setLevel(level);
        location.setPosition(position);
        location.setCode(code);
        return locationRepository.save(location);
    }

    private BookTitle seedBookTitle(String title,
                                    String author,
                                    String isbn,
                                    String publisher,
                                    int year,
                                    String category,
                                    String career,
                                    String description,
                                    String coverPath) {
        BookTitle existing = bookTitleRepository.findByIsbn(isbn);
        if (existing != null) {
            return existing;
        }

        BookTitle bookTitle = new BookTitle();
        bookTitle.setTitle(title);
        bookTitle.setAuthor(author);
        bookTitle.setIsbn(isbn);
        bookTitle.setPublisher(publisher);
        bookTitle.setYear(year);
        bookTitle.setCategory(category);
        bookTitle.setCareer(career);
        bookTitle.setDescription(description);
        bookTitle.setCoverPath(coverPath);
        return bookTitleRepository.save(bookTitle);
    }

    private void seedBookCopy(BookTitle bookTitle, Location location, String inventoryCode, CopyStatus status, String notes) {
        if (bookCopyRepository.findByInventoryCode(inventoryCode) != null) {
            return;
        }

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBookTitleId(bookTitle.getId());
        bookCopy.setInventoryCode(inventoryCode);
        bookCopy.setLocationId(location.getId());
        bookCopy.setStatus(status);
        bookCopy.setNotes(notes);
        bookCopyRepository.save(bookCopy);
    }

    private void seedAdminUser() {
        // El usuario admin inicial solo se crea la primera vez.
        User existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin != null) {
            // Si la base ya tenia la contrasena antigua en plano, se reemplaza por hash.
            if (passwordService.needsRehash(existingAdmin.getPasswordHash())) {
                existingAdmin.setPasswordHash(passwordService.hashPassword("admin123"));
                userRepository.save(existingAdmin);
            }
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordService.hashPassword("admin123"));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
    }
}
