# Contexto para Empezar el Desarrollo

## Vision del sistema
El sistema no es solo un buscador. Su eje principal es un catalogo visual navegable que debe mostrarse desde el inicio. La busqueda existe para ayudar, no para desbloquear el acceso al contenido.

## Problema que resuelve
Permite localizar libros y ejemplares de una biblioteca fisica de forma rapida, mostrando tanto informacion bibliografica como la ubicacion fisica exacta dentro del espacio de la biblioteca.

## Decisiones de producto ya tomadas
- El catalogo debe cargar desde la pantalla principal sin depender de una consulta previa.
- La aplicacion usa una sola base de interfaz para `GUEST` y `ADMIN`.
- La administracion es una extension del catalogo, no un sistema separado.
- Se debe preferir baja logica o cambio de estado antes que borrado fisico.
- La busqueda debe ser flexible y tolerar coincidencias parciales.

## Modelo de dominio que no debe romperse

### BookTitle
Representa la obra bibliografica.

Campos base:
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

Campos base:
- `id`
- `bookTitleId`
- `inventoryCode`
- `locationId`
- `status`
- `notes`

### Location
Representa la ubicacion fisica estructurada.

Campos base:
- `id`
- `room`
- `section`
- `shelf`
- `level`
- `position`
- `code`

## Regla conceptual clave
No tratar un libro y su copia fisica como la misma entidad. Si esa separacion se pierde, luego se complica la ubicacion, la disponibilidad y la gestion de multiples ejemplares.

## Roles del sistema

### GUEST
- explora catalogo
- busca libros
- ve detalle
- consulta ubicacion fisica

### ADMIN
- todo lo de `GUEST`
- registra libros y ejemplares
- edita informacion
- cambia estado de ejemplares
- realiza baja logica

## Estados de ejemplar
Estados definidos:
- `AVAILABLE`
- `MISSING`
- `REMOVED`

Preguntas que deben cerrarse antes de implementar totalmente la UI:
- si `MISSING` se muestra o no en la vista publica
- si `REMOVED` desaparece del catalogo o solo del inventario visible

## Busqueda
La busqueda inicial debe cumplir como minimo esto:
- coincidencia parcial
- por titulo y autor
- sin importar mayusculas
- idealmente sin importar acentos

Mas adelante puede crecer a:
- multiples terminos
- sugerencias
- fuzzy search
- ranking de relevancia

## Arquitectura que conviene respetar
- `config`: configuracion e infraestructura
- `security`: usuarios, roles, sesion, autenticacion
- `domain`: entidades y enums del negocio
- `search.query`: criterios, paginacion y view models
- `repository`: acceso a datos
- `service`: reglas de negocio y orquestacion
- `ui.controller`: coordinacion de eventos y flujo
- `ui.view`: interfaz visual
- `app`: arranque de la aplicacion

## Responsabilidades por capa
- `repository` no debe decidir reglas de negocio
- `service` no debe convertirse en copia del repository
- `controller` no debe concentrar logica de negocio
- `view` no debe conocer persistencia
- `AppConfig` no debe terminar cargando logica funcional

## Estado tecnico actual del proyecto
- proyecto Java con `Maven`
- dependencia `sqlite-jdbc`
- inicializacion de tablas `SQLite`
- repositorios en memoria funcionales
- servicios base cableados
- datos semilla de ejemplo

## Pendientes tecnicos inmediatos
- implementar repositorios `SQLite` reales
- decidir tecnologia final de interfaz
- mover el flujo principal desde consola o stubs a UI real
- agregar pruebas automatizadas
- definir configuracion entre modo memoria y modo base de datos

## Datos de prueba que se necesitan
Para que el desarrollo tenga valor real, se recomienda cargar:
- varios libros del mismo autor
- varios ejemplares del mismo titulo
- ejemplares en diferentes estados
- varias ubicaciones reales
- categorias y carreras distintas

## Riesgos de implementacion
- construir primero formularios admin y dejar incompleto el catalogo
- no definir bien la estructura de ubicacion fisica
- dejar estados ambiguos
- no validar `inventoryCode`
- acoplar la UI directamente a `SQLite`

## Criterios practicos antes de programar nuevas funciones
Antes de agregar una funcionalidad nueva, revisar:
- que rol la puede usar
- si afecta `BookTitle`, `BookCopy` o ambos
- si cambia disponibilidad publica
- si requiere persistencia real o puede arrancar en memoria
- si necesita validaciones nuevas
- si cambia el diagrama o las reglas del dominio

## Recomendacion de arranque
El desarrollo debe empezar por el flujo principal del catalogo:
1. persistencia `SQLite`
2. listado del catalogo
3. detalle con ubicacion
4. busqueda flexible
5. login admin
6. administracion de inventario

## Meta tecnica inmediata
Pasar del esqueleto actual a una aplicacion que ya permita:
- levantar datos desde `SQLite`
- mostrar libros desde el inicio
- consultar detalle real
- registrar cambios persistentes
