# Implementation Plan: Demo Deployment (.exe / .deb)

## Estado Actual
- JDK 21 con `jlink` y `jpackage` disponibles
- JavaFX 21 (modular), SQLite JDBC, Maven
- Covers en `biblioteca/covers/` (33 archivos, paths en DB con prefijo `covers/`)
- DB se crea en `~/.biblioteca/biblioteca.db`
- NO hay `module-info.java` (no modular)
- NO hay recursos empaquetados en el JAR (covers se leen como `new File()`)

## Paso 1 — Mover covers a resources
```bash
mv biblioteca/covers/ biblioteca/src/main/resources/covers/
```
Para que Maven empaquete las 33 portadas dentro del JAR como recursos del classpath.

## Paso 2 — Crear `CoverResolver.java`
Clase utilitaria en `com.biblioteca.util` que centraliza la carga de cubiertas:
- Busca primero en `{user.home}/.biblioteca/covers/{filename}` (subidas por usuario)
- Si no existe, extrae desde el classpath `getResource("/covers/{filename}")` al data dir
- Retorna el `Path` absoluto
- Se llama desde `CatalogView.applyCoverBackground()` y `applyDetailCover()`

## Paso 3 — Extraer cubiertas seed al primer arranque
En `AppConfig`, después de `seedSampleData()`, agregar método `extractSeedCovers()` que:
- Toma todas las cubiertas de `getResource("/covers/")`
- Las copia a `~/.biblioteca/covers/` si no existen ya
- Asegura que `new File("covers/xxx.jpg")` funcione incluso en empaquetado

## Paso 4 — Actualizar `CatalogView.java`
Cambiar `applyCoverBackground()` y `applyDetailCover()` para resolver la ruta:
- Si `coverPath` es absoluta (`/home/...` o `C:\...`), usarla directo
- Si `coverPath` empieza con `covers/`, resolver contra `~/.biblioteca/covers/`
- Si el archivo no existe, fallback gradient (ya implementado)
- Usar `CoverResolver` para la resolución

## Paso 5 — Crear `module-info.java`
```
module com.biblioteca {
    requires javafx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    exports com.biblioteca.app;
    exports com.biblioteca.ui.view;
    opens com.biblioteca.config to java.sql;
}
```

## Paso 6 — Crear script de build `package.sh`
Script que ejecuta `mvn package`, `jlink` (runtime image recortada) y `jpackage`:
```bash
#!/bin/bash
set -e
mvn clean package -DskipTests
JLINK_MODULES="java.base,java.sql,javafx.controls,javafx.graphics,javafx.base"
jlink --module-path target/dependency:$JAVA_HOME/jmods \
      --add-modules $JLINK_MODULES \
      --output target/runtime \
      --strip-debug --compress=2 --no-header-files --no-man-pages
jpackage --input target/dependency \
         --main-jar biblioteca-1.0-SNAPSHOT.jar \
         --main-class com.biblioteca.app.MainApp \
         --type deb \
         --name "Indice-Digital-Biblioteca" \
         --app-version 1.0.0 \
         --vendor "Biblioteca" \
         --runtime-image target/runtime \
         --java-options "-Xmx512m"
```

## Paso 7 — Crear `app.ico` genérico
Icono placeholder 256×256: libro blanco sobre fondo azul oscuro.

## Paso 8 — Probar build
Ejecutar `package.sh` para verificar que el pipeline funciona (formato `.deb` en Linux).

## Paso 9 — CI / GitHub Actions (`.github/workflows/build.yml`)
Workflow para build cross-platform:
- Windows: genera `.exe` (requiere WiX Toolset)
- Linux: genera `.deb`
- Upload artifacts

## Dependencias externas
| Componente | Windows | Notas |
|---|---|---|
| JDK 21 | `actions/setup-java` | Incluye jlink + jpackage |
| WiX Toolset v3 | `choco install wixtoolset` | Necesario para `--type exe` |
| Inno Setup (alt) | `choco install innosetup` | Alternativa a WiX |

## Entregables
1. Instalador `.exe` (~40-70 MB): JRE recortado + app + covers extraídos al primer arranque
2. Instalador `.deb` (Linux): mismo contenido, formato `.deb`
3. ZIP portable: alternativa manual
