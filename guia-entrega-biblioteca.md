# Guia de Entrega - Indice Digital de Biblioteca

## Objetivo de la entrega
Aplicacion JavaFX para consultar y administrar un catalogo de biblioteca fisica con persistencia en SQLite, busqueda flexible, detalle de ejemplares y ubicacion fisica.

## Requisitos
- JDK 17 o superior
- Maven

## Comandos principales

### Ejecutar pruebas
```bash
cd /home/debian/codex/biblioteca
mvn test
```

### Ejecutar demo desde Maven
```bash
cd /home/debian/codex/biblioteca
mvn javafx:run
```

### Generar paquete reproducible
```bash
cd /home/debian/codex/biblioteca
mvn clean package
```

### Ejecutar paquete generado
```bash
cd /home/debian/codex/biblioteca
java --module-path "target/dependency" --add-modules javafx.controls -cp "target/biblioteca-1.0-SNAPSHOT.jar:target/dependency/*" com.biblioteca.app.MainApp
```

## Credenciales
- Administrador: `admin`
- Contrasena: `admin123`
- Invitado: acceso directo desde la pantalla inicial

## Base de datos
- Motor: SQLite
- Archivo: `biblioteca/src/main/resources/biblioteca.db`
- Tablas: `users`, `book_titles`, `locations`, `book_copies`
- Inicializacion: `DatabaseInitializer`
- Datos semilla: `AppConfig`

Los datos semilla se cargan de forma idempotente por usuario, ISBN, codigo de ubicacion y codigo de inventario. Para una demo desde cero se puede eliminar `biblioteca/src/main/resources/biblioteca.db`; al iniciar, la aplicacion recrea tablas y datos base.

## Datos de demo incluidos
- Varios titulos de programacion, arquitectura, bases de datos, redes e historia.
- Varios ejemplares para algunas obras.
- Ubicaciones en salas y secciones distintas.
- Estados `AVAILABLE`, `MISSING`, `REPAIR` y `REMOVED`.

## Reglas de visibilidad
- Invitados no ven ejemplares `REMOVED`.
- Administradores si ven ejemplares `REMOVED` para gestion.
- `AVAILABLE` cuenta como disponible.
- `MISSING` y `REPAIR` son visibles, pero no cuentan como disponibles.

## Validacion final esperada
- `mvn test` debe terminar con la suite completa en verde.
- La pantalla inicial debe permitir entrar como invitado o administrador.
- El catalogo debe aparecer sin buscar.
- El detalle debe mostrar ejemplares, estado y ubicacion.
- El administrador debe poder registrar, editar y cambiar estado de ejemplares.
