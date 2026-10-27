#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/set-minecraft-version.sh \
    --base-minecraft <x.y[.z]> \
    [--supported-versions <csv>] \
    [--fabric-supported-versions <csv>] \
    [--neoforge-supported-versions <csv>] \
    [--mod-version <MAJOR.MINOR.PATCH>] \
    [--yarn <value>] \
    [--fabric-api <value>] \
    [--neoforge <value>] \
    [--resource-pack-format <number>] \
    [--fabric-range ">=x.y[.z] <x.y"] \
    [--minecraft-range "[x.y,x.y)"]

Compatibility mode:
  --supported-versions sets both --fabric-supported-versions and --neoforge-supported-versions
  when explicit loader lists are not provided.

Example:
  ./scripts/set-minecraft-version.sh \
    --base-minecraft 1.21.11 \
    --fabric-supported-versions "1.21.9,1.21.10,1.21.11" \
    --neoforge-supported-versions "1.21.9,1.21.10,1.21.11" \
    --mod-version 1.0.0 \
    --yarn 1.21.11+build.1 \
    --fabric-api 0.138.4+1.21.10 \
    --neoforge 21.1.174
USAGE
}

require_value() {
  local flag="$1"
  local value="${2:-}"
  if [ -z "$value" ]; then
    echo "Missing value for $flag"
    usage
    exit 1
  fi
}

trim() {
  local value="$1"
  echo "$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
}

validate_mc_version() {
  local version="$1"
  if [[ ! "$version" =~ ^([0-9]+)\.([0-9]+)(\.([0-9]+))?$ ]]; then
    echo "Invalid Minecraft version '$version'. Expected MAJOR.MINOR or MAJOR.MINOR.PATCH"
    exit 1
  fi
}

version_sort_key() {
  local version="$1"
  local major minor patch
  IFS='.' read -r major minor patch <<< "$version"
  patch="${patch:-0}"
  printf '%08d.%08d.%08d %s\n' "$major" "$minor" "$patch" "$version"
}

next_patch_version() {
  local version="$1"
  local major minor patch
  IFS='.' read -r major minor patch <<< "$version"
  patch="${patch:-0}"
  echo "${major}.${minor}.$((patch + 1))"
}

normalize_supported_versions() {
  local csv="$1"
  local label="$2"
  local raw
  local value
  local normalized=()

  IFS=',' read -ra raw <<< "$csv"
  for value in "${raw[@]}"; do
    value="$(trim "$value")"
    [ -z "$value" ] && continue
    validate_mc_version "$value"
    normalized+=("$value")
  done

  if [ "${#normalized[@]}" -eq 0 ]; then
    echo "$label must contain at least one valid version"
    exit 1
  fi

  mapfile -t SUPPORTED_VERSIONS < <(
    for value in "${normalized[@]}"; do
      version_sort_key "$value"
    done | sort -u | awk '{print $2}'
  )
}

contains_value() {
  local needle="$1"
  shift
  local value
  for value in "$@"; do
    if [ "$value" = "$needle" ]; then
      return 0
    fi
  done
  return 1
}

ensure_on_base_patch_line() {
  local label="$1"
  shift
  local version
  local major minor patch

  for version in "$@"; do
    IFS='.' read -r major minor patch <<< "$version"
    if [ "$major" -ne "$base_major" ] || [ "$minor" -ne "$base_minor" ]; then
      echo "$label must stay on one patch line (${base_major}.${base_minor}.x). Found: $version"
      exit 1
    fi
  done
}

join_csv() {
  local values=("$@")
  if [ "${#values[@]}" -eq 0 ]; then
    echo ""
    return 0
  fi
  echo "$(IFS=,; echo "${values[*]}")"
}

set_prop() {
  local key="$1"
  local value="$2"
  local file="$3"

  local tmp
  tmp=$(mktemp)

  awk -v k="$key" -v v="$value" '
    BEGIN { done = 0 }
    {
      if ($0 ~ "^" k "=") {
        print k "=" v
        done = 1
      } else {
        print $0
      }
    }
    END {
      if (done == 0) {
        print k "=" v
      }
    }
  ' "$file" > "$tmp"

  mv "$tmp" "$file"
}

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS_FILE="${PROPS_FILE:-$PROJECT_ROOT/gradle.properties}"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Cannot find properties file at $PROPS_FILE"
  exit 1
fi

BASE_MC=""
SHARED_SUPPORTED=""
FABRIC_SUPPORTED=""
NEOFORGE_SUPPORTED=""
MOD_VERSION=""
YARN=""
FABRIC_API=""
NEOFORGE=""
MODMENU=""
NEOFORGE_LOADER_RANGE=""
RESOURCE_PACK_FORMAT=""
FABRIC_RANGE=""
MINECRAFT_RANGE=""

resolve_known_version_defaults() {
  local mc="$1"
  case "$mc" in
    1.21.9)
      [ -z "$YARN" ] && YARN="1.21.9+build.1"
      [ -z "$FABRIC_API" ] && FABRIC_API="0.134.1+1.21.9"
      [ -z "$NEOFORGE" ] && NEOFORGE="21.9.16-beta"
      [ -z "$MODMENU" ] && MODMENU="16.0.1"
      ;;
    1.21.10)
      [ -z "$YARN" ] && YARN="1.21.10+build.1"
      [ -z "$FABRIC_API" ] && FABRIC_API="0.138.4+1.21.10"
      [ -z "$NEOFORGE" ] && NEOFORGE="21.10.64"
      [ -z "$MODMENU" ] && MODMENU="16.0.1"
      ;;
    1.21.11)
      [ -z "$YARN" ] && YARN="1.21.11+build.1"
      [ -z "$FABRIC_API" ] && FABRIC_API="0.141.6+1.21.11"
      [ -z "$NEOFORGE" ] && NEOFORGE="21.11.48"
      [ -z "$MODMENU" ] && MODMENU="17.0.0-beta.2"
      ;;
    26.1|26.1.1|26.1.2)
      [ -z "$FABRIC_API" ] && FABRIC_API="0.155.2+26.1.2"
      [ -z "$NEOFORGE" ] && NEOFORGE="26.1.2.103"
      ;;
    26.2)
      [ -z "$FABRIC_API" ] && FABRIC_API="0.159.0+26.2"
      [ -z "$NEOFORGE" ] && NEOFORGE="26.2.0.79"
      ;;
    26.3)
      [ -z "$FABRIC_API" ] && FABRIC_API="0.160.6+26.3"
      [ -z "$NEOFORGE" ] && NEOFORGE="26.3.0.1-beta"
      ;;
  esac
}

while [ "$#" -gt 0 ]; do
  case "$1" in
    --base-minecraft)
      require_value "$1" "${2:-}"
      BASE_MC="$2"
      shift 2
      ;;
    --supported-versions)
      require_value "$1" "${2:-}"
      SHARED_SUPPORTED="$2"
      shift 2
      ;;
    --fabric-supported-versions)
      require_value "$1" "${2:-}"
      FABRIC_SUPPORTED="$2"
      shift 2
      ;;
    --neoforge-supported-versions)
      require_value "$1" "${2:-}"
      NEOFORGE_SUPPORTED="$2"
      shift 2
      ;;
    --mod-version)
      require_value "$1" "${2:-}"
      MOD_VERSION="$2"
      shift 2
      ;;
    --yarn)
      require_value "$1" "${2:-}"
      YARN="$2"
      shift 2
      ;;
    --fabric-api)
      require_value "$1" "${2:-}"
      FABRIC_API="$2"
      shift 2
      ;;
    --neoforge)
      require_value "$1" "${2:-}"
      NEOFORGE="$2"
      shift 2
      ;;
    --modmenu)
      require_value "$1" "${2:-}"
      MODMENU="$2"
      shift 2
      ;;
    --neoforge-loader-range)
      require_value "$1" "${2:-}"
      NEOFORGE_LOADER_RANGE="$2"
      shift 2
      ;;
    --resource-pack-format)
      require_value "$1" "${2:-}"
      RESOURCE_PACK_FORMAT="$2"
      shift 2
      ;;
    --fabric-range)
      require_value "$1" "${2:-}"
      FABRIC_RANGE="$2"
      shift 2
      ;;
    --minecraft-range)
      require_value "$1" "${2:-}"
      MINECRAFT_RANGE="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1"
      usage
      exit 1
      ;;
  esac
done

if [ -z "$BASE_MC" ]; then
  echo "--base-minecraft is required"
  usage
  exit 1
fi

if [ -z "$SHARED_SUPPORTED" ] && [ -z "$FABRIC_SUPPORTED" ] && [ -z "$NEOFORGE_SUPPORTED" ]; then
  echo "Provide --supported-versions or explicit --fabric-supported-versions and --neoforge-supported-versions"
  usage
  exit 1
fi

if [ -n "$SHARED_SUPPORTED" ]; then
  if [ -z "$FABRIC_SUPPORTED" ]; then
    FABRIC_SUPPORTED="$SHARED_SUPPORTED"
  fi
  if [ -z "$NEOFORGE_SUPPORTED" ]; then
    NEOFORGE_SUPPORTED="$SHARED_SUPPORTED"
  fi
fi

if [ -z "$FABRIC_SUPPORTED" ] || [ -z "$NEOFORGE_SUPPORTED" ]; then
  echo "Both --fabric-supported-versions and --neoforge-supported-versions must be provided (or use --supported-versions)."
  usage
  exit 1
fi

validate_mc_version "$BASE_MC"
resolve_known_version_defaults "$BASE_MC"

normalize_supported_versions "$FABRIC_SUPPORTED" "--fabric-supported-versions"
FABRIC_SUPPORTED_VERSIONS=("${SUPPORTED_VERSIONS[@]}")

normalize_supported_versions "$NEOFORGE_SUPPORTED" "--neoforge-supported-versions"
NEOFORGE_SUPPORTED_VERSIONS=("${SUPPORTED_VERSIONS[@]}")

if ! contains_value "$BASE_MC" "${FABRIC_SUPPORTED_VERSIONS[@]}"; then
  echo "Base version '$BASE_MC' must be part of --fabric-supported-versions"
  exit 1
fi

if ! contains_value "$BASE_MC" "${NEOFORGE_SUPPORTED_VERSIONS[@]}"; then
  echo "Base version '$BASE_MC' must be part of --neoforge-supported-versions"
  exit 1
fi

IFS='.' read -r base_major base_minor base_patch <<< "$BASE_MC"

ensure_on_base_patch_line "--fabric-supported-versions" "${FABRIC_SUPPORTED_VERSIONS[@]}"
ensure_on_base_patch_line "--neoforge-supported-versions" "${NEOFORGE_SUPPORTED_VERSIONS[@]}"

FABRIC_MIN="${FABRIC_SUPPORTED_VERSIONS[0]}"
FABRIC_MAX="${FABRIC_SUPPORTED_VERSIONS[${#FABRIC_SUPPORTED_VERSIONS[@]}-1]}"

MODRINTH_SUPPORTED_VERSIONS=()
for version in "${FABRIC_SUPPORTED_VERSIONS[@]}"; do
  if contains_value "$version" "${NEOFORGE_SUPPORTED_VERSIONS[@]}"; then
    MODRINTH_SUPPORTED_VERSIONS+=("$version")
  fi
done

if [ "${#MODRINTH_SUPPORTED_VERSIONS[@]}" -eq 0 ]; then
  echo "No shared versions between Fabric and NeoForge lists. Cannot set modrinth_game_versions."
  exit 1
fi

if [ -z "$FABRIC_RANGE" ]; then
  if [ "$base_major" -eq 1 ] && [ "$base_minor" -eq 21 ]; then
    FABRIC_RANGE=">=${FABRIC_MIN} <$(next_patch_version "$FABRIC_MAX")"
  else
    FABRIC_RANGE=">=${FABRIC_MIN} <${base_major}.$((base_minor + 1))"
  fi
fi

if [ -z "$MINECRAFT_RANGE" ]; then
  if [ "$base_major" -eq 1 ] && [ "$base_minor" -eq 21 ]; then
    NEOFORGE_MIN="${NEOFORGE_SUPPORTED_VERSIONS[0]}"
    NEOFORGE_MAX="${NEOFORGE_SUPPORTED_VERSIONS[${#NEOFORGE_SUPPORTED_VERSIONS[@]}-1]}"
    MINECRAFT_RANGE="[${NEOFORGE_MIN},$(next_patch_version "$NEOFORGE_MAX"))"
  else
    MINECRAFT_RANGE="[${base_major}.${base_minor},${base_major}.$((base_minor + 1)))"
  fi
fi

if [ -z "$NEOFORGE_LOADER_RANGE" ]; then
  if [ "$base_major" -eq 1 ] && [ "$base_minor" -eq 21 ]; then
    NEOFORGE_MIN="${NEOFORGE_SUPPORTED_VERSIONS[0]}"
    NEOFORGE_MAX="${NEOFORGE_SUPPORTED_VERSIONS[${#NEOFORGE_SUPPORTED_VERSIONS[@]}-1]}"
    min_patch=$(echo "$NEOFORGE_MIN" | cut -d'.' -f3)
    max_patch=$(echo "$NEOFORGE_MAX" | cut -d'.' -f3)
    next_max_patch=$((max_patch + 1))
    NEOFORGE_LOADER_RANGE="[21.${min_patch},21.${next_max_patch})"
  fi
fi

FABRIC_SUPPORTED_JOINED="$(join_csv "${FABRIC_SUPPORTED_VERSIONS[@]}")"
NEOFORGE_SUPPORTED_JOINED="$(join_csv "${NEOFORGE_SUPPORTED_VERSIONS[@]}")"
MODRINTH_SUPPORTED_JOINED="$(join_csv "${MODRINTH_SUPPORTED_VERSIONS[@]}")"

set_prop "minecraft_version" "$BASE_MC" "$PROPS_FILE"
set_prop "fabric_game_versions" "$FABRIC_SUPPORTED_JOINED" "$PROPS_FILE"
set_prop "neoforge_game_versions" "$NEOFORGE_SUPPORTED_JOINED" "$PROPS_FILE"
set_prop "modrinth_game_versions" "$MODRINTH_SUPPORTED_JOINED" "$PROPS_FILE"
set_prop "fabric_minecraft_version_range" "$FABRIC_RANGE" "$PROPS_FILE"
set_prop "minecraft_version_range" "$MINECRAFT_RANGE" "$PROPS_FILE"

if [ -n "$MOD_VERSION" ]; then
  set_prop "mod_version" "$MOD_VERSION" "$PROPS_FILE"
fi
if [ -n "$YARN" ]; then
  set_prop "yarn_mappings" "$YARN" "$PROPS_FILE"
fi
if [ -n "$FABRIC_API" ]; then
  set_prop "fabric_api_version" "$FABRIC_API" "$PROPS_FILE"
fi
if [ -n "$NEOFORGE" ]; then
  set_prop "neoforge_version" "$NEOFORGE" "$PROPS_FILE"
fi
if [ -n "$MODMENU" ]; then
  set_prop "modmenu_version" "$MODMENU" "$PROPS_FILE"
fi
if [ -n "$NEOFORGE_LOADER_RANGE" ]; then
  set_prop "neoforge_loader_range" "$NEOFORGE_LOADER_RANGE" "$PROPS_FILE"
fi
if [ -n "$RESOURCE_PACK_FORMAT" ]; then
  set_prop "resource_pack_format" "$RESOURCE_PACK_FORMAT" "$PROPS_FILE"
fi

echo "Updated gradle.properties"
echo "- minecraft_version=$BASE_MC"
echo "- fabric_game_versions=$FABRIC_SUPPORTED_JOINED"
echo "- neoforge_game_versions=$NEOFORGE_SUPPORTED_JOINED"
echo "- modrinth_game_versions=$MODRINTH_SUPPORTED_JOINED"
echo "- fabric_minecraft_version_range=$FABRIC_RANGE"
echo "- minecraft_version_range=$MINECRAFT_RANGE"
if [ -n "$MOD_VERSION" ]; then
  echo "- mod_version=$MOD_VERSION"
fi
