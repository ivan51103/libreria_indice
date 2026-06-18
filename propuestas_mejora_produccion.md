# Propuestas de Mejora para Nivel de Producción

Para llevar la aplicación **Índice Digital de Biblioteca** de un estado de Producto Mínimo Viable (MVP) a un entorno productivo real, robusto y escalable, se identifican las siguientes áreas críticas de mejora y refactorización técnica:

---

## 1. Persistencia y Gestión de Conexiones

### A. Pool de Conexiones (Connection Pooling)
- **Estado Actual**: En [ConnectionProvider.java](file:///home/debian/codex/biblioteca/src/main/java/com/biblioteca/config/ConnectionProvider.java), el método `getConnection()` llama directamente a `DriverManager.getConnection(...)` en cada invocación de repositorio, abriendo y cerrando conexiones físicas repetidamente.
- **Riesgo**: Alta ineficiencia en lecturas/escrituras concurrentes y degradación de rendimiento.
- **Mejora**: Integrar un pool de conexiones ligero como **HikariCP** para reutilizar conexiones abiertas y limitar el número máximo de conexiones activas a SQLite.

### B. Rutas de Base de Datos Dinámicas y Externalizadas
- **Estado Actual**: La ruta de la base de datos está hardcodeada de forma relativa al espacio de trabajo del proyecto: `Path.of("src", "main", "resources", "biblioteca.db")`.
- **Riesgo**: Si la aplicación se empaqueta en un JAR y se ejecuta en un servidor o máquina de usuario final, intentará crear o escribir en la carpeta de recursos de desarrollo (`src/main/resources`), lo cual fallará debido a falta de permisos o inexistencia de la carpeta.
- **Mejora**: Externalizar la ruta de la base de datos.
  1. Por defecto, ubicarla en el directorio de usuario (`System.getProperty("user.home") + "/.biblioteca/biblioteca.db"`).
  2. Permitir sobrescribir la ruta mediante variables de entorno o un archivo de configuración externa (`config.properties`).

### C. Herramientas de Migración de Esquema (Database Migrations)
- **Estado Actual**: Se utiliza [DatabaseInitializer.java](file:///home/debian/codex/biblioteca/src/main/java/com/biblioteca/config/DatabaseInitializer.java) con sentencias SQL estáticas de tipo `CREATE TABLE IF NOT EXISTS`.
- **Riesgo**: No existe forma de actualizar de manera incremental y controlada la estructura de la base de datos (p. ej., añadir una columna a `book_titles` en la versión 1.1) sin borrar el archivo `.db` existente o realizar actualizaciones manuales propensas a errores.
- **Mejora**: Integrar una herramienta de migración ligera como **Flyway** en su versión para Java embebido.

---

## 2. Consistencia de Datos y Gestión de Transacciones (ACID)

- **Estado Actual**: En [InventoryService.java](file:///home/debian/codex/biblioteca/src/main/java/com/biblioteca/service/InventoryService.java#L25-L42), el método `registerBook` ejecuta operaciones secuenciales e independientes en los repositorios de ubicación, título y ejemplar:
  ```java
  Location storedLocation = locationRepository.save(location);
  BookTitle storedTitle = bookTitleRepository.save(bookTitle);
  // ...
  bookCopyRepository.save(bookCopy);
  ```
- **Riesgo**: Si el guardado del ejemplar (`bookCopy`) falla (por ejemplo, debido a un código de inventario duplicado), la ubicación y el título ya habrán sido guardados permanentemente, dejando registros huérfanos o datos a medias en la base de datos (inconsistencia).
- **Mejora**: Implementar **Transacciones JDBC**. Se debe obtener una conexión compartida, establecer `connection.setAutoCommit(false)` en el servicio y hacer `commit()` solo si todas las operaciones tienen éxito. En caso de error, ejecutar un `rollback()` para restaurar el estado anterior.

---

## 3. Experiencia de Usuario y Robustez en la UI (JavaFX)

### A. Ejecución de Consultas Fuera del Hilo de UI (Asincronía)
- **Estado Actual**: Las consultas SQL a SQLite (como la búsqueda de catálogo y carga de detalles) se ejecutan de forma sincrónica en el hilo principal de JavaFX (`JavaFX Application Thread`).
- **Riesgo**: Si la base de datos crece sustancialmente, o si se migra a una base de datos en red (PostgreSQL/MySQL), la interfaz de usuario se congelará o se volverá inestable mientras se realizan consultas.
- **Mejora**: Envolver las llamadas de base de datos de los controladores en `Task<T>` o `Service<T>` de JavaFX, utilizando indicadores visuales de carga (p. ej., `ProgressIndicator`) mientras los datos se recuperan en un hilo de fondo.

### B. Manejo Global de Excepciones
- **Estado Actual**: Los errores en repositorios y servicios lanzan `IllegalStateException` o excepciones personalizadas que son atrapadas puntualmente o registradas en consola.
- **Riesgo**: Un error no controlado en producción (como disco lleno o fallo inesperado de base de datos) puede congelar la ventana del sistema o cerrarla abruptamente sin retroalimentación al usuario.
- **Mejora**: Configurar un manejador global de excepciones no capturadas (`Thread.setDefaultUncaughtExceptionHandler`) que muestre un diálogo de error amigable, y registrar el detalle técnico en un log físico.

---

## 4. Seguridad, Auditoría y Logging

### A. Cierre de Sesión por Inactividad
- **Estado Actual**: La sesión administrativa del `ADMIN` se mantiene activa permanentemente en la memoria local (`UserSession`) hasta que se cierra manualmente la aplicación.
- **Riesgo**: Si un administrador deja la terminal desatendida, cualquier usuario puede acceder a las funciones de administración.
- **Mejora**: Implementar un temporizador de inactividad basado en eventos de UI (movimientos del cursor, clics) que cierre la sesión del administrador automáticamente tras un período configurable.

### B. Bitácora de Auditoría (Audit Logs)
- **Estado Actual**: No existe registro histórico de las modificaciones realizadas en el inventario.
- **Riesgo**: Es imposible rastrear quién cambió un ejemplar a estado `REMOVED` o quién editó los datos de una obra.
- **Mejora**: Crear una tabla `audit_logs` que registre la fecha, el usuario, la acción (registro, edición, cambio de estado) y el identificador de los registros afectados.

### C. Sistema de Logging Profesional
- **Estado Actual**: Se imprimen excepciones y flujos mediante `System.out` o `printStackTrace()`.
- **Riesgo**: Las trazas de consola se pierden en entornos de producción y dificultan el diagnóstico de fallos reportados por usuarios.
- **Mejora**: Integrar una fachada de logging estándar como **SLF4J** con **Logback**, configurando un archivo de logs rotativo diario en una ubicación del sistema (p. ej., `/var/log/biblioteca/` o `%APPDATA%/biblioteca/logs/`).

---

## 5. Escalabilidad del Modelo y Despliegue

### A. Separación de Clientes y API (Si escala a Web)
- **Estado Actual**: Arquitectura monolítica de escritorio estrechamente acoplada a través de JDBC local.
- **Mejora**: Si en el futuro se desea tener acceso remoto al catálogo desde múltiples ordenadores, se debe migrar a una arquitectura Cliente/Servidor. El cliente JavaFX consumiría una API REST centralizada escrita en Spring Boot o Jakarta EE, facilitando además la creación de clientes web y móviles alternativos.
