#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/set-minecraft-version.sh \
    --minecraft <x.y.z> \
    [--mod-version <MAJOR.MINOR.PATCH>] \
    [--yarn <value>] \
    [--fabric-api <value>] \
    [--neoforge <value>] \
    [--resource-pack-format <number>] \
    [--modrinth-game-versions <csv>]

Example:
  ./scripts/set-minecraft-version.sh \
    --minecraft 1.21.2 \
    --mod-version 1.1.1 \
    --yarn 1.21.2+build.1 \
    --fabric-api 0.103.0+1.21.2 \
    --neoforge 21.2.10 \
    --modrinth-game-versions "1.21.1,1.21.2"
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
PROPS_FILE="$PROJECT_ROOT/gradle.properties"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Cannot find gradle.properties at $PROPS_FILE"
  exit 1
fi

MC_VERSION=""
MOD_VERSION=""
YARN_MAPPINGS=""
FABRIC_API_VERSION=""
NEOFORGE_VERSION=""
RESOURCE_PACK_FORMAT=""
MODRINTH_GAME_VERSIONS=""

while [ "$#" -gt 0 ]; do
  case "$1" in
    --minecraft)
      require_value "$1" "${2:-}"
      MC_VERSION="$2"
      shift 2
      ;;
    --mod-version)
      require_value "$1" "${2:-}"
      MOD_VERSION="$2"
      shift 2
      ;;
    --yarn)
      require_value "$1" "${2:-}"
      YARN_MAPPINGS="$2"
      shift 2
      ;;
    --fabric-api)
      require_value "$1" "${2:-}"
      FABRIC_API_VERSION="$2"
      shift 2
      ;;
    --neoforge)
      require_value "$1" "${2:-}"
      NEOFORGE_VERSION="$2"
      shift 2
      ;;
    --resource-pack-format)
      require_value "$1" "${2:-}"
      RESOURCE_PACK_FORMAT="$2"
      shift 2
      ;;
    --modrinth-game-versions)
      require_value "$1" "${2:-}"
      MODRINTH_GAME_VERSIONS="$2"
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

if [ -z "$MC_VERSION" ]; then
  echo "--minecraft is required"
  usage
  exit 1
fi

if [[ ! "$MC_VERSION" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "Invalid --minecraft version '$MC_VERSION'. Expected format: x.y.z"
  exit 1
fi

MC_MAJOR="${BASH_REMATCH[1]}"
MC_MINOR="${BASH_REMATCH[2]}"
NEXT_MINOR=$((MC_MINOR + 1))
MC_RANGE="[${MC_MAJOR}.${MC_MINOR},${MC_MAJOR}.${NEXT_MINOR})"

if [ -z "$MODRINTH_GAME_VERSIONS" ]; then
  MODRINTH_GAME_VERSIONS="$MC_VERSION"
fi

set_prop "minecraft_version" "$MC_VERSION" "$PROPS_FILE"
set_prop "minecraft_version_range" "$MC_RANGE" "$PROPS_FILE"
set_prop "modrinth_game_versions" "$MODRINTH_GAME_VERSIONS" "$PROPS_FILE"

if [ -n "$MOD_VERSION" ]; then
  set_prop "mod_version" "$MOD_VERSION" "$PROPS_FILE"
fi
if [ -n "$YARN_MAPPINGS" ]; then
  set_prop "yarn_mappings" "$YARN_MAPPINGS" "$PROPS_FILE"
fi
if [ -n "$FABRIC_API_VERSION" ]; then
  set_prop "fabric_api_version" "$FABRIC_API_VERSION" "$PROPS_FILE"
fi
if [ -n "$NEOFORGE_VERSION" ]; then
  set_prop "neoforge_version" "$NEOFORGE_VERSION" "$PROPS_FILE"
fi
if [ -n "$RESOURCE_PACK_FORMAT" ]; then
  set_prop "resource_pack_format" "$RESOURCE_PACK_FORMAT" "$PROPS_FILE"
fi

echo "Updated gradle.properties:"
echo "- minecraft_version=$MC_VERSION"
echo "- minecraft_version_range=$MC_RANGE"
echo "- modrinth_game_versions=$MODRINTH_GAME_VERSIONS"
if [ -n "$MOD_VERSION" ]; then
  echo "- mod_version=$MOD_VERSION"
fi
if [ -n "$YARN_MAPPINGS" ]; then
  echo "- yarn_mappings=$YARN_MAPPINGS"
fi
if [ -n "$FABRIC_API_VERSION" ]; then
  echo "- fabric_api_version=$FABRIC_API_VERSION"
fi
if [ -n "$NEOFORGE_VERSION" ]; then
  echo "- neoforge_version=$NEOFORGE_VERSION"
fi
if [ -n "$RESOURCE_PACK_FORMAT" ]; then
  echo "- resource_pack_format=$RESOURCE_PACK_FORMAT"
fi

echo
echo "Next steps:"
echo "1) ./gradlew --no-daemon clean buildAll publishReadyCheck"
echo "2) git add gradle.properties"
if [ -n "$MOD_VERSION" ]; then
  echo "3) git commit -m \"chore(release): prepare MC $MC_VERSION and v$MOD_VERSION\""
  echo "4) git tag v$MOD_VERSION && git push origin HEAD --tags"
else
  echo "3) git commit -m \"chore(release): prepare MC $MC_VERSION\""
fi
