#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SOURCE_DIRS=(
    "common/src/main/java"
    "common/src/test/java"
    "fabric/src/main/java"
    "fabric/src/test/java"
    "neoforge/src/main/java"
    "neoforge/src/test/java"
)

EXISTING_SOURCE_DIRS=()
for source_dir in "${SOURCE_DIRS[@]}"; do
    if [[ -d "$source_dir" ]]; then
        EXISTING_SOURCE_DIRS+=("$source_dir")
    fi
done

if (( ${#EXISTING_SOURCE_DIRS[@]} == 0 )); then
    echo "No Java source directories found for hygiene validation."
    exit 1
fi

SOURCE_FILES=$(find "${EXISTING_SOURCE_DIRS[@]}" -type f -name '*.java' | sort || true)
if [[ -z "$SOURCE_FILES" ]]; then
    echo "No Java source files found for hygiene validation."
    exit 1
fi

COMMENT_MATCHES=$(rg -n --glob '*.java' '(//|/\*)' "${EXISTING_SOURCE_DIRS[@]}" || true)
if [[ -n "$COMMENT_MATCHES" ]]; then
    echo "Java source hygiene violation: comments are not allowed in source files."
    echo "$COMMENT_MATCHES"
    exit 1
fi

MARKER_MATCHES=$(rg -n --glob '*.java' '\b(TODO|FIXME|XXX)\b' "${EXISTING_SOURCE_DIRS[@]}" || true)
if [[ -n "$MARKER_MATCHES" ]]; then
    echo "Java source hygiene violation: TODO/FIXME/XXX markers are not allowed."
    echo "$MARKER_MATCHES"
    exit 1
fi

WILDCARD_IMPORT_MATCHES=$(rg -n --glob '*.java' '^import\s+[^;]*\.\*;' "${EXISTING_SOURCE_DIRS[@]}" || true)
if [[ -n "$WILDCARD_IMPORT_MATCHES" ]]; then
    echo "Java source hygiene violation: wildcard imports are not allowed."
    echo "$WILDCARD_IMPORT_MATCHES"
    exit 1
fi

echo "Source hygiene validation passed."
