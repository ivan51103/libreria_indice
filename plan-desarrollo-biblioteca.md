# Plan de Desarrollo - Indice Digital de Biblioteca

## Objetivo
Construir una aplicacion Java para consultar y administrar el catalogo de una biblioteca fisica, priorizando la exploracion visual del catalogo, la consulta de detalle y la localizacion fisica exacta de ejemplares.

## Estado actual
- Existe una base de proyecto Java con `Maven`.
- Ya estan definidas las capas principales del sistema.
- Existe modelo base para `BookTitle`, `BookCopy`, `Location`, seguridad, busqueda y servicios.
- Hay repositorios en memoria funcionales para pruebas iniciales.
- La conexion e inicializacion de `SQLite` ya estan configuradas.
- Existen diagramas de contexto, casos de uso y clases base.

## Alcance recomendado del MVP
El MVP debe incluir solo lo necesario para validar el flujo principal del sistema.

- Acceso como invitado
- Acceso como administrador
- Catalogo visible desde el inicio
- Busqueda por titulo y autor con coincidencia parcial
- Filtros basicos
- Vista de detalle del libro
- Visualizacion de ubicacion fisica
- Registro de libro y ejemplar por admin
- Edicion de informacion del libro
- Cambio de estado de ejemplar

## Fase 1 - Consolidacion tecnica
Objetivo: dejar la base lista para desarrollo continuo.

- Revisar el esqueleto actual y limpiar responsabilidades entre capas
- Definir convenciones de nombres, paquetes y estilo de codigo
- Crear archivo de configuracion para entorno local
- Confirmar estrategia de UI
- Elegir si la primera interfaz sera `JavaFX`, `Swing` o una consola temporal

Entregables:
- estructura del proyecto estabilizada
- decisiones tecnicas documentadas
- app ejecutando sin errores

## Fase 2 - Persistencia real con SQLite
Objetivo: reemplazar repositorios en memoria por repositorios JDBC sobre `SQLite`.

- Implementar `SQLiteUserRepository`
- Implementar `SQLiteBookTitleRepository`
- Implementar `SQLiteBookCopyRepository`
- Implementar `SQLiteLocationRepository`
- Crear scripts o inicializacion de datos semilla
- Adaptar `AppConfig` para elegir entre memoria y `SQLite`

Entregables:
- CRUD funcional sobre `SQLite`
- datos persistentes entre ejecuciones
- carga inicial controlada

## Fase 3 - Catalogo publico
Objetivo: implementar el flujo principal para usuarios invitados.

- Mostrar catalogo desde el inicio
- Listar libros paginados
- Mostrar portada, titulo, autor y categoria
- Abrir detalle de libro
- Mostrar copias, estado y ubicacion fisica
- Mostrar mensaje claro cuando no haya resultados

Entregables:
- pantalla principal funcional
- pantalla de detalle funcional
- navegacion basica estable

## Fase 4 - Busqueda y filtros
Objetivo: hacer util la consulta del catalogo sin volverla obligatoria.

- Buscar por titulo
- Buscar por autor
- Ignorar mayusculas y minusculas
- Ignorar acentos
- Permitir coincidencia parcial
- Agregar filtros por categoria, carrera y disponibilidad
- Ordenar por titulo, autor, anio o categoria

Entregables:
- busqueda flexible operativa
- filtros combinables
- paginacion consistente con filtros

## Fase 5 - Seguridad y sesion
Objetivo: separar experiencia `GUEST` y `ADMIN` sin dividir la aplicacion.

- Implementar login de administrador
- Mantener acceso como invitado
- Mostrar rol activo en interfaz
- Restringir acciones administrativas
- Permitir cierre de sesion y cambio de modo

Entregables:
- sesiones funcionales
- autorizacion minima aplicada
- acciones protegidas por rol

## Fase 6 - Administracion del catalogo
Objetivo: habilitar operacion real del inventario.

- Alta de `BookTitle`
- Alta de `BookCopy`
- Edicion de datos bibliograficos
- Edicion de datos del ejemplar
- Cambio de ubicacion
- Cambio de estado `AVAILABLE`, `MISSING`, `REMOVED`, `REPAIR`
- Evitar borrado fisico como flujo principal

Entregables:
- formularios admin funcionales
- cambios persistentes en base de datos
- reglas basicas de validacion

## Fase 7 - Validaciones y calidad de datos
Objetivo: evitar inconsistencias en catalogo e inventario.

- Validar campos obligatorios
- Evitar `inventoryCode` duplicado
- Validar estructura de ubicacion
- Revisar ISBN si aplica
- Evitar estados invalidos
- Definir tratamiento de ejemplares removidos en catalogo publico

Entregables:
- formularios con validacion
- reglas de integridad consistentes
- mensajes de error claros

## Fase 8 - Pruebas
Objetivo: asegurar que los flujos principales no se rompan.

- Pruebas unitarias para servicios
- Pruebas de repositorios `SQLite`
- Pruebas de busqueda
- Pruebas de autorizacion
- Pruebas de integracion para alta, consulta y cambio de estado

Entregables:
- suite minima automatizada
- casos criticos cubiertos

## Fase 9 - Preparacion para entrega o despliegue
Objetivo: dejar el proyecto presentable y reproducible.

- Documentar como ejecutar
- Documentar credenciales de prueba
- Documentar estructura de base de datos
- Empaquetar build ejecutable
- Limpiar datos de prueba si no corresponden a la entrega

Entregables:
- `README` tecnico
- build reproducible
- proyecto listo para demo

## Orden de implementacion recomendado
1. Persistencia `SQLite`
2. Catalogo publico
3. Busqueda y filtros
4. Login y autorizacion
5. Administracion del catalogo
6. Validaciones
7. Pruebas

## Riesgos principales
- Intentar construir administracion completa antes de cerrar el flujo de consulta
- Diseñar la UI sin datos reales o sin multiples ejemplares por titulo
- Duplicar logica entre controller, service y repository
- Mezclar demasiado pronto repositorios en memoria con repositorios `SQLite`
- No definir desde el inicio que estados se muestran al usuario invitado

## Criterios de exito del MVP
- Un invitado puede entrar y ver libros sin buscar
- Un usuario puede encontrar libros por titulo o autor con texto parcial
- El detalle muestra al menos una ubicacion fisica clara
- Un admin puede registrar y actualizar informacion basica
- Los cambios persisten en `SQLite`
