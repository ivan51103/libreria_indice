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
        this.catalogService = new CatalogService(bookTitleRepository, bookCopyRepository, locationRepository);
        this.bookCatalogService = new BookCatalogService(bookTitleRepository, bookCopyRepository);
        this.inventoryService = new InventoryService(connectionProvider, bookTitleRepository, bookCopyRepository, locationRepository);
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
        Location adminShelf = seedLocation("Sala General", "Administración", "A1", "1", "01", "SG-ADM-A1-1-01");
        Location industrialShelf = seedLocation("Sala General", "Ingeniería Industrial", "B1", "1", "01", "SG-IND-B1-1-01");
        Location marketingShelf = seedLocation("Sala General", "Mercadotecnia", "C1", "1", "01", "SG-MKT-C1-1-01");
        Location designShelf = seedLocation("Sala General", "Diseño Gráfico", "D1", "1", "01", "SG-DIS-D1-1-01");
        Location languagesShelf = seedLocation("Sala General", "Lenguas Extranjeras", "E1", "1", "01", "SG-LEN-E1-1-01");
        Location systemsShelf = seedLocation("Sala General", "Sistemas", "F1", "1", "01", "SG-SIS-F1-1-01");
        Location communicationShelf = seedLocation("Sala General", "Comunicación", "G1", "1", "01", "SG-COM-G1-1-01");

        BookTitle administracion = seedBookTitle(
                "Administración",
                "Stephen P. Robbins",
                "9780134529226",
                "Pearson",
                2016,
                "Administración",
                "LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES",
                "Fundamentos de administración para emprendedores.",
                "covers/administracion.jpg");
        BookTitle emprendedor = seedBookTitle(
                "El Emprendedor Exitoso",
                "Rafael Alcaraz",
                "9786073235927",
                "McGraw-Hill",
                2017,
                "Emprendimiento",
                "LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES",
                "Estrategias y habilidades para emprender con éxito.",
                "covers/emprendedor.jpg");
        BookTitle operaciones = seedBookTitle(
                "Administración de Operaciones",
                "William Stevenson",
                "9781259071205",
                "McGraw-Hill",
                2012,
                "Producción",
                "LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL",
                "Gestión de procesos productivos y cadena de suministro.",
                "covers/operaciones.jpg");
        BookTitle calidad = seedBookTitle(
                "Control Total de Calidad",
                "W. Edwards Deming",
                "9789681815231",
                "FCE",
                2000,
                "Calidad",
                "LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL",
                "Principios de calidad y mejora continua.",
                "covers/calidad.jpg");
        BookTitle marketing = seedBookTitle(
                "Marketing",
                "Philip Kotler",
                "9780133856460",
                "Pearson",
                2015,
                "Mercadotecnia",
                "LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA",
                "Fundamentos de marketing y estrategia comercial.",
                "covers/marketing.jpg");
        BookTitle investigacion = seedBookTitle(
                "Investigación de Mercados",
                "Naresh Malhotra",
                "9789702612355",
                "Pearson",
                2012,
                "Investigación",
                "LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA",
                "Técnicas y métodos de investigación de mercados.",
                "covers/investigacion.jpg");
        BookTitle disenoFundamentos = seedBookTitle(
                "Fundamentos del Diseño",
                "Robert Scott",
                "9789681815232",
                "Trillas",
                2005,
                "Diseño Gráfico",
                "LICENCIATURA EN DISEÑO GRÁFICO",
                "Principios básicos del diseño visual.",
                "covers/diseno-fundamentos.jpg");
        BookTitle disenoGrafico = seedBookTitle(
                "Diseño Gráfico: Nuevos Fundamentos",
                "Ellen Lupton",
                "9788425226665",
                "GG",
                2012,
                "Diseño",
                "LICENCIATURA EN DISEÑO GRÁFICO",
                "Teoría y práctica del diseño contemporáneo.",
                "covers/diseno-grafico.jpg");
        BookTitle linguistica = seedBookTitle(
                "Introducción a la Lingüística",
                "Humberto López",
                "9789681680003",
                "Trillas",
                2010,
                "Lingüística",
                "LICENCIATURA EN LENGUAS EXTRANJERAS",
                "Fundamentos del estudio del lenguaje.",
                "covers/linguistica.jpg");
        BookTitle traduccion = seedBookTitle(
                "Manual de Traducción",
                "Amparo Hurtado",
                "9788497423350",
                "Cátedra",
                2011,
                "Traducción",
                "LICENCIATURA EN LENGUAS EXTRANJERAS",
                "Técnicas y estrategias de traducción.",
                "covers/traduccion.jpg");
        BookTitle cleanCode = seedBookTitle(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Prentice Hall",
                2008,
                "Programación",
                "LICENCIATURA EN SISTEMAS COMPUTACIONALES",
                "Buenas prácticas para escribir código mantenible.",
                "covers/clean-code.jpg");
        BookTitle basesDatos = seedBookTitle(
                "Fundamentos de Bases de Datos",
                "Abraham Silberschatz",
                "9780073523323",
                "McGraw-Hill",
                2010,
                "Bases de Datos",
                "LICENCIATURA EN SISTEMAS COMPUTACIONALES",
                "Modelo relacional, SQL, diseño y administración de bases de datos.",
                "covers/bases-datos.jpg");
        BookTitle comunicacionHumana = seedBookTitle(
                "Teoría de la Comunicación Humana",
                "Paul Watzlawick",
                "9788425412078",
                "Herder",
                2004,
                "Comunicación",
                "LICENCIATURA EN COMUNICACIÓN",
                "Estudio de la comunicación interpersonal y sistémica.",
                "covers/comunicacion-humana.jpg");
        BookTitle comunicacionOrg = seedBookTitle(
                "Comunicación Organizacional",
                "Carlos Fernández",
                "9789706861415",
                "Thomson",
                2006,
                "Comunicación",
                "LICENCIATURA EN COMUNICACIÓN",
                "Estrategias de comunicación en el entorno corporativo.",
                "covers/comunicacion-org.jpg");

        seedBookCopy(administracion, adminShelf, "INV-0001", CopyStatus.AVAILABLE, "Manual de referencia");
        seedBookCopy(emprendedor, adminShelf, "INV-0002", CopyStatus.AVAILABLE, "Incluye casos prácticos");
        seedBookCopy(operaciones, industrialShelf, "INV-0003", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(calidad, industrialShelf, "INV-0004", CopyStatus.AVAILABLE, "Buen estado");
        seedBookCopy(marketing, marketingShelf, "INV-0005", CopyStatus.AVAILABLE, "Edición actualizada");
        seedBookCopy(investigacion, marketingShelf, "INV-0006", CopyStatus.AVAILABLE, "Incluye CD interactivo");
        seedBookCopy(disenoFundamentos, designShelf, "INV-0007", CopyStatus.AVAILABLE, "Ejemplar de sala");
        seedBookCopy(disenoGrafico, designShelf, "INV-0008", CopyStatus.AVAILABLE, "Buen estado");
        seedBookCopy(linguistica, languagesShelf, "INV-0009", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(traduccion, languagesShelf, "INV-0010", CopyStatus.AVAILABLE, "Préstamo interno");
        seedBookCopy(cleanCode, systemsShelf, "INV-0011", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(basesDatos, systemsShelf, "INV-0012", CopyStatus.MISSING, "No localizado en inventario");
        seedBookCopy(comunicacionHumana, communicationShelf, "INV-0013", CopyStatus.REMOVED, "Baja lógica por deterioro");
        seedBookCopy(comunicacionOrg, communicationShelf, "INV-0014", CopyStatus.REMOVED, "Edición desactualizada");
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
