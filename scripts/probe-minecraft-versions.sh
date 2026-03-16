#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/probe-minecraft-versions.sh \
    --versions "<csv>" \
    [--tasks "<gradle tasks>"] \
    [--yarn-build "<build suffix>"] \
    [--keep-workdir]

Examples:
  ./scripts/probe-minecraft-versions.sh --versions "1.21.5,1.21.6,1.21.7,1.21.8"
  ./scripts/probe-minecraft-versions.sh --versions "1.21.8,1.21.9" --tasks "clean buildAll"
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
  # shellcheck disable=SC2001
  echo "$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
}

validate_mc_version() {
  local version="$1"
  if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Invalid Minecraft version '$version'. Expected MAJOR.MINOR.PATCH"
    exit 1
  fi
}

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SET_VERSION_SCRIPT="$PROJECT_ROOT/scripts/set-minecraft-version.sh"

if [ ! -x "$SET_VERSION_SCRIPT" ]; then
  echo "Cannot execute $SET_VERSION_SCRIPT"
  exit 1
fi

VERSIONS_CSV=""
GRADLE_TASKS="clean buildAll publishReadyCheck"
YARN_BUILD_SUFFIX="build.1"
KEEP_WORKDIR=0

while [ "$#" -gt 0 ]; do
  case "$1" in
    --versions)
      require_value "$1" "${2:-}"
      VERSIONS_CSV="$2"
      shift 2
      ;;
    --tasks)
      require_value "$1" "${2:-}"
      GRADLE_TASKS="$2"
      shift 2
      ;;
    --yarn-build)
      require_value "$1" "${2:-}"
      YARN_BUILD_SUFFIX="$2"
      shift 2
      ;;
    --keep-workdir)
      KEEP_WORKDIR=1
      shift
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

if [ -z "$VERSIONS_CSV" ]; then
  echo "--versions is required"
  usage
  exit 1
fi

IFS=',' read -ra RAW_VERSIONS <<< "$VERSIONS_CSV"
VERSIONS=()
for version in "${RAW_VERSIONS[@]}"; do
  version="$(trim "$version")"
  [ -z "$version" ] && continue
  validate_mc_version "$version"
  VERSIONS+=("$version")
done

if [ "${#VERSIONS[@]}" -eq 0 ]; then
  echo "No valid versions provided in --versions"
  exit 1
fi

WORKDIR="$(mktemp -d /tmp/jdf-probe.XXXXXX)"
REPO_COPY="$WORKDIR/repo"
LOG_DIR="$WORKDIR/logs"

mkdir -p "$REPO_COPY" "$LOG_DIR"
cp -a "$PROJECT_ROOT"/. "$REPO_COPY"
rm -rf "$REPO_COPY/.git" "$REPO_COPY/.gradle" "$REPO_COPY/build" "$REPO_COPY/common/build" "$REPO_COPY/fabric/build" "$REPO_COPY/neoforge/build"

echo "Probe workspace: $WORKDIR"
echo "Gradle tasks: $GRADLE_TASKS"
echo

PASS_COUNT=0
FAIL_COUNT=0

for version in "${VERSIONS[@]}"; do
  log_file="$LOG_DIR/probe_${version}.log"
  yarn_value="${version}+${YARN_BUILD_SUFFIX}"

  "$REPO_COPY/scripts/set-minecraft-version.sh" \
    --base-minecraft "$version" \
    --supported-versions "$version" \
    --yarn "$yarn_value" >/dev/null

  if (cd "$REPO_COPY" && ./gradlew --no-daemon --stacktrace $GRADLE_TASKS >"$log_file" 2>&1); then
    echo "$version PASS"
    PASS_COUNT=$((PASS_COUNT + 1))
  else
    reason="$(
      rg -m 1 "Unsupported unpick version|Execution failed for task|error: " "$log_file" \
        | sed 's/^[0-9:]*//' \
        || true
    )"

    if [ -z "$reason" ]; then
      reason="See $log_file"
    fi

    echo "$version FAIL - $reason"
    FAIL_COUNT=$((FAIL_COUNT + 1))
  fi
done

echo
echo "Summary: PASS=$PASS_COUNT FAIL=$FAIL_COUNT"
echo "Logs: $LOG_DIR"

if [ "$FAIL_COUNT" -gt 0 ]; then
  echo "Probe failed. Keeping workspace for inspection: $WORKDIR"
  exit 1
fi

if [ "$KEEP_WORKDIR" -ne 1 ]; then
  rm -rf "$WORKDIR"
else
  echo "Keeping workspace (--keep-workdir): $WORKDIR"
fi
