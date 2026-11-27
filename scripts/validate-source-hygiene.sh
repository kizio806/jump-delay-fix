#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SOURCE_DIRS=(
    "common/src/main/java"
    "common/src/client/java"
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

search_java() {
    local pattern="$1"
    shift
    if command -v rg >/dev/null 2>&1; then
        rg -n --glob '*.java' "$pattern" "$@" 2>/dev/null || true
    else
        grep -rnE --include='*.java' "$pattern" "$@" 2>/dev/null || true
    fi
}

MARKER_MATCHES=$(search_java '\b(TODO|FIXME|XXX)\b' "${EXISTING_SOURCE_DIRS[@]}")
if [[ -n "$MARKER_MATCHES" ]]; then
    echo "Java source hygiene violation: TODO/FIXME/XXX markers are not allowed."
    echo "$MARKER_MATCHES"
    exit 1
fi

WILDCARD_IMPORT_MATCHES=$(search_java '^import\s+[^;]*\.\*;' "${EXISTING_SOURCE_DIRS[@]}")
if [[ -n "$WILDCARD_IMPORT_MATCHES" ]]; then
    echo "Java source hygiene violation: wildcard imports are not allowed."
    echo "$WILDCARD_IMPORT_MATCHES"
    exit 1
fi

SUPPRESS_WARNINGS_MATCHES=$(search_java '@SuppressWarnings' "${EXISTING_SOURCE_DIRS[@]}")
if [[ -n "$SUPPRESS_WARNINGS_MATCHES" ]]; then
    echo "Java source hygiene violation: @SuppressWarnings is strictly forbidden across the codebase. Fix the root cause instead of suppressing warnings."
    echo "$SUPPRESS_WARNINGS_MATCHES"
    exit 1
fi

echo "Source hygiene validation passed."
