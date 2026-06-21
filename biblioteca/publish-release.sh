#!/bin/bash
set -e

# publish-release.sh — Crea un GitHub Release y sube los assets empaquetados
# Uso: ./publish-release.sh TOKEN_GITHUB [ruta-al-paquete1] [ruta-al-paquete2] ...
#
# Ejemplo:
#   ./publish-release.sh ghp_xxxx ../package.sh exe   # construye y publica
#   ./publish-release.sh ghp_xxxx ./Indice-Digital-Biblioteca-1.1.0.exe   # solo publica

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROP_FILE="$SCRIPT_DIR/src/main/resources/version.properties"

APP_NAME=$(grep '^app.name=' "$PROP_FILE" | cut -d= -f2 | tr -d '[:space:]')
APP_VERSION=$(grep '^app.version=' "$PROP_FILE" | cut -d= -f2 | tr -d '[:space:]')
REPO="ivan51103/libreria_indice"
TOKEN="$1"
shift || true

if [[ -z "$TOKEN" ]]; then
    echo "Error: Debes proporcionar un token de GitHub."
    echo "Uso: $0 TOKEN [archivos...]"
    exit 1
fi

TAG="v$APP_VERSION"
echo "=== Publicando $APP_NAME $TAG ==="

# 1. Crear el Release en GitHub
echo "Creando release $TAG..."
RELEASE_RESPONSE=$(curl -s -X POST "https://api.github.com/repos/$REPO/releases" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"tag_name\": \"$TAG\",
        \"name\": \"$APP_NAME $TAG\",
        \"body\": \"Lanzamiento $APP_NAME version $APP_VERSION\",
        \"draft\": false,
        \"prerelease\": false
    }")

RELEASE_ID=$(echo "$RELEASE_RESPONSE" | grep -o '"id": [0-9]*' | head -1 | cut -d' ' -f2)
if [[ -z "$RELEASE_ID" ]]; then
    echo "Error al crear release. Respuesta:"
    echo "$RELEASE_RESPONSE"
    exit 1
fi
echo "Release creado con ID: $RELEASE_ID"

# 2. Subir cada asset
for FILE in "$@"; do
    if [[ ! -f "$FILE" ]]; then
        echo "Advertencia: el archivo '$FILE' no existe, se omite."
        continue
    fi
    BASENAME=$(basename "$FILE")
    echo "Subiendo $BASENAME..."
    UPLOAD_URL="https://uploads.github.com/repos/$REPO/releases/$RELEASE_ID/assets?name=$BASENAME"
    curl -s -X POST "$UPLOAD_URL" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/octet-stream" \
        --data-binary @"$FILE" > /dev/null
    echo "  $BASENAME subido."
done

echo ""
echo "=== Publicacion completada ==="
echo "Release: https://github.com/$REPO/releases/tag/$TAG"
