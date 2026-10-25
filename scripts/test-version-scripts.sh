#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SET_SCRIPT="$ROOT_DIR/scripts/set-minecraft-version.sh"
PROBE_SCRIPT="$ROOT_DIR/scripts/probe-minecraft-versions.sh"

TMP_PROPS="$(mktemp)"
trap 'rm -f "$TMP_PROPS"' EXIT

reset_temp_props() {
  cat <<'PROPS' > "$TMP_PROPS"
minecraft_version=26.3
fabric_minecraft_version_range=>=26.3 <26.4
fabric_game_versions=26.3
neoforge_game_versions=26.3
modrinth_game_versions=26.3
minecraft_version_range=[26.3,26.4)
mod_version=1.4.1
PROPS
}

assert_contains() {
  local file="$1"
  local pattern="$2"
  if ! grep -q "^${pattern}$" "$file"; then
    echo "Assertion failed: '$pattern' not found in $file"
    cat "$file"
    exit 1
  fi
}

echo "=== Testing Minecraft Version Scripts ==="

# Test 1: Valid 2-part version (Minecraft 26.3)
reset_temp_props
PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" \
  --base-minecraft 26.3 \
  --supported-versions 26.3 >/dev/null

assert_contains "$TMP_PROPS" "minecraft_version=26.3"
assert_contains "$TMP_PROPS" "fabric_game_versions=26.3"
assert_contains "$TMP_PROPS" "neoforge_game_versions=26.3"
assert_contains "$TMP_PROPS" "modrinth_game_versions=26.3"
assert_contains "$TMP_PROPS" "fabric_minecraft_version_range=>=26.3 <26.4"
assert_contains "$TMP_PROPS" "minecraft_version_range=\[26.3,26.4)"
echo "PASS: 2-part version 26.3 basic setup"

# Test 2: Valid 2-part + 3-part multi-version sorting on one patch line
reset_temp_props
PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" \
  --base-minecraft 26.3 \
  --supported-versions "26.3.2, 26.3, 26.3.1, 26.3" >/dev/null

assert_contains "$TMP_PROPS" "minecraft_version=26.3"
assert_contains "$TMP_PROPS" "fabric_game_versions=26.3,26.3.1,26.3.2"
assert_contains "$TMP_PROPS" "neoforge_game_versions=26.3,26.3.1,26.3.2"
assert_contains "$TMP_PROPS" "modrinth_game_versions=26.3,26.3.1,26.3.2"
assert_contains "$TMP_PROPS" "fabric_minecraft_version_range=>=26.3 <26.4"
assert_contains "$TMP_PROPS" "minecraft_version_range=\[26.3,26.4)"
echo "PASS: Sorting and deduplication of 26.3, 26.3.1, 26.3.2"

# Test 3: Legacy 3-part version handling (e.g. 26.1.2)
reset_temp_props
PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" \
  --base-minecraft 26.1.2 \
  --supported-versions "26.1.2, 26.1, 26.1.1" >/dev/null

assert_contains "$TMP_PROPS" "minecraft_version=26.1.2"
assert_contains "$TMP_PROPS" "fabric_game_versions=26.1,26.1.1,26.1.2"
assert_contains "$TMP_PROPS" "fabric_minecraft_version_range=>=26.1 <26.2"
assert_contains "$TMP_PROPS" "minecraft_version_range=\[26.1,26.2)"
echo "PASS: Legacy 3-part version 26.1.2"

# Test 4: Custom fabric and minecraft range overrides
reset_temp_props
PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" \
  --base-minecraft 26.3 \
  --supported-versions 26.3 \
  --fabric-range ">=26.3 <=26.3.1" \
  --minecraft-range "[26.3,26.3.1]" >/dev/null

assert_contains "$TMP_PROPS" "fabric_minecraft_version_range=>=26.3 <=26.3.1"
assert_contains "$TMP_PROPS" "minecraft_version_range=\[26.3,26.3.1]"
echo "PASS: Custom range overrides"

# Test 5: Rejection of invalid version formats
for bad_version in "invalid" "26" "26.3.1.2" "26." ".26" "26.3.a"; do
  if PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" --base-minecraft "$bad_version" --supported-versions "$bad_version" >/dev/null 2>&1; then
    echo "FAIL: Expected failure for invalid version '$bad_version' in set-minecraft-version.sh"
    exit 1
  fi
done
echo "PASS: Rejection of invalid version formats"

# Test 6: Rejection of cross-line versions
if PROPS_FILE="$TMP_PROPS" "$SET_SCRIPT" --base-minecraft 26.3 --supported-versions "26.3,26.4" >/dev/null 2>&1; then
  echo "FAIL: Expected failure for cross-line supported version 26.4"
  exit 1
fi
echo "PASS: Rejection of cross-line versions"

# Test 7: probe-minecraft-versions.sh validation check
for valid_ver in "26.3" "26.3.1" "1.21.11"; do
  if ! bash -c '
    source <(sed -n "/validate_mc_version() {/,/^}/p" "'"$PROBE_SCRIPT"'")
    validate_mc_version "$1"
  ' _ "$valid_ver" >/dev/null 2>&1; then
    echo "FAIL: probe-minecraft-versions.sh rejected valid version '$valid_ver'"
    exit 1
  fi
done

for invalid_ver in "invalid" "26" "26.3.1.2"; do
  if bash -c '
    source <(sed -n "/validate_mc_version() {/,/^}/p" "'"$PROBE_SCRIPT"'")
    validate_mc_version "$1"
  ' _ "$invalid_ver" >/dev/null 2>&1; then
    echo "FAIL: probe-minecraft-versions.sh accepted invalid version '$invalid_ver'"
    exit 1
  fi
done
echo "PASS: probe-minecraft-versions.sh format validation"

echo "=== All version script tests passed successfully ==="
