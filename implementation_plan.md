# Implementación del Sistema de Filtros

Estado: implementada.

## Resumen
Se reemplazó el sistema de sugerencias (panel izquierdo) por un sistema de filtros funcionales: alfabeto A-Z, ordenamiento y filtro por carrera. Se eliminó el campo redundante de búsqueda por autor y las clases/servicios de sugerencias.

## Cambios Realizados

### Backend

**`BookSearchCriteria.java`**
- `career` (String) → `careers` (Set<String>) para multi-selección
- Se agregó `firstLetters` (Set<String>) para filtro alfabético
- Se eliminaron `author`, `category`, `availableOnly`

**`BookTitleRepository.java`** (interfaz)
- Se eliminó `suggest(String, boolean)`
- Se agregó `List<String> findAllCareers()`

**`SQLiteBookTitleRepository.java`**
- Se eliminó `suggest()` y métodos relacionados (`mapSuggestion`, `matchesSuggestion`, `resolveMatchContext`, `normalizedSql`)
- Se implementó `findAllCareers()` con `SELECT DISTINCT career`
- `matches()` ahora usa `matchesText()`, `matchesCareers()`, `matchesFirstLetters()`

**`InMemoryBookTitleRepository.java`**
- Mismos cambios que SQLiteBookTitleRepository

**`CatalogService.java`**
- Se eliminó dependencia de `SearchService`
- Se eliminó `suggest()`
- Se agregó `getCareers()`

**`CatalogController.java`**
- Se eliminó `suggest()`
- Se agregó `getCareers()`

**`SearchSuggestion.java`** — Eliminado
**`SearchService.java`** — Eliminado

**`AppConfig.java`**
- Se eliminó `SearchService` del wiring

### Frontend (CatalogView.java)

**Eliminado:**
- `authorSearchField` (TextField) — redundante
- Sistema completo de sugerencias: `suggestionDebounce`, `suggestionsPanel`, `suggestionsList`, `suggestionsStatusLabel`, `suggestionButtons`, `applyingSuggestion`, `selectedSuggestionIndex`
- Listeners de teclado UP/DOWN/ENTER para navegación de sugerencias
- ~150 líneas de lógica de sugerencias

**Agregado — Panel de Filtros (reemplaza sugerencias):**
- **Ordenar por**: RadioButtons A→Z y Z→A (ToggleGroup)
- **Primera letra**: FlowPane con 26 botones (A-Z) + "TODAS", multi-select
- **Carrera**: CheckBoxes poblados desde `CatalogController.getCareers()`
- Todos los filtros activan `loadCatalog()` al cambiar

### Pruebas
- 34 tests, 0 fallos, 0 errores
- Tests de sugerencia reemplazados por tests de `findAllCareers()`
- Tests actualizados a la nueva API de `BookSearchCriteria` y `CatalogService`

---

# Abreviación de Carreras en Panel de Filtros y Limpieza de Datos Semilla

Estado: implementada.

## Resumen
Se reemplazan los datos semilla existentes por 14 libros nuevos (2 por cada una de las 7 carreras) y se agregan abreviaciones para mostrar en los checkboxes del panel de filtros de `CatalogView`.

## Cambios Realizados

### `CatalogView.java`
- Agregar mapa `CAREER_ABBREVIATIONS` con las 7 abreviaciones:
  - `LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES` → `ADMINISTRACIÓN`
  - `LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL` → `INGENIERÍA`
  - `LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA` → `MERCADOTECNIA`
  - `LICENCIATURA EN DISEÑO GRÁFICO` → `DISEÑO`
  - `LICENCIATURA EN LENGUAS EXTRANJERAS` → `LENGUAS`
  - `LICENCIATURA EN SISTEMAS COMPUTACIONALES` → `SISTEMAS`
  - `LICENCIATURA EN COMUNICACIÓN` → `COMUNICACIÓN`
- Modificar `loadCareers()` para mostrar la abreviatura en el label del `CheckBox`
- Las claves internas (`careerCheckboxes`) siguen usando el nombre completo para no romper el filtrado

### `AppConfig.java`
- Eliminar los 6 libros, 5 ubicaciones y 9 ejemplares semilla actuales
- Agregar 7 ubicaciones nuevas (una por carrera):
  - `adminShelf` → `SG-ADM-A1-1-01`
  - `industrialShelf` → `SG-IND-B1-1-01`
  - `marketingShelf` → `SG-MKT-C1-1-01`
  - `designShelf` → `SG-DIS-D1-1-01`
  - `languagesShelf` → `SG-LEN-E1-1-01`
  - `systemsShelf` → `SG-SIS-F1-1-01`
  - `communicationShelf` → `SG-COM-G1-1-01`
- Agregar 14 libros (2 por carrera) con título, autor, ISBN, editorial, año, categoría y carrera
- Agregar 14 ejemplares (1 por libro) con estados variados (11 AVAILABLE, 1 MISSING, 2 REMOVED)

### Pruebas
- `mvn test` debe seguir pasando (34 tests, 0 fallos).

---

# Eliminación del Login Inicial y Autenticación desde el Catálogo

Estado: implementada.

## Resumen
Se elimina la pantalla de login inicial. La aplicación abre directamente el catálogo en modo invitado. Se agregan botones "Iniciar sesión" y "Cerrar sesión" en la barra superior del catálogo.

## Cambios Realizados

### `MainApp.java`
- Crear sesión GUEST directamente (`authenticationService.loginAsGuest()`)
- Abrir `CatalogView` con sesión GUEST y pasarle `LoginController`
- Eliminar creación de `LoginView` como pantalla inicial

### `CatalogView.java`
- Hacer `session` mutable (quitar `final`)
- Agregar botón **"Iniciar sesión"** en `buildTopBar()` — visible solo en modo GUEST
- Agregar botón **"Cerrar sesión"** en `buildTopBar()` — visible solo en modo ADMIN
- Agregar referencia a `LoginController` para abrir diálogo de login
- Crear `updateSession(UserSession)` que:
  - Reemplaza `this.session`
  - Actualiza visibilidad de botones admin (`registerButton`, `editButton`, `adminPanel`)
  - Actualiza etiqueta "Rol activo: ..."
  - Recarga catálogo (reglas de visibilidad cambian según rol)

### `LoginView.java`
- Convertir a diálogo modal que retorna `UserSession`
- Eliminar dependencia de `CatalogController` y `BookAdminController`
- Método `showAndWait(Stage owner)` → devuelve `UserSession` o `null`
- El botón "Entrar como invitado" ya no es necesario (se entra directo desde el inicio)

### Archivos NO modificados
- `LoginController`, `AuthenticationService`, `PasswordService`
- Reglas de visibilidad, repositorios, servicios

### Pruebas
- `mvn test` debe seguir pasando sin cambios en la lógica de negocio.

---

# Rediseño UI Completo — Estilo Netflix (Azul)

Estado: implementada.

## Resumen
Rediseño completo de las 3 vistas (CatalogView, LoginView, BookAdminView) con paleta azul dominante, animaciones, hover effects, layout responsivo y consistencia visual tipo Netflix.

## Discovery
**Contexto:** App de catálogo de biblioteca. Usuarios invitados (navegan/buscan) y administradores (CRUD libros).

**Problemas actuales:**
- Estilos inline repetidos (~50 declaraciones spagetti)
- Sin animaciones, hover effects ni transiciones
- Tarjetas estáticas sin interactividad
- Paleta de azules inconsistente (6+ tonos diferentes)
- Layout rígido con pixeles fijos

## Flujos y Estados

| Flujo | Pantallas | Estados |
|---|---|---|
| Invitado entra | CatalogView | Loading → Grid tarjetas → Empty (0 resultados) |
| Invitado busca/filtra | CatalogView | Resultados filtrados → Empty state |
| Invitado selecciona libro | CatalogView → Detalle | Card selected (glow azul) → Detail cargado |
| Invitado → Admin (login) | LoginView modal | Form → Success → Error credenciales |
| Admin registra libro | BookAdminView modal | Form → Success → Error validación |
| Admin edita libro | BookAdminView modal | Form pre-poblado → Success → Error |
| Admin cambia estado | CatalogView detail | Confirmación → Actualizado |

## Sistema de Tokens (Design Tokens)

### Colores (Blue-first palette)

| Token | Hex | Uso |
|---|---|---|
| `bg-primary` | `#0a1929` | Fondo principal |
| `bg-secondary` | `#0f2740` | Paneles, secciones |
| `bg-card` | `#112240` | Fondo tarjetas |
| `bg-elevated` | `#1a365d` | Hover, active states |
| `blue-primary` | `#1d4ed8` | Botones, acentos principales |
| `blue-hover` | `#2563eb` | Hover elementos azules |
| `blue-glow` | `#3b82f6` | Glows, borders, selection |
| `blue-light` | `#60a5fa` | Textos secundarios |
| `blue-subtle` | `#93c5fd` | Textos terciarios |
| `blue-bg-subtle` | `rgba(29,78,216,0.15)` | Fondos de secciones |
| `text-primary` | `#ffffff` | Texto principal |
| `text-secondary` | `#cbd5e1` | Texto secundario |
| `text-muted` | `#94a3b8` | Metadata |
| `accent-warning` | `#fbbf24` | Disponibles, badges |
| `accent-success` | `#22c55e` | Disponible (status) |
| `accent-error` | `#ef4444` | Extraviado, errores |
| `accent-removed` | `#6b7280` | Retirado |

### Tipografía
- Títulos: `"Segoe UI", system-ui, sans-serif`, bold
- Cuerpo: system-ui, regular
- Escala: 10 / 12 / 13 / 14 / 15 / 18 / 20 / 24 / 28 px

### Espaciado
- Gap base: 8px
- Padding paneles: 16px
- Padding contenido: 12px
- Section spacing: 12px

### Border Radius
- Tarjetas: 12px
- Paneles: 8px
- Botones: 6px
- Badges: 999px (pill)

### Animaciones
- Hover card: scale 1.08×, 200ms ease-out
- Selección: borde glow, 150ms
- Transiciones panel: 250ms
- Focus input: borde azul, 150ms

## Componentes y Cambios por Archivo

### `CatalogView.java` — Rediseño completo

**Top Bar (Netflix-style header)**
- Logo "Indice Digital de Biblioteca" a la izquierda (bold, white, 24px)
- Search bar centrada: fondo semitransparente, borde azul al focus, icono lupa
- Botones acción derecha: [Iniciar sesión | Cerrar sesión] [Registrar] [Editar]
- Badge rol "Admin" o "Invitado" pequeño
- Contador resultados integrado

**Filter Panel (left sidebar)**
- Fondo `#0a1929` sólido, borde derecho sutil
- Secciones con header y chevron plegable
- Ordenar: toggle pill-shaped A-Z / Z-A
- Alfabeto: botones más grandes, hover con glow azul
- Carreras: checkboxes como toggle-switch
- Fixed width 200px

**Catalog Grid**
- Tarjetas 150×230, border-radius 12, sombra sutil
- Hover: escala 1.08×, glow azul `#3b82f6`, shadow azulado
- Selección: borde azul brillante `#3b82f6` 3px + glow
- Overlay inferior compacto (título, autor, disponibles)
- Gap 12px entre tarjetas

**Detail Panel (right sidebar)**
- Cover 260×360 con sombra tipo Netflix
- Info en cards separadas con fondo azul sutil
- Descripción con fondo `blue-bg-subtle`
- Código ubicación tipo badge/pill azul
- Admin panel integrado sin Separator
- Fixed width 340px

### `LoginView.java` — Rediseño completo
- Fondo azul oscuro degradado
- Card blanca centrada con formulario
- Inputs con borde azul al focus
- Botón "Entrar" azul sólido con hover más claro
- Error inline en vez de Alert popup

### `BookAdminView.java` — Rediseño
- Fondo azul oscuro con secciones en cards
- Inputs con estilo consistente
- ComboBox estilizados
- Botón guardar azul prominente

## Archivos a Modificar

| Archivo | Cambios |
|---|---|
| `CatalogView.java` | Rediseñar topBar, filterPanel, catalogGrid, createCatalogCard, detailPane. Nueva paleta azul, hover effects, layout responsivo |
| `LoginView.java` | Rediseño completo: card blanca, inputs estilizados, error inline |
| `BookAdminView.java` | Rediseño: cards, inputs, botones azules |

## Orden de Implementación

1. Crear clase `DesignTokens.java` con constantes de color, radio, spacing
2. `CatalogView.java` — topBar + filterPanel (estructura externa)
3. `CatalogView.java` — catalogGrid + createCatalogCard (corazón visual)
4. `CatalogView.java` — detailPane
5. `LoginView.java` — rediseño completo
6. `BookAdminView.java` — rediseño
7. Ajustes finales y testing (`mvn test`)
