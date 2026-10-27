#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

OUTPUT_DIR="${1:-$PROJECT_ROOT/release-artifacts}"
PROPS_FILE="$PROJECT_ROOT/gradle.properties"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Error: gradle.properties not found at $PROPS_FILE" >&2
  exit 1
fi

PROPS_BACKUP="$(mktemp)"
cp "$PROPS_FILE" "$PROPS_BACKUP"

cleanup() {
  local exit_code=$?
  if [ -f "$PROPS_BACKUP" ]; then
    cp "$PROPS_BACKUP" "$PROPS_FILE"
    rm -f "$PROPS_BACKUP"
  fi
  exit "$exit_code"
}
trap cleanup EXIT INT TERM

mkdir -p "$OUTPUT_DIR"

VERSIONS=("1.21.9" "1.21.10" "1.21.11")
MOD_VERSION="$(grep '^mod_version=' "$PROPS_FILE" | cut -d'=' -f2 | tr -d '[:space:]')"

echo "=== Building Jump Delay Fix for all target versions: ${VERSIONS[*]} ==="
echo "Destination: $OUTPUT_DIR"
echo

declare -a CREATED_ARTIFACTS=()

for version in "${VERSIONS[@]}"; do
  echo "--- Building Minecraft $version ---"
  "$PROJECT_ROOT/scripts/set-minecraft-version.sh" --base-minecraft "$version" --supported-versions "$version" >/dev/null
  ./gradlew clean buildAll publishReadyCheck

  fabric_jar="fabric/build/libs/jumpdelayfix-fabric-${version}-${MOD_VERSION}.jar"
  fabric_sources="fabric/build/libs/jumpdelayfix-fabric-${version}-${MOD_VERSION}-sources.jar"
  neoforge_jar="neoforge/build/libs/jumpdelayfix-neoforge-${version}-${MOD_VERSION}.jar"
  neoforge_sources="neoforge/build/libs/jumpdelayfix-neoforge-${version}-${MOD_VERSION}-sources.jar"

  if [ ! -f "$fabric_jar" ]; then
    echo "Error: Expected Fabric jar not found: $fabric_jar" >&2
    exit 1
  fi
  if [ ! -f "$neoforge_jar" ]; then
    echo "Error: Expected NeoForge jar not found: $neoforge_jar" >&2
    exit 1
  fi

  cp "$fabric_jar" "$OUTPUT_DIR/"
  CREATED_ARTIFACTS+=("$(basename "$fabric_jar")")
  [ -f "$fabric_sources" ] && cp "$fabric_sources" "$OUTPUT_DIR/" && CREATED_ARTIFACTS+=("$(basename "$fabric_sources")")

  cp "$neoforge_jar" "$OUTPUT_DIR/"
  CREATED_ARTIFACTS+=("$(basename "$neoforge_jar")")
  [ -f "$neoforge_sources" ] && cp "$neoforge_sources" "$OUTPUT_DIR/" && CREATED_ARTIFACTS+=("$(basename "$neoforge_sources")")

  echo "Completed Minecraft $version build successfully."
  echo
done

echo "=== Generated Artifacts Report (${#CREATED_ARTIFACTS[@]} files) ==="
for art in "${CREATED_ARTIFACTS[@]}"; do
  art_path="$OUTPUT_DIR/$art"
  size_bytes=$(stat -c%s "$art_path" 2>/dev/null || stat -f%z "$art_path")
  sha=$(sha256sum "$art_path" | cut -d' ' -f1)
  echo "  - $art (${size_bytes} bytes, SHA-256: ${sha:0:16}...)"
done

(
  cd "$OUTPUT_DIR"
  sha256sum "${CREATED_ARTIFACTS[@]}" > SHA256SUMS.txt
)
echo "Wrote checksums to $OUTPUT_DIR/SHA256SUMS.txt"
echo
echo "All ${#VERSIONS[@]} versions built and verified successfully."
