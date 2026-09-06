#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

EMPTY_DIRS=$(find common/src/main fabric/src/main neoforge/src/main -type d -empty | sort || true)

if [[ -n "$EMPTY_DIRS" ]]; then
    echo "Found empty source directories:"
    echo "$EMPTY_DIRS"
    exit 1
fi

COMMON_PLATFORM_IMPORTS=$(rg -n 'import (net\.fabricmc|net\.neoforged)\.' common/src/main/java || true)

if [[ -n "$COMMON_PLATFORM_IMPORTS" ]]; then
    echo "Found loader-specific imports in common:"
    echo "$COMMON_PLATFORM_IMPORTS"
    exit 1
fi

FORBIDDEN_ACCESSORS=$(rg -n '@Accessor|@Invoker' common/src/main/java fabric/src/main/java neoforge/src/main/java || true)

if [[ -n "$FORBIDDEN_ACCESSORS" ]]; then
    echo "Found forbidden accessor/invoker mixins:"
    echo "$FORBIDDEN_ACCESSORS"
    exit 1
fi

required_docs=(
    "CHANGELOG.md"
    "CONTRIBUTING.md"
    "SECURITY.md"
    "docs/architecture.md"
    "docs/development.md"
    "docs/installation.md"
    "docs/modrinth.md"
    "docs/releases.md"
    "docs/troubleshooting.md"
)

missing_docs=()
for doc_file in "${required_docs[@]}"; do
    if [[ ! -f "$doc_file" ]]; then
        missing_docs+=("$doc_file")
    fi
done

if (( ${#missing_docs[@]} > 0 )); then
    echo "Missing required documentation files:"
    printf '%s\n' "${missing_docs[@]}"
    exit 1
fi

required_tests=(
    "common/src/test/java/com/kizio/jumpdelayfix/config/RuntimeConfigTest.java"
    "common/src/test/java/com/kizio/jumpdelayfix/jump/JumpHandlerIntegrationTest.java"
    "fabric/src/test/java/com/kizio/jumpdelayfix/fabric/FabricMetadataContractTest.java"
    "neoforge/src/test/java/com/kizio/jumpdelayfix/neoforge/NeoForgeMetadataContractTest.java"
)

missing_tests=()

for test_file in "${required_tests[@]}"; do
    if [[ ! -f "$test_file" ]]; then
        missing_tests+=("$test_file")
    fi
done

if (( ${#missing_tests[@]} > 0 )); then
    echo "Missing required tests:"
    printf '%s\n' "${missing_tests[@]}"
    exit 1
fi

echo "Structure validation passed."
