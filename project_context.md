# Indice Digital de Biblioteca - Contexto General Actualizado

Este documento resume el contexto real del proyecto a partir del codigo, las pruebas y la documentacion existente. La idea es que funcione como referencia tecnica y de producto para continuar desarrollo sin depender de documentos desfasados.

---

## 1. Resumen Ejecutivo

La aplicacion es un sistema de escritorio para una biblioteca fisica con dos modos de uso:
- `GUEST`: consulta publica del catalogo.
- `ADMIN`: administracion de obras, ejemplares y ubicaciones.

La solucion esta pensada para operar en un solo equipo, con una sola base local, y con un catalogo de tamano moderado. La persistencia real usa `SQLite` y la interfaz esta implementada en `JavaFX`.

Estado funcional actual:
- El arranque principal abre una pantalla de acceso en `JavaFX`.
- El usuario puede entrar como invitado o autenticarse como administrador.
- El catalogo se carga desde `SQLite` y muestra obras, disponibilidad y detalle.
- La administracion permite registrar, editar y cambiar estado de ejemplares.
- La autenticacion usa hash de contrasena con `PBKDF2WithHmacSHA256`.
- La suite automatizada esta estable y se ejecuta con `mvn test`.
- Una meta de producto vigente es un diseño general estilo Netflix, con predominio de colores azules y blancos y una experiencia visual mas pulida.

---

## 2. Alcance Real Del Sistema

El proyecto esta disenado para:
- operar en modo monopuesto;
- servir a una unica persona administradora;
- permitir consulta publica sin friccion;
- mostrar ubicacion fisica exacta de cada ejemplar;
- evitar borrado fisico como mecanismo normal de operacion.

Supuestos de producto vigentes:
- La biblioteca es fisica.
- El catalogo se consulta desde una sola interfaz.
- El acceso publico no requiere cuenta.
- El inventario se gestiona por ejemplar, no solo por titulo.

---

## 3. Estado Tecnico Actual

Tecnologias y componentes presentes:
- Java 17
- Maven
- JavaFX 21.0.10
- SQLite JDBC 3.46.1.3
- JUnit 5.10.2

Punto de entrada:
- `com.biblioteca.app.MainApp`

Persistencia:
- SQLite como backend definitivo.
- Repositorios JDBC reales para usuarios, titulos, ejemplares y ubicaciones.
- Repositorios en memoria conservados para pruebas unitarias.

Empaquetado:
- El `pom.xml` genera un JAR ejecutable.
- Se copian dependencias runtime a `target/dependency`.
- El manifiesto apunta a `com.biblioteca.app.MainApp`.

Ubicacion de base de datos en ejecucion:
- `~/.biblioteca/biblioteca.db`

Esto es importante porque algunos documentos viejos aun mencionan `src/main/resources/biblioteca.db`, pero el codigo ya usa la ruta dinamica de usuario.

---

## 4. Arquitectura Por Capas

La estructura del proyecto sigue una separacion clasica por responsabilidades:

```
com.biblioteca
├── app
├── config
├── domain
├── repository
│   ├── memory
│   └── sqlite
├── search.query
├── security
├── service
└── ui
    ├── controller
    └── view
```

Responsabilidad de cada capa:
- `app`: arranque de la aplicacion.
- `config`: conexion, inicializacion y cableado de dependencias.
- `domain`: entidades y enums del negocio.
- `repository`: contratos de persistencia.
- `repository.sqlite`: implementaciones JDBC.
- `repository.memory`: dobles de prueba.
- `search.query`: criterios, paginacion y view models.
- `security`: usuarios, roles, sesion y contrasenas.
- `service`: reglas de negocio y composicion de datos.
- `ui.controller`: puente entre vista y servicios.
- `ui.view`: interfaz `JavaFX`.

---

## 5. Modelo De Dominio

### BookTitle
Representa la obra bibliografica.

Campos:
- `id`
- `title`
- `author`
- `isbn`
- `publisher`
- `year`
- `category`
- `career`
- `description`
- `coverPath`

### BookCopy
Representa el ejemplar fisico.

Campos:
- `id`
- `bookTitleId`
- `inventoryCode`
- `locationId`
- `status`
- `notes`

### Location
Representa la ubicacion fisica estructurada.

Campos:
- `id`
- `room`
- `section`
- `shelf`
- `level`
- `position`
- `code`

### CopyStatus
Estado actual vigente en el codigo:
- `AVAILABLE`
- `MISSING`
- `REMOVED`

Nota importante:
- `REPAIR` ya no existe en el codigo fuente principal.
- Algunos documentos historicos aun lo mencionan, pero el estado real del proyecto ya fue simplificado a tres valores.

### Security
Usuarios y roles:
- `User`
- `UserRole.GUEST`
- `UserRole.ADMIN`
- `UserSession`

---

## 6. Reglas De Negocio Confirmadas

### Visibilidad de ejemplares
- `GUEST` no ve ejemplares `REMOVED`.
- Si un titulo solo tiene ejemplares `REMOVED`, desaparece del catalogo publico.
- `ADMIN` ve tambien los ejemplares `REMOVED`.
- `AVAILABLE` cuenta como disponible.
- `MISSING` se ve, pero no cuenta como disponible.

### Baja logica
- El flujo preferido para retiro de inventario es cambiar el estado a `REMOVED`.
- El borrado fisico existe en algunos repositorios, pero no es el flujo de negocio principal.

### Unicidad de datos
El sistema valida:
- ISBN unico por titulo.
- `inventoryCode` unico por ejemplar.
- `Location.code` unico por ubicacion.

### Datos obligatorios
- Titulo y autor son obligatorios.
- El codigo de inventario es obligatorio.
- La ubicacion visible requiere codigo de ubicacion.
- Sala, seccion, estante y nivel se conservan internamente para compatibilidad con SQLite y pueden tomar un valor por defecto cuando no se capturan en la UI.

---

## 7. Persistencia Y Semilla

### Inicializacion
`DatabaseInitializer` crea al arrancar las tablas:
- `users`
- `book_titles`
- `locations`
- `book_copies`

### Cableado
`AppConfig` crea en este orden:
- `ConnectionProvider`
- `DatabaseInitializer`
- repositorios SQLite
- `PasswordService`
- `AuthenticationService`
- `SearchService`
- `CatalogService`
- `BookCatalogService`
- `InventoryService`
- `AuthorizationService`

### Semilla
`AppConfig` tambien siembra:
- usuario `admin`
- libros de ejemplo
- ubicaciones fisicas
- ejemplares con distintos estados

La siembra es idempotente por:
- username
- ISBN
- codigo de ubicacion
- codigo de inventario

### Ruta real de la base
La conexion ya no depende de un recurso embebido en `src/main/resources`.
El archivo real se crea en:
- `~/.biblioteca/biblioteca.db`

Si la base no existe, la aplicacion crea el directorio y reconstruye tablas y semilla al arrancar.

---

## 8. Seguridad Y Autenticacion

### Mecanismo
- Hash de contrasena con `PBKDF2WithHmacSHA256`
- Comparacion segura de hashes
- Migracion automatica de passwords legados en texto plano

### Flujo
- `AuthenticationService` valida credenciales.
- Si detecta una contrasena antigua sin hash, la rehace y la guarda.
- `loginAsGuest()` crea una sesion de invitado sin pasar por base de datos.

### Credenciales por defecto
- Usuario: `admin`
- Contrasena: `admin123`

---

## 9. Catalogo, Busqueda Y Detalle

### Servicio de catalogo
`CatalogService` arma la vista publica o administrativa a partir de:
- titulos
- ejemplares
- ubicaciones

### Filtros actuales
La busqueda soporta:
- texto por titulo
- autor
- categoria
- carrera
- filtro de disponibilidad

### Normalizacion
La logica de busqueda ignora:
- mayusculas y minusculas
- acentos

### Modelos de vista
- `BookCatalogItemView`: resumen de catalogo.
- `BookDetailViewModel`: detalle de obra, ejemplares y ubicaciones.
- `PageRequest` y `PageResult`: paginacion.

### Situacion actual del buscador
- Hay busqueda flexible operativa.
- `SearchService.suggest()` existe, pero aun devuelve lista vacia.
- El sistema no tiene fuzzy search ni autocomplete real todavia.

---

## 10. Interfaz De Usuario

### Pantallas actuales
- `LoginView`
- `CatalogView`
- `BookAdminView`

### Flujo actual
1. Se abre login.
2. El usuario entra como invitado o administrador.
3. El catalogo aparece desde el inicio.
4. Se puede buscar y abrir detalle.
5. Si es admin, puede registrar, editar y cambiar estado de ejemplares.

### Estado visual real
- La UI esta en `JavaFX`.
- La mayoria de textos estan en espanol.
- Aun hay campos y etiquetas que pueden seguir refinandose.
- `CatalogView` sigue usando una tabla con panel de detalle textual, no una grilla estilo Netflix.
- `BookAdminView` aun usa campo de texto para `coverPath`; no hay `FileChooser` real.
- La direccion visual objetivo ya esta definida: estilo Netflix, con base cromatica azul y blanca, tarjetas mas limpias y jerarquia visual mas marcada.

### Pendientes de UI que siguen vigentes
- Castellanizacion completa y consistente.
- Selector nativo para imagenes de portada.
- Rediseno visual del catalogo con tarjetas de portada.
- Mejoras de presentacion del detalle.

---

## 11. Administracion Del Inventario

### Operaciones disponibles
- registrar libro, ejemplar y ubicacion en conjunto
- editar titulo, ejemplar y ubicacion
- editar solo titulo
- editar solo ejemplar
- cambiar estado de ejemplar

### Servicio central
`InventoryService` coordina:
- validaciones de negocio
- validaciones de unicidad
- persistencia de titulo, copia y ubicacion

### Estado de atomicidad
- La logica de negocio ya esta separada y validada.
- Todavia no hay una implementacion explicita de transacciones SQLite en el servicio.
- Este punto sigue siendo una mejora pendiente si se quiere garantizar rollback de operaciones compuestas.

---

## 12. Pruebas Automatizadas

La suite de pruebas cubre:
- hashing y verificacion de contrasenas
- autenticacion y migracion de password legado
- autorizacion de rol
- repositorios SQLite
- servicios de inventario
- reglas de visibilidad del catalogo
- integracion entre inventario y catalogo

### Hecho importante sobre Maven
El `pom.xml` agrega `src/test-fixed/java` como fuente adicional de pruebas.
Tambien excluye dos pruebas originales del arbol principal porque existian variantes corregidas en `src/test-fixed`.

Esto significa que:
- el proyecto no solo depende de `src/test/java`
- hay una capa de correccion adicional para mantener la suite verde

### Estado de la suite
La documentacion reciente reporta:
- `32` pruebas
- `0` fallos
- `0` errores

---

## 13. Documentacion Existente Y Desalineaciones

Hay varios archivos de contexto y planificacion. Los mas relevantes son:
- `avance-reciente.md`
- `task.md`
- `implementation_plan.md`
- `project_context.md`
- `README.md`
- `guia-entrega-biblioteca.md`
- `contexto-inicio-desarrollo-biblioteca.md`
- `plan-desarrollo-biblioteca.md`

Desalineaciones detectadas:
- algunos documentos aun hablan de `REPAIR` como estado vigente;
- el `README` raiz aun referencia la base en `src/main/resources/biblioteca.db`;
- el codigo ya usa base dinamica en `~/.biblioteca/biblioteca.db`;
- el plan inicial incluye pendientes que ya fueron resueltos en el codigo actual.

La referencia mas confiable para estado real sigue siendo el codigo fuente y la suite de pruebas.

---

## 14. Riesgos Y Deuda Tecnica Actual

Puntos que conviene tratar como deuda activa:
- terminar de unificar la documentacion antigua con el estado real del codigo;
- completar el rediseno visual del catalogo;
- implementar carga/copia real de portadas en disco;
- agregar transacciones explicitas para flujos compuestos de inventario;
- decidir si `delete()` debe permanecer expuesto o restringirse mas a nivel de servicio;
- completar el buscador inteligente si la experiencia lo requiere;
- revisar si la base de datos semilla debe vivirse solo como demo o como parte del despliegue real.

---

## 15. Orden Recomendado Para Seguir

Si se continua desarrollo, el orden mas coherente hoy es:
1. Consolidar la castellanizacion restante.
2. Implementar `FileChooser` y almacenamiento real de portadas.
3. Agregar transacciones SQLite para operaciones compuestas.
4. Redisenar `CatalogView` con tarjetas visuales.
5. Mejorar filtros y sugerencias de busqueda.
6. Mantener y ampliar pruebas de regresion.

---

## 16. Criterio Final Del Proyecto

El proyecto ya no es un prototipo inicial. Es una aplicacion funcional de biblioteca local con:
- persistencia SQLite real;
- autenticacion de administrador;
- consulta publica desde el arranque;
- reglas de visibilidad por estado;
- inventario estructurado por obra, ejemplar y ubicacion;
- suite de pruebas automatizadas estable.

Lo que falta no es la base del sistema, sino refinamiento funcional y visual para acercarlo a la experiencia de producto final.
