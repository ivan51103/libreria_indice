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

        // Ubicaciones (estanterías)
        Location loc1 = seedLocation("Sala General", "Administración", "A1", "1", "01", "SG-ADM-A1-1-01");
        Location loc2 = seedLocation("Sala General", "Ingeniería Industrial", "B1", "1", "01", "SG-IND-B1-1-01");
        Location loc3 = seedLocation("Sala General", "Mercadotecnia", "C1", "1", "01", "SG-MKT-C1-1-01");
        Location loc4 = seedLocation("Sala General", "Diseño Gráfico", "D1", "1", "01", "SG-DIS-D1-1-01");
        Location loc5 = seedLocation("Sala General", "Lenguas Extranjeras", "E1", "1", "01", "SG-LEN-E1-1-01");
        Location loc6 = seedLocation("Sala General", "Sistemas", "F1", "1", "01", "SG-SIS-F1-1-01");
        Location loc7 = seedLocation("Sala General", "Comunicación", "G1", "1", "01", "SG-COM-G1-1-01");
        Location loc8 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0001");
        Location loc9 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0002");
        Location loc10 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0003");
        Location loc11 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0004");
        Location loc12 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0005");
        Location loc13 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0006");
        Location loc14 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0007");
        Location loc15 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0008");
        Location loc16 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0009");
        Location loc17 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0010");
        Location loc18 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0011");
        Location loc19 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0012");
        Location loc20 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0013");
        Location loc21 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0014");
        Location loc22 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0015");
        Location loc23 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0016");
        Location loc24 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0017");
        Location loc25 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0018");
        Location loc26 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0019");
        Location loc27 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0020");
        Location loc28 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0021");
        Location loc29 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0022");
        Location loc30 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0023");
        Location loc31 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0024");
        Location loc32 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0025");
        Location loc33 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0026");
        Location loc34 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0027");
        Location loc35 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0028");
        Location loc36 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0029");
        Location loc37 = seedLocation("Sin especificar", "Sin especificar", "Sin especificar", null, null, "0030");

        // Libros
        BookTitle book1 = seedBookTitle("Administración", "Stephen P. Robbins", "9780134529226", "Pearson", 2016, "Administración", "LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES", "Fundamentos de administración para emprendedores.", "covers/9780134529226.jpeg");
        BookTitle book2 = seedBookTitle("El Emprendedor Exitoso", "Rafael Alcaraz", "9786073235927", "McGraw-Hill", 2017, "Emprendimiento", "LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES", "Estrategias y habilidades para emprender con éxito.", "covers/emprendedor.jpg");
        BookTitle book3 = seedBookTitle("Administración de Operaciones", "William Stevenson", "9781259071205", "McGraw-Hill", 2012, "Producción", "LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL", "Gestión de procesos productivos y cadena de suministro.", "covers/9781259071205.jpeg");
        BookTitle book4 = seedBookTitle("Control Total de Calidad", "W. Edwards Deming", "9789681815231", "FCE", 2000, "Calidad", "LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES", "Principios de calidad y mejora continua.", "covers/calidad.jpg");
        BookTitle book5 = seedBookTitle("Marketing", "Philip Kotler", "9780133856460", "Pearson", 2015, "Mercadotecnia", "LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA", "Fundamentos de marketing y estrategia comercial.", "covers/marketing.jpg");
        BookTitle book6 = seedBookTitle("Investigación de Mercados", "Naresh Malhotra", "9789702612355", "Pearson", 2012, "Investigación", "LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA", "Técnicas y métodos de investigación de mercados.", "covers/investigacion.jpg");
        BookTitle book7 = seedBookTitle("Fundamentos del Diseño", "Robert Scott", "9789681815232", "Trillas", 2005, "Diseño Gráfico", "LICENCIATURA EN DISEÑO GRÁFICO", "Principios básicos del diseño visual.", "covers/diseno-fundamentos.jpg");
        BookTitle book8 = seedBookTitle("Diseño Gráfico: Nuevos Fundamentos", "Ellen Lupton", "9788425226665", "GG", 2012, "Diseño", "LICENCIATURA EN DISEÑO GRÁFICO", "Teoría y práctica del diseño contemporáneo.", "covers/diseno-grafico.jpg");
        BookTitle book9 = seedBookTitle("Introducción a la Lingüística", "Humberto López", "9789681680003", "Trillas", 2010, "Lingüística", "LICENCIATURA EN LENGUAS EXTRANJERAS", "Fundamentos del estudio del lenguaje.", "covers/9789681680003.jpeg");
        BookTitle book10 = seedBookTitle("Manual de Traducción", "Amparo Hurtado", "9788497423350", "Cátedra", 2011, "Traducción", "LICENCIATURA EN LENGUAS EXTRANJERAS", "Técnicas y estrategias de traducción.", "covers/traduccion.jpg");
        BookTitle book11 = seedBookTitle("Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008, "Programación", "LICENCIATURA EN SISTEMAS COMPUTACIONALES", "Buenas prácticas para escribir código mantenible.", "covers/clean-code.jpg");
        BookTitle book12 = seedBookTitle("Fundamentos de Bases de Datos", "Abraham Silberschatz", "9780073523323", "McGraw-Hill", 2010, "Bases de Datos", "LICENCIATURA EN SISTEMAS COMPUTACIONALES", "Modelo relacional, SQL, diseño y administración de bases de datos.", "covers/bases-datos.jpg");
        BookTitle book13 = seedBookTitle("Teoría de la Comunicación Humana", "Paul Watzlawick", "9788425412078", "Herder", 2004, "Comunicación", "LICENCIATURA EN COMUNICACIÓN", "Estudio de la comunicación interpersonal y sistémica.", "covers/comunicacion-humana.jpg");
        BookTitle book14 = seedBookTitle("Comunicación Organizacional", "Carlos Fernández", "9789706861415", "Thomson", 2006, "Comunicación", "LICENCIATURA EN COMUNICACIÓN", "Estrategias de comunicación en el entorno corporativo.", "covers/comunicacion-org.jpg");
        BookTitle book15 = seedBookTitle("Creaciones gráficas con Illustrator", "Stéphane Béguin", "978-84-329-1432-4", "Grupo Editorial CEAC", 2005, "Diseño", "LICENCIATURA EN DISEÑO GRÁFICO", "Diseño gráfico / Informática y software de ilustraciónGuía práctica estructurada en formato de talleres donde nueve ilustradores profesionales explican paso a paso el proceso creativo y técnico para desarrollar proyectos reales con Adobe Illustrator.", "covers/978-84-329-1432-4.jpeg");
        BookTitle book16 = seedBookTitle("Folleto del Sistema Canon EOS", "Canon Inc.", null, "Canon México (.com.mx)", 2006, "Fotografía / Catálogos de equipo fotográfico", "LICENCIATURA EN DISEÑO GRÁFICO", "Folleto publicitario y guía del sistema fotográfico réflex digital de Canon que promociona la compatibilidad de los cuerpos de cámaras EOS con la amplia línea de objetivos EF y accesorios de la marca.", "covers/cover-d029e12c-af04-4977-beaa-0eb19963623e.jpeg");
        BookTitle book17 = seedBookTitle("Técnicas de positivado en blanco y negro", "Hubert C. Birnbaum / Eastman Kodak Company", "84-85902-47-5", "Ediciones Folio", 1988, "Arte y Fotografía (Manuales técnicos de revelado)", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual práctico de laboratorio analógico centrado en los procesos de revelado y positivado en papel de emulsiones fotográficas monocromas, enseñando técnicas de exposición, contraste y control químico del cuarto oscuro.", "covers/84-85902-47-5.jpeg");
        BookTitle book18 = seedBookTitle("Sistemas de signos en la comunicación visual", "Otl Aicher y Martin Krampen", "978-8425209529", "Editorial Gustavo Gili", 1981, "Diseño gráfico / Semiótica y Comunicación Visual", "LICENCIATURA EN DISEÑO GRÁFICO", "Tratado teórico y metodológico orientado a profesionales del diseño y la arquitectura que analiza la estructura, el funcionamiento y la correcta aplicación de los sistemas semióticos, lenguajes icónicos y códigos de pictogramas en el entorno visual urbano y comercial.", "covers/978-8425209529.jpeg");
        BookTitle book19 = seedBookTitle("The Graphic Language of Neville Brody", "Jon Wozencroft", "978-0500274965", "Thames & Hudson", 1988, "Diseño gráfico / Tipografía y Arte visual", "LICENCIATURA EN DISEÑO GRÁFICO", "Monografía y obra de referencia que documenta el revolucionario trabajo del diseñador británico Neville Brody durante la década de 1980, explorando sus innovaciones en la dirección de arte de revistas como The Face, portadas de discos y su enfoque expresivo de la tipografía digital.", "covers/978-0500274965.jpeg");
        BookTitle book20 = seedBookTitle("Dibujante de Cómics (Fascículo 2)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Segundo tomo pedagógico perteneciente al curso de formación por correspondencia especializado en la enseñanza del dibujo de historietas, centrado en analizar las bases narrativas de los clásicos del humor, técnicas de encuadre, equilibrio de perspectiva y la confección formal de guiones literarios y técnicos orientados al formato cómic.", "covers/cover-13940890-ff13-4ffd-91e3-b839dfd4cffb.jpeg");
        BookTitle book21 = seedBookTitle("Dibujo: Curso de dibujo y pintura", "Varios autores", "978-84-329-1577-2", "Grupo Editorial CEAC", 2007, "Dibujo / Técnicas artísticas y pedagógicas", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual didáctico enfocado en la enseñanza formal de las Bellas Artes que aborda desde las nociones básicas de encuadre y esbozo hasta técnicas complejas de modelado, control de sombras, luces y el dominio de materiales tradicionales como la sanguina y el carboncillo para la representación de la anatomía humana.", "covers/978-84-329-1577-2.jpeg");
        BookTitle book22 = seedBookTitle("Creative Camera (Número 12 / 1987)", "Peter Turner (Editor)", null, "CC Publishing / Coo Press Ltd.", 1987, "Fotografía / Revistas de arte y teoría fotográfica", "LICENCIATURA EN DISEÑO GRÁFICO", "Edición mensual de la histórica revista británica de fotografía independiente dedicada en este número a la plataforma internacional Euro Photo, la cual incluye portafolios, críticas y ensayos visuales de destacados fotógrafos contemporáneos como Joan Fontcuberta, Pere Formiguera, Diana Block, Harald Falkenhagen y Herlinde Koelbl.", "covers/cover-16a0a7aa-efe9-4eec-85cf-2babcc2f0b00.jpeg");
        BookTitle book23 = seedBookTitle("Diccionario de arte a partir de sus símbolos", "Sara Carr-Gomm", "978-8420654768", "Alianza Editorial", 2003, "Arte / Historia del Arte y Simbología", "LICENCIATURA EN DISEÑO GRÁFICO", "Guía de referencia y consulta indispensable que decodifica e interpreta los principales mitos, símbolos, temas religiosos y alegorías presentes en las obras maestras de la pintura y escultura occidental desde la Antigüedad clásica hasta la era moderna.", "covers/978-8420654768.jpeg");
        BookTitle book24 = seedBookTitle("Color: Curso de dibujo y pintura", "Varios autores", "978-84-329-1582-6", "Grupo Editorial CEAC", 2006, "Dibujo / Técnicas artísticas y teoría del color", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual didáctico enfocado en la teoría y práctica cromática en las Bellas Artes, abordando el concepto del color desde su naturaleza física (percepción y luz) hasta su aplicación técnica y mezclas en diferentes disciplinas pictóricas.", "covers/978-84-329-1582-6.jpeg");
        BookTitle book25 = seedBookTitle("Introducción a la teoría de los diseños", "Juan Acha", "978-607-17-0147-3", "Editorial Trillas", 1988, "Diseño / Teoría del Arte y Sociología", "LICENCIATURA EN DISEÑO GRÁFICO", "Texto fundamental que examina las diferencias conceptuales, históricas y sociopolíticas entre las artesanías, las artes y los diseños en el contexto de la producción industrial y la cultura de masas, sentando las bases para una teoría del diseño desde una perspectiva latinoamericana.", "covers/978-607-17-0147-3.jpeg");
        BookTitle book26 = seedBookTitle("Mi pueblo se llama San Agustín Oapan", "Luz María Chapela y Abraham Mauricio Salazar", "968-29-1836-7", "Secretaría de Educación Pública (SEP) / Libros del Rincón", 1988, "Literatura infantil / Monografías comunitarias e indígenas", "LICENCIATURA EN DISEÑO GRÁFICO", "Relato ilustrado infantil que narra con un lenguaje sencillo las costumbres, tradiciones cotidianas, leyendas, el trabajo artesanal con barro y la vida diaria de los habitantes en la comunidad indígena de San Agustín Oapan, en el estado de Guerrero.", "covers/968-29-1836-7.jpeg");
        BookTitle book27 = seedBookTitle("Illustrator CS4 (Guía Práctica)", "Laura Apolonio", "978-84-415-2576-4", "Anaya Multimedia", 2009, "Informática / Manuales de software y diseño gráfico", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual técnico de carácter formativo estructurado de manera progresiva y didáctica, enfocado en dotar al lector del conocimiento operativo básico y avanzado sobre las herramientas de dibujo vectorial, gestión del color y filtros creativos de Adobe Illustrator CS4, el cual incluye un apéndice exclusivo detallado para facilitar la transición técnica de usuarios migrantes del programa Macromedia FreeHand.", "covers/978-84-415-2576-4.jpeg");
        BookTitle book28 = seedBookTitle("bla, blArt (Número 9)", "Varios autores / Centro de Arte Joven y Creación de Álava", null, "Diputación Foral de Álava (Centro de Arte Joven y Creación)", 2000, "Arte contemporáneo / Revistas culturales y artísticas", "LICENCIATURA EN DISEÑO GRÁFICO", "Noveno ejemplar de esta publicación institucional periódica editada en el País Vasco, orientada a la divulgación, fomento y análisis crítico de las propuestas plásticas contemporáneas, el diseño de vanguardia y las expresiones visuales de creadores emergentes.", "covers/cover-bccb366c-57fd-4fc0-8cba-49a046f436fa.jpeg");
        BookTitle book29 = seedBookTitle("Escaparates: Diseño de montajes efímeros", "Alejandro Bahamón Ríos y Anna Vicens Soler", "978-84-342-3579-3", "Parramon Ediciones", 2009, "Diseño gráfico / Arquitectura efímera y Escaparatismo", "LICENCIATURA EN DISEÑO GRÁFICO", "Libro enfocado en analizar la relación entre el escaparatismo y el mundo de las artes visuales, explorando la creación de espacios tridimensionales temporales mediante diversos proyectos arquitectónicos y comerciales a nivel global.", "covers/978-84-342-3579-3.jpeg");
        BookTitle book30 = seedBookTitle("Dibujante de Cómics (Fascículo 3)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Tercer tomo pedagógico del curso de formación a distancia especializado en la enseñanza técnica y teórica de la historieta, enfocado en el desarrollo de la composición gráfica (planos, ángulos de visión y centros de interés), la adaptación de obras literarias al formato de guion, y el análisis estilístico de grandes maestros del medio como Burne Hogarth.", "covers/cover-13168088-13af-47ac-9bf6-5fd1dd466afe.jpeg");
        BookTitle book31 = seedBookTitle("Cómo corregir pruebas en color", "David Bann y John Gargan", "978-8425214905", "Editorial Gustavo Gili", 1992, "Diseño gráfico / Artes gráficas e Impresión", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual técnico y práctico orientado a profesionales y estudiantes que detalla los métodos esenciales para controlar, analizar y rectificar las pruebas de color antes de la tirada final de imprenta, garantizando la fidelidad cromática entre el diseño original y el resultado impreso.", "covers/978-8425214905.jpeg");
        BookTitle book32 = seedBookTitle("Tras la imagen: Investigación y práctica en fotografía", "Anna Fox y Natasha Caruana", "978-8425224751", "Editorial Gustavo Gili", 2014, "Fotografía / Ensayos y teoría fotográfica", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual práctico y pedagógico enfocado en el proceso de investigación y preparación de proyectos fotográficos, el cual detalla a través de casos prácticos cómo se entrelazan las influencias personales, la documentación previa y la metodología para consolidar series artísticas o documentales significativas.", "covers/978-8425224751.jpeg");
        BookTitle book33 = seedBookTitle("Dibujante de Cómics (Fascículo 7)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Séptimo tomo didáctico del curso de formación por correspondencia especializado en la historieta, enfocado en repasar la historia de los superhéroes, perfeccionar el dibujo a tinta con tramas mecánicas y manuales, analizar la estructuración de secuencias y encadenados en el guion, y estudiar el estilo del autor belga Hergé.", "covers/cover-7789ff06-0aa8-4907-a4b0-3923a8925c12.jpeg");
        BookTitle book34 = seedBookTitle("Dibujante de Cómics (Fascículo 5)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Quinto tomo didáctico del curso de formación por correspondencia especializado en la historieta, enfocado en repasar la trayectoria del cómic policíaco, profundizar en estructuras especiales del diseño de página, analizar las distintas partes de un guion formal y estudiar el estilo e influencia de Charles M. Schulz.", "covers/cover-64e86a75-bceb-4e35-a86d-7afb2b2fb2e5.jpeg");
        BookTitle book35 = seedBookTitle("La foto en blanco y negro: Técnica-consejos", "Roger Bellone", "978-8428205245", "Ediciones Omega", 1979, "Fotografía / Manuales técnicos de revelado", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual práctico de fotografía analógica orientado al aprendizaje de las técnicas de toma, procesado y positivado de emulsiones monocromas, aportando consejos específicos sobre iluminación, encuadre y manipulación en el laboratorio.", "covers/978-8428205245.jpeg");
        BookTitle book36 = seedBookTitle("Dibujante de Cómics (Fascículo 4)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Cuarto tomo didáctico del curso de formación por correspondencia especializado en la historieta, enfocado en repasar los cómics de aventuras, el tratamiento técnico de efectos de luces y sombras, el montaje y la planificación de la acción en la página, las distintas partes de un guion y el estilo del maestro Harold Foster.", "covers/cover-f483e59a-5a30-4406-8782-03a5f8e18564.jpeg");
        BookTitle book37 = seedBookTitle("Los carteles: su historia y su lenguaje", "John Barnicoat", "84-252-0779-7", "Editorial Gustavo Gili", 2000, "Diseño gráfico / Historia del arte y publicidad", "LICENCIATURA EN DISEÑO GRÁFICO", "Ensayo clásico que examina la evolución de la cartelería desde finales del siglo XIX hasta finales del siglo XX, analizando sus implicaciones estéticas, comerciales y socioculturales a través de corrientes artísticas que van desde el Art Nouveau hasta las expresiones contraculturales y underground.", "covers/84-252-0779-7.jpeg");
        BookTitle book38 = seedBookTitle("John Bignell: Chelsea Photographer", "John Bignell", "978-0950622811", "Studio B", 1983, "Arte y Fotografía / Monografías de fotógrafos", "LICENCIATURA EN DISEÑO GRÁFICO", "Libro monográfico y retrospectiva visual que recopila más de tres décadas de trabajo del fotógrafo británico John Bignell, documentando la vida cotidiana, la arquitectura, los artistas y la vibrante cultura bohemia del barrio de Chelsea y el centro de Londres a mediados del siglo XX.", "covers/978-0950622811.jpeg");
        BookTitle book39 = seedBookTitle("Catálogo de la 13ª Bienal Internacional del Cartel en México (13 BICM)", "Varios autores / Bienal Internacional del Cartel en México A.C.", null, "Bienal Internacional del Cartel en México A.C.", 2014, "Diseño gráfico / Cartelismo y Artes visuales", "LICENCIATURA EN DISEÑO GRÁFICO", "Catálogo oficial de la decimotercera edición de la bienal que reúne y documenta la selección internacional de más de 300 carteles provenientes de 43 países, conmemorando los 25 años del certamen, rindiendo homenaje In Memoriam a Martha Covarrubias Newton y Víctor Sandoval, y destacando a Quebec como el invitado de honor.", "covers/cover-7db09367-cc61-4fe7-94e8-0b1218610b8f.jpeg");
        BookTitle book40 = seedBookTitle("Fundamentación del anuncio publicitario: Génesis del anuncio", "Raúl E. Beltrán y Cruces", "978-9682468698", "Editorial Trillas", 2003, "Publicidad / Mercadotecnia y Comunicación", "LICENCIATURA EN DISEÑO GRÁFICO", "Texto académico enfocado en analizar de manera integral las disciplinas auxiliares como la antropología, la sociología y la psicología que sustentan la investigación de mercados, la creación del mensaje publicitario efectivo y el comportamiento del consumidor, incluyendo un análisis sobre la responsabilidad ética en los medios.", "covers/978-9682468698.jpeg");
        BookTitle book41 = seedBookTitle("Dibujante de Cómics (Fascículo 1)", "Varios autores / Centro de Estudios Profesionales CCC", null, "Centro de Estudios Profesionales CCC", 1982, "Dibujo / Historieta y Cómic", "LICENCIATURA EN DISEÑO GRÁFICO", "Primer tomo didáctico del curso de formación por correspondencia especializado en la historieta, centrado en los orígenes e historia desde la prehistoria, técnicas básicas para el dibujo de personajes y géneros, la importancia estructural del guion y un análisis del estilo del maestro Alex Raymond.", "covers/cover-0ad3326d-3d98-42c0-94bf-f5973fc35cfc.jpeg");
        BookTitle book42 = seedBookTitle("Impacto de los medios de comunicación (Octava edición)", "Shirley Biagi", "978-9708300865", "Cengage Learning Editores", 2009, "Comunicación / Medios masivos e industrias culturales", "LICENCIATURA EN DISEÑO GRÁFICO", "Manual universitario de referencia que ofrece una completa y crítica introducción a las industrias de los medios de comunicación de masas, examinando los cambios tecnológicos, la convergencia digital, el impacto sociocultural y los dilemas éticos que afronta el periodismo contemporáneo.", "covers/978-9708300865.jpeg");
        BookTitle book43 = seedBookTitle("Contemporary Graphic Design", "Charlotte Fiell y Peter Fiell", "978-3836521376", "Taschen", 2010, "Diseño gráfico / Arte visual moderno", "LICENCIATURA EN DISEÑO GRÁFICO", "Compendio visual exhaustivo que analiza las vanguardias del diseño internacional de principios del siglo XXI a través del trabajo de 100 de los estudios y diseñadores más progresistas del mundo, mostrando proyectos experimentales en áreas como identidad corporativa, tipografía, empaque, páginas web y cartelería publicitaria.", "covers/978-3836521376.jpeg");
        BookTitle book44 = seedBookTitle("Kórima (Número 2 / Ene-Jun 1986)", "Varios autores / Colegio de Lingüística y Literatura Hispánica de la UAP", null, "Universidad Autónoma de Puebla", 1986, "Literatura / Lingüística y Revistas académicas", "LICENCIATURA EN DISEÑO GRÁFICO", "Segundo número de esta publicación universitaria de investigación filológica y literaria que reúne ensayos críticos, análisis de teoría literaria y textos creativos enfocados en el desarrollo y difusión de la literatura hispánica y la lingüística regional.", "covers/cover-0e9aab8f-abd2-4441-9346-40b6b4112c3a.jpeg");

        // Ejemplares físicos
        seedBookCopy(book1, loc1, "INV-0001", CopyStatus.AVAILABLE, "Manual de referencia");
        seedBookCopy(book2, loc1, "INV-0002", CopyStatus.AVAILABLE, "Incluye casos prácticos");
        seedBookCopy(book3, loc2, "INV-0003", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(book4, loc2, "INV-0004", CopyStatus.MISSING, "Buen estado");
        seedBookCopy(book5, loc3, "INV-0005", CopyStatus.AVAILABLE, "Edición actualizada");
        seedBookCopy(book6, loc3, "INV-0006", CopyStatus.AVAILABLE, "Incluye CD interactivo");
        seedBookCopy(book7, loc4, "INV-0007", CopyStatus.AVAILABLE, "Ejemplar de sala");
        seedBookCopy(book8, loc4, "INV-0008", CopyStatus.AVAILABLE, "Buen estado");
        seedBookCopy(book9, loc5, "INV-0009", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(book10, loc5, "INV-0010", CopyStatus.AVAILABLE, "Préstamo interno");
        seedBookCopy(book11, loc6, "INV-0011", CopyStatus.AVAILABLE, "Ejemplar de consulta");
        seedBookCopy(book12, loc6, "INV-0012", CopyStatus.MISSING, "No localizado en inventario");
        seedBookCopy(book13, loc7, "INV-0013", CopyStatus.REMOVED, "Baja lógica por deterioro");
        seedBookCopy(book14, loc7, "INV-0014", CopyStatus.MISSING, "Edición desactualizada");
        seedBookCopy(book15, loc8, "0001", CopyStatus.AVAILABLE, "En buen estado");
        seedBookCopy(book16, loc9, "0002", CopyStatus.AVAILABLE, "Como nuevo");
        seedBookCopy(book17, loc10, "0003", CopyStatus.AVAILABLE, null);
        seedBookCopy(book18, loc11, "0004", CopyStatus.AVAILABLE, null);
        seedBookCopy(book19, loc12, "0005", CopyStatus.AVAILABLE, null);
        seedBookCopy(book20, loc13, "0006", CopyStatus.AVAILABLE, null);
        seedBookCopy(book21, loc14, "0007", CopyStatus.AVAILABLE, null);
        seedBookCopy(book22, loc15, "0008", CopyStatus.AVAILABLE, null);
        seedBookCopy(book23, loc16, "0009", CopyStatus.AVAILABLE, null);
        seedBookCopy(book24, loc17, "0010", CopyStatus.AVAILABLE, null);
        seedBookCopy(book25, loc18, "0011", CopyStatus.AVAILABLE, null);
        seedBookCopy(book26, loc19, "0012", CopyStatus.AVAILABLE, null);
        seedBookCopy(book27, loc20, "0013", CopyStatus.AVAILABLE, null);
        seedBookCopy(book28, loc21, "0014", CopyStatus.AVAILABLE, null);
        seedBookCopy(book29, loc22, "0015", CopyStatus.AVAILABLE, null);
        seedBookCopy(book30, loc23, "0016", CopyStatus.AVAILABLE, null);
        seedBookCopy(book31, loc24, "0017", CopyStatus.AVAILABLE, null);
        seedBookCopy(book32, loc25, "0018", CopyStatus.AVAILABLE, null);
        seedBookCopy(book33, loc26, "0019", CopyStatus.AVAILABLE, null);
        seedBookCopy(book34, loc27, "0020", CopyStatus.AVAILABLE, null);
        seedBookCopy(book35, loc28, "0021", CopyStatus.AVAILABLE, null);
        seedBookCopy(book36, loc29, "0022", CopyStatus.AVAILABLE, null);
        seedBookCopy(book37, loc30, "0023", CopyStatus.AVAILABLE, null);
        seedBookCopy(book38, loc31, "0024", CopyStatus.AVAILABLE, null);
        seedBookCopy(book39, loc32, "0025", CopyStatus.AVAILABLE, null);
        seedBookCopy(book40, loc33, "0026", CopyStatus.AVAILABLE, null);
        seedBookCopy(book41, loc34, "0027", CopyStatus.AVAILABLE, null);
        seedBookCopy(book42, loc35, "0028", CopyStatus.AVAILABLE, null);
        seedBookCopy(book43, loc36, "0029", CopyStatus.AVAILABLE, null);
        seedBookCopy(book44, loc37, "0030", CopyStatus.AVAILABLE, null);
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
