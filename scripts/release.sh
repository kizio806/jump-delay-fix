#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

MODE="dry-run"

for arg in "$@"; do
    case "$arg" in
        --publish)
            MODE="publish"
            ;;
        --dry-run)
            MODE="dry-run"
            ;;
        *)
            echo "Unknown argument: $arg"
            echo "Usage: $0 [--dry-run | --publish]"
            exit 1
            ;;
    esac
done

echo "=== Jump Delay Fix Release Preparation (mode: $MODE) ==="

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "Error: Not inside a git repository."
    exit 1
fi

BRANCH="$(git rev-parse --abbrev-ref HEAD)"
echo "Current branch: $BRANCH"

if [[ ! -f "gradle.properties" ]]; then
    echo "Error: gradle.properties not found."
    exit 1
fi

MOD_ID="$(grep '^mod_id=' gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')"
MOD_VERSION="$(grep '^mod_version=' gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')"
MINECRAFT_VERSION="$(grep '^minecraft_version=' gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')"

echo "Mod ID: $MOD_ID"
echo "Mod Version: $MOD_VERSION"
echo "Target Minecraft Version: $MINECRAFT_VERSION"

TAG_NAME="v${MOD_VERSION}"

case "$BRANCH" in
    support/1.21.9-1.21.11)
        EXPECTED_VERSION_PREFIX="1.1."
        ;;
    support/26.1.x)
        EXPECTED_VERSION_PREFIX="1.2."
        ;;
    support/26.2.x)
        EXPECTED_VERSION_PREFIX="1.3."
        ;;
    release/*)
        RELEASE_SUFFIX="${BRANCH#release/}"
        if [[ "$RELEASE_SUFFIX" =~ ^([0-9]+\.[0-9]+) ]]; then
            EXPECTED_VERSION_PREFIX="${BASH_REMATCH[1]}."
        else
            EXPECTED_VERSION_PREFIX=""
        fi
        ;;
    main)
        if [[ "$MOD_VERSION" =~ ^(1\.[0-3]\.) ]]; then
            echo "Error: Mod version '$MOD_VERSION' on main belongs to an older support line (expected >= 1.4.0)."
            exit 1
        fi
        EXPECTED_VERSION_PREFIX=""
        ;;
    *)
        echo "Warning: Current branch '$BRANCH' is not one of the standard support/release branches."
        EXPECTED_VERSION_PREFIX=""
        ;;
esac

if [[ -n "$EXPECTED_VERSION_PREFIX" && ! "$MOD_VERSION" =~ ^${EXPECTED_VERSION_PREFIX} ]]; then
    echo "Error: Mod version '$MOD_VERSION' does not match branch expectation for '$BRANCH' (expected prefix: '$EXPECTED_VERSION_PREFIX')."
    exit 1
fi

if [[ ! -f "CHANGELOG.md" ]]; then
    echo "Error: CHANGELOG.md not found."
    exit 1
fi

if ! grep -q "^## ${MOD_VERSION}" CHANGELOG.md; then
    echo "Error: CHANGELOG.md does not contain an entry for version '## ${MOD_VERSION}'."
    exit 1
fi

if git rev-parse "$TAG_NAME" >/dev/null 2>&1; then
    echo "Error: Local git tag '$TAG_NAME' already exists."
    exit 1
fi

if git remote get-url origin >/dev/null 2>&1; then
    REMOTE_TAG_CHECK=$(git ls-remote --tags origin "refs/tags/${TAG_NAME}" || true)
    if [[ -n "$REMOTE_TAG_CHECK" ]]; then
        echo "Error: Remote tag '$TAG_NAME' already exists on origin."
        exit 1
    fi
fi

if [[ "$MODE" == "publish" ]]; then
    if [[ -n "$(git status --porcelain)" ]]; then
        echo "Error: Working directory has uncommitted changes. Commit or stash all changes before running --publish."
        exit 1
    fi
fi

echo "--- 1. Validating repository structure and source hygiene ---"
./scripts/validate-structure.sh
./scripts/validate-source-hygiene.sh

echo "--- 2. Running strict verification and pre-publish gates ---"
./gradlew --no-daemon strictCheck publishReadyCheck

echo "--- 3. Building release artifacts ---"
if [[ -f "./scripts/build-all-versions.sh" ]]; then
    ./scripts/build-all-versions.sh
else
    ./gradlew --no-daemon buildAll
fi

echo "--- 4. Verifying built artifacts ---"
if [[ -d "release-artifacts" && -f "./scripts/build-all-versions.sh" ]]; then
    echo "Verifying multi-version release artifacts in release-artifacts/:"
    for ver in 1.21.9 1.21.10 1.21.11; do
        fab="release-artifacts/${MOD_ID}-fabric-${ver}-${MOD_VERSION}.jar"
        neo="release-artifacts/${MOD_ID}-neoforge-${ver}-${MOD_VERSION}.jar"
        if [[ ! -f "$fab" ]]; then
            echo "Error: Fabric artifact not found: $fab"
            exit 1
        fi
        if [[ ! -f "$neo" ]]; then
            echo "Error: NeoForge artifact not found: $neo"
            exit 1
        fi
        echo "Found Fabric artifact: $fab ($(du -h "$fab" | cut -f1))"
        echo "Found NeoForge artifact: $neo ($(du -h "$neo" | cut -f1))"
    done
else
    FABRIC_JAR="fabric/build/libs/${MOD_ID}-fabric-${MINECRAFT_VERSION}-${MOD_VERSION}.jar"
    NEOFORGE_JAR="neoforge/build/libs/${MOD_ID}-neoforge-${MINECRAFT_VERSION}-${MOD_VERSION}.jar"

    if [[ ! -f "$FABRIC_JAR" ]]; then
        echo "Error: Fabric artifact not found: $FABRIC_JAR"
        exit 1
    fi

    if [[ ! -f "$NEOFORGE_JAR" ]]; then
        echo "Error: NeoForge artifact not found: $NEOFORGE_JAR"
        exit 1
    fi

    echo "Found Fabric artifact: $FABRIC_JAR ($(du -h "$FABRIC_JAR" | cut -f1))"
    echo "Found NeoForge artifact: $NEOFORGE_JAR ($(du -h "$NEOFORGE_JAR" | cut -f1))"
fi

if [[ "$MODE" == "dry-run" ]]; then
    echo ""
    echo "=== DRY-RUN VERIFICATION SUCCESSFUL ==="
    echo "Branch: $BRANCH"
    echo "Target tag: $TAG_NAME"
    echo "Version: $MOD_VERSION"
    echo "All pre-release gates, tests, hygiene checks, and builds passed."
    echo "To perform the actual release commit and tag push, run:"
    echo "  ./scripts/release.sh --publish"
    exit 0
fi

echo "--- 5. Preparing release commit and tag ---"

if ! git diff-index --quiet HEAD --; then
    git add -A
    git commit -m "chore(release): ${TAG_NAME}"
fi

git tag -a "$TAG_NAME" -m "Release ${TAG_NAME}"

echo "--- 6. Pushing branch and tag to origin ---"
git push origin "$BRANCH" "$TAG_NAME"

echo ""
echo "=== RELEASE PUBLISHED SUCCESSFULLY ==="
echo "Pushed branch: $BRANCH"
echo "Pushed tag: $TAG_NAME"
echo "GitHub Actions will build and deploy the release automatically."
