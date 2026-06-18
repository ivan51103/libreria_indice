# Avance Reciente

## Lo que se hizo
- Se conecto el proyecto a `SQLite` como persistencia principal.
- Se implementaron repositorios JDBC para usuarios, libros, ejemplares y ubicaciones.
- Se actualizo `AppConfig` para usar repositorios `SQLite` en el flujo principal.
- Se dejo la inicializacion automatica de tablas en `DatabaseInitializer`.
- Se agrego carga inicial de datos de prueba y usuario `admin` sin duplicar registros.
- Se corrigio el manejo de conexiones JDBC para cerrar recursos correctamente.

## Resultado actual
- La aplicacion compila con `Maven`.
- La aplicacion arranca correctamente.
- El catalogo ya lee datos desde `SQLite`.
- Actualmente se cargan 2 libros de ejemplo en la base local.

## Archivos clave modificados
- `biblioteca/src/main/java/com/biblioteca/config/AppConfig.java`
- `biblioteca/src/main/java/com/biblioteca/config/DatabaseInitializer.java`
- `biblioteca/src/main/java/com/biblioteca/repository/sqlite/*`
- `biblioteca/src/main/java/com/biblioteca/app/MainApp.java`

## Pendiente inmediato
- Construir la UI real del catalogo
- Mostrar detalle con ubicacion fisica
- Implementar validaciones de negocio
- Reemplazar password en texto plano por hash

## Actualizacion de fase siguiente
- Se implemento una UI base en `Swing` para acceso y catalogo publico.
- Se agrego pantalla de acceso con entrada como invitado y login de administrador.
- El catalogo ahora muestra libros desde `SQLite`.
- Se agrego busqueda basica por titulo y autor.
- Se puede abrir el detalle de un libro y ver ejemplares, estado y ubicacion fisica.

## Archivos clave agregados o ampliados en esta fase
- `biblioteca/src/main/java/com/biblioteca/ui/view/LoginView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/CatalogView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/controller/LoginController.java`
- `biblioteca/src/main/java/com/biblioteca/ui/controller/CatalogController.java`
- `biblioteca/src/main/java/com/biblioteca/app/MainApp.java`

## Validacion realizada
- El proyecto compila correctamente con `Maven`.
- La validacion visual manual queda pendiente porque esta fase usa interfaz grafica interactiva.

## Actualizacion JavaFX y fase admin
- Se reemplazo la UI base en `Swing` por una UI en `JavaFX`.
- Se agrego soporte `JavaFX` en `Maven`.
- El arranque principal ahora usa `Application` de JavaFX.
- La vista de acceso abre el catalogo en interfaz JavaFX.
- El catalogo muestra listado, busqueda y detalle desde `SQLite`.
- Para `ADMIN` se agregaron acciones minimas para registrar libro con ejemplar y cambiar estado de ejemplar.

## Archivos clave de esta fase
- `biblioteca/pom.xml`
- `biblioteca/src/main/java/com/biblioteca/app/MainApp.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/LoginView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/CatalogView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/BookAdminView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/controller/BookAdminController.java`

## Pendiente inmediato actualizado
- validacion visual manual de la interfaz JavaFX
- formularios de edicion de libro existente
- validaciones de negocio mas estrictas
- hash de contrasenas

## Actualizacion de administracion y validaciones
- Se agregaron validaciones de negocio para evitar duplicados en ISBN, codigo de inventario y codigo de ubicacion.
- Se agrego una excepcion de negocio para mostrar errores mas claros en interfaz.
- La administracion ahora permite editar el libro seleccionado, su ejemplar fisico y su ubicacion.
- El formulario admin se reutiliza tanto para alta como para edicion.
- Se mantienen mensajes claros cuando una operacion no puede guardarse.

## Archivos clave de esta fase
- `biblioteca/src/main/java/com/biblioteca/service/BusinessException.java`
- `biblioteca/src/main/java/com/biblioteca/service/InventoryService.java`
- `biblioteca/src/main/java/com/biblioteca/repository/BookTitleRepository.java`
- `biblioteca/src/main/java/com/biblioteca/repository/BookCopyRepository.java`
- `biblioteca/src/main/java/com/biblioteca/repository/LocationRepository.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/BookAdminView.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/CatalogView.java`

## Resultado actual
- ya se puede registrar libro, ejemplar y ubicacion
- ya se puede editar el registro seleccionado
- ya se puede cambiar estado del ejemplar
- el sistema bloquea duplicados comunes antes de guardar

## Actualizacion de seguridad
- Se reemplazo la comparacion de contrasena en texto plano por hash con `PBKDF2WithHmacSHA256`.
- Se agrego `PasswordService` para generar y verificar hashes.
- El usuario `admin` sembrado ahora se guarda con hash.
- Si la base ya tenia un password legado en texto plano, el sistema lo migra automaticamente.

## Archivos clave de seguridad
- `biblioteca/src/main/java/com/biblioteca/security/PasswordService.java`
- `biblioteca/src/main/java/com/biblioteca/security/AuthenticationService.java`
- `biblioteca/src/main/java/com/biblioteca/config/AppConfig.java`

## Actualizacion de pruebas automatizadas
- Se agrego infraestructura de pruebas con `JUnit 5`.
- Se implementaron pruebas para hash y verificacion de contrasenas.
- Se implementaron pruebas para autenticacion y migracion de password legado.
- Se implementaron pruebas para `InventoryService`, alta de registros y validaciones de duplicados.
- La suite actual ejecuta correctamente con `mvn test`.

## Archivos clave de pruebas
- `biblioteca/src/test/java/com/biblioteca/security/PasswordServiceTest.java`
- `biblioteca/src/test/java/com/biblioteca/security/AuthenticationServiceTest.java`
- `biblioteca/src/test/java/com/biblioteca/service/InventoryServiceTest.java`

## Actualizacion de visibilidad por estado
- Se definieron reglas explicitas entre vista publica y vista administrativa.
- Para invitados, los ejemplares `REMOVED` ya no aparecen en catalogo ni en detalle.
- Para administradores, los ejemplares `REMOVED` siguen visibles para gestion.
- Los ejemplares `AVAILABLE` cuentan como disponibles.
- Los ejemplares `MISSING` siguen visibles, pero no cuentan como disponibles.
- Si un libro solo tiene ejemplares `REMOVED`, desaparece del catalogo publico.

## Cobertura agregada para esta regla
- `biblioteca/src/test/java/com/biblioteca/service/CatalogServiceTest.java`

## Actualizacion de fase 8 - Pruebas SQLite e integracion
- Se agregaron pruebas automatizadas para repositorios `SQLite` usando bases temporales.
- Se cubrio persistencia y actualizacion de libros, ejemplares y ubicaciones.
- Se cubrio busqueda con coincidencia parcial, filtros, ordenamiento, paginacion e ignorando acentos.
- Se agrego una prueba de integracion para el flujo alta de inventario, consulta de catalogo y detalle con ubicacion fisica.
- Se agrego cobertura de integracion para baja logica mediante estado `REMOVED` y su visibilidad diferenciada entre invitado y administrador.

## Archivos clave de esta fase
- `biblioteca/src/test/java/com/biblioteca/repository/sqlite/SQLiteRepositoryTest.java`
- `biblioteca/src/test/java/com/biblioteca/integration/InventoryCatalogSQLiteIntegrationTest.java`

## Validacion realizada
- La suite completa ejecuta correctamente con `mvn test`.
- Resultado actual: 24 pruebas, 0 fallos, 0 errores.

## Cierre de fase 8 - Pruebas
- Se agregaron pruebas para `SQLiteUserRepository`, cubriendo alta, consulta por usuario, actualizacion y usuario inexistente.
- Se agregaron pruebas para `AuthorizationService`, confirmando que solo una sesion activa `ADMIN` puede administrar catalogo.
- Se amplio `CatalogServiceTest` para cubrir filtros por categoria/carrera, disponibilidad sin contar `MISSING`, y paginacion despues de ocultar ejemplares `REMOVED`.
- Con esto quedan cubiertos los puntos principales de la fase: servicios, seguridad, busqueda/filtros, repositorios `SQLite` e integracion alta-consulta-cambio de estado.

## Archivos clave agregados o ampliados
- `biblioteca/src/test/java/com/biblioteca/repository/sqlite/SQLiteUserRepositoryTest.java`
- `biblioteca/src/test/java/com/biblioteca/service/AuthorizationServiceTest.java`
- `biblioteca/src/test/java/com/biblioteca/service/CatalogServiceTest.java`

## Validacion final de fase 8
- La suite completa ejecuta correctamente con `mvn test`.
- Resultado actual: 32 pruebas, 0 fallos, 0 errores.

## Preparacion previa a fase 9
- Se ampliaron los datos semilla para demo con mas titulos, varios ejemplares por obra, ubicaciones distintas y estados `AVAILABLE`, `MISSING` y `REMOVED`.
- El sembrado ahora es idempotente por usuario, ISBN, codigo de ubicacion y codigo de inventario, para enriquecer bases existentes sin duplicar registros.
- Se actualizo `README.md` para reflejar el estado real del proyecto: JavaFX, SQLite, credenciales, ejecucion, pruebas y reglas de visibilidad.
- Se actualizo JavaFX de `21.0.2` a `21.0.10` y la compilacion Maven a `release 17`, eliminando warnings previos de OpenJFX y del compilador.
- Se corrigio un warning de tipos sin verificar en `CatalogView` y se evito registrar repetidamente el listener del selector de ejemplares al cargar detalles.

## Archivos clave actualizados
- `README.md`
- `biblioteca/pom.xml`
- `biblioteca/src/main/java/com/biblioteca/config/AppConfig.java`
- `biblioteca/src/main/java/com/biblioteca/ui/view/CatalogView.java`

## Validacion previa a fase 9
- La suite completa ejecuta correctamente con `mvn test`.
- Resultado actual: 32 pruebas, 0 fallos, 0 errores.

## Cierre de fase 9 - Preparacion para entrega
- Se agrego una guia de entrega con requisitos, comandos de ejecucion, empaquetado, credenciales, base de datos, datos de demo y reglas de visibilidad.
- Se amplio `README.md` con instrucciones para empaquetar y ejecutar el paquete generado.
- Se configuro Maven para generar un JAR con `Main-Class` y copiar dependencias runtime a `target/dependency`.
- Se verifico que el manifiesto del JAR apunte a `com.biblioteca.app.MainApp`.
- Se confirmo la politica de base de datos: se puede entregar `biblioteca.db` incluida o eliminarla para que la aplicacion reconstruya tablas y datos semilla al iniciar.

## Archivos clave de fase 9
- `guia-entrega-biblioteca.md`
- `README.md`
- `biblioteca/pom.xml`
- `biblioteca/src/main/java/com/biblioteca/ui/view/CatalogView.java`

## Validacion final de fase 9
- `mvn clean package` ejecuta correctamente.
- Resultado de pruebas dentro del paquete: 32 pruebas, 0 fallos, 0 errores.
- Artefactos generados:
  - `biblioteca/target/biblioteca-1.0-SNAPSHOT.jar`
  - `biblioteca/target/dependency/`

## Preparacion para repositorio privado
- Se inicializo un repositorio Git local en rama `main`.
- Se confirmo que no se versionan artefactos generados, JARs, `target/`, bases SQLite ni archivos locales de Codex.
- Se agrego `biblioteca/src/main/resources/.gitkeep` para conservar la carpeta de recursos sin subir `biblioteca.db`.
- Se creo el commit inicial estable del proyecto.

## Fase 10 - Preparación para Producción Monopuesto
- Se modificó `ConnectionProvider.java` para utilizar una ruta de base de datos dinámica localizada en el directorio de usuario (`~/.biblioteca/biblioteca.db`).
- Se añadió la creación automática del directorio padre de la base de datos al arrancar para evitar fallos de inicialización.
- Se verificó la suite completa de pruebas (32 pruebas pasando con éxito).
