#!/bin/bash
set -e

APP_NAME="Indice-Digital-Biblioteca"
APP_VERSION="1.0.0"
MAIN_JAR="biblioteca-1.0-SNAPSHOT.jar"
MAIN_CLASS="com.biblioteca.app.MainApp"
ICON_FILE="app.png"

echo "=== Compilando proyecto ==="
mvn clean package -DskipTests

echo "=== Creando runtime image con jlink ==="
rm -rf target/runtime
jlink --module-path target/biblioteca-1.0-SNAPSHOT.jar:target/dependency:$JAVA_HOME/jmods \
      --add-modules com.biblioteca \
      --output target/runtime \
      --strip-debug --compress zip-6 --no-header-files --no-man-pages

echo "=== Copiando JAR al directorio de input ==="
cp "target/$MAIN_JAR" target/dependency/

PACKAGE_TYPE="${1:-deb}"
echo "=== Empaquetando como $PACKAGE_TYPE ==="

JPACKAGE_OPTS=(
    --input target/dependency
    --main-jar "$MAIN_JAR"
    --main-class "$MAIN_CLASS"
    --type "$PACKAGE_TYPE"
    --name "$APP_NAME"
    --app-version "$APP_VERSION"
    --vendor "Biblioteca"
    --runtime-image target/runtime
    --java-options "-Xmx512m"
)

if [[ "$PACKAGE_TYPE" == "deb" || "$PACKAGE_TYPE" == "rpm" ]]; then
    JPACKAGE_OPTS+=(--linux-deb-maintainer "demo@biblioteca.local")
    if [[ -f "$ICON_FILE" ]]; then
        JPACKAGE_OPTS+=(--icon "$ICON_FILE")
    fi
elif [[ "$PACKAGE_TYPE" == "exe" || "$PACKAGE_TYPE" == "msi" ]]; then
    if [[ -f "app.ico" ]]; then
        JPACKAGE_OPTS+=(--icon "app.ico")
    fi
    JPACKAGE_OPTS+=(--win-dir-chooser --win-menu)
fi

jpackage "${JPACKAGE_OPTS[@]}"

echo "=== Listo ==="
PKG_PREFIX=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]')
ls -lh "${PKG_PREFIX}"*.deb "${PKG_PREFIX}"*.rpm "${APP_NAME}"*.exe "${APP_NAME}"*.msi 2>/dev/null || echo "(Busca el instalador en el directorio actual)"
