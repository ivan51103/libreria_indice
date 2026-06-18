# Indice Digital de Biblioteca

Aplicacion Java para consultar y administrar el catalogo de una biblioteca fisica, con enfoque en exploracion visual, detalle bibliografico y ubicacion fisica exacta de ejemplares. La direccion visual objetivo es un estilo tipo Netflix, con predominio de colores azules y blancos, tarjetas limpias y detalle destacado.

## Estado actual
- Aplicacion Java con `Maven` y UI en `JavaFX`
- Persistencia principal en `SQLite` mediante repositorios JDBC
- Catalogo visible desde el inicio para invitados y administradores, con una UI orientada a una experiencia visual tipo Netflix
- Busqueda por texto, autor, categoria, carrera y disponibilidad
- Detalle de libro con ejemplares, estados, ubicaciones y una presentacion visual mas cinematografica
- Administracion basica para alta, edicion y cambio de estado de ejemplares
- Autenticacion de administrador con hash `PBKDF2WithHmacSHA256`
- Pruebas automatizadas con `JUnit 5`
- Estados vigentes de ejemplar: `AVAILABLE`, `MISSING`, `REMOVED`

## Tecnologias usadas
- `Java 17`
- `Maven`
- `JavaFX`
- `SQLite`
- `JDBC`
- `JUnit 5`
- `PlantUML`

## Como ejecutar

### Requisitos
- `JDK 17` o superior
- `Maven`

### Compilar
```bash
cd /home/debian/codex/biblioteca
mvn compile
```

### Ejecutar la aplicacion JavaFX
```bash
cd /home/debian/codex/biblioteca
mvn javafx:run
```

### Ejecutar pruebas
```bash
cd /home/debian/codex/biblioteca
mvn test
```

### Empaquetar
```bash
cd /home/debian/codex/biblioteca
mvn clean package
```

El empaquetado genera:
- `target/biblioteca-1.0-SNAPSHOT.jar`
- `target/dependency/` con dependencias de ejecucion

### Ejecutar el paquete generado
```bash
cd /home/debian/codex/biblioteca
java --module-path "target/dependency" --add-modules javafx.controls -cp "target/biblioteca-1.0-SNAPSHOT.jar:target/dependency/*" com.biblioteca.app.MainApp
```

## Credenciales de prueba
- usuario: `admin`
- password: `admin123`

Tambien existe acceso como invitado desde la pantalla inicial.

## Base de datos
- Motor: `SQLite`
- Ruta configurada en ejecucion: `~/.biblioteca/biblioteca.db`
- Inicializacion automatica: `DatabaseInitializer`
- Datos semilla: `AppConfig`

Tablas principales:
- `users`
- `book_titles`
- `locations`
- `book_copies`

Los datos semilla se cargan de forma idempotente por usuario, ISBN, codigo de ubicacion y codigo de inventario. Esto permite ampliar datos de demo sin duplicar registros.

Para una demo limpia se puede eliminar `~/.biblioteca/biblioteca.db` antes de ejecutar. La aplicacion recrea tablas y datos semilla al iniciar.

## Flujo funcional esperado
1. El usuario entra como invitado o administrador.
2. El catalogo se muestra desde el inicio.
3. El usuario busca o filtra libros si lo necesita.
4. El usuario abre el detalle de un libro.
5. El sistema muestra ejemplares, estado y ubicacion fisica.
6. Si el usuario es `ADMIN`, puede registrar, editar o cambiar estado de ejemplares.

## Reglas importantes del dominio
- `BookTitle` representa la obra bibliografica.
- `BookCopy` representa el ejemplar fisico.
- `Location` representa la ubicacion estructurada.
- Se prefiere baja logica mediante estado antes que borrado fisico.
- `REMOVED` se oculta para invitados.
- `AVAILABLE` cuenta como disponible.
- `MISSING` es visible, pero no cuenta como disponible.

## Paquetes principales
- `app`: arranque de la aplicacion
- `config`: configuracion, conexion y datos semilla
- `domain`: entidades y enums del negocio
- `security`: usuario, rol, sesion y autenticacion
- `search.query`: criterios, paginacion y modelos de vista
- `repository`: contratos de persistencia
- `repository.sqlite`: implementaciones JDBC sobre `SQLite`
- `repository.memory`: implementaciones usadas en pruebas unitarias
- `service`: reglas de negocio y orquestacion
- `ui.controller`: coordinacion de eventos y flujo
- `ui.view`: interfaz JavaFX

## Documentacion del proyecto
- [plan-desarrollo-biblioteca.md](/home/debian/codex/plan-desarrollo-biblioteca.md)
- [avance-reciente.md](/home/debian/codex/avance-reciente.md)
- [guia-entrega-biblioteca.md](/home/debian/codex/guia-entrega-biblioteca.md)
- [project_context.md](/home/debian/codex/project_context.md)
- [contexto-inicio-desarrollo-biblioteca.md](/home/debian/codex/contexto-inicio-desarrollo-biblioteca.md)
- [diagrama-contexto-biblioteca.puml](/home/debian/codex/diagrama-contexto-biblioteca.puml)
- [diagrama-casos-uso-biblioteca.puml](/home/debian/codex/diagrama-casos-uso-biblioteca.puml)
- [diagrama-clases-biblioteca.puml](/home/debian/codex/diagrama-clases-biblioteca.puml)

## Estado de pruebas
La suite automatizada cubre seguridad, autorizacion, servicios de inventario, reglas de visibilidad, repositorios `SQLite` e integracion del flujo alta-consulta-cambio de estado.

```bash
cd /home/debian/codex/biblioteca
mvn test
```
