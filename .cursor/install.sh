#!/usr/bin/env bash
#
# Idempotent Cloud Agent setup for the BoxManagerNew Android project.
#
# Installs the Android SDK components required to build the app, points Gradle
# at them via local.properties, and warms the Gradle build. Safe to run
# repeatedly: existing SDK packages are detected and skipped.
set -euo pipefail

# Where the Android SDK lives. Overridable, but defaults to a per-user path so
# no elevated privileges are required.
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"

# Pinned toolchain versions (kept in sync with app/build.gradle.kts:
# compileSdk = 36 -> platforms;android-36 + build-tools;36.0.0).
CMDLINE_TOOLS_VERSION="11076708"
PLATFORM="platforms;android-36"
BUILD_TOOLS="build-tools;36.0.0"

require() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "ERROR: required tool '$1' was not found on PATH." >&2
    exit 1
  }
}
require java
require curl
require unzip

SDKMANAGER="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

if [ ! -x "$SDKMANAGER" ]; then
  echo "==> Installing Android command-line tools into $ANDROID_SDK_ROOT"
  mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
  tmp_zip="$(mktemp)"
  curl -fsSL -o "$tmp_zip" \
    "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
  rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest" \
    "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools"
  unzip -q "$tmp_zip" -d "$ANDROID_SDK_ROOT/cmdline-tools"
  mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" \
    "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  rm -f "$tmp_zip"
else
  echo "==> Android command-line tools already present in $ANDROID_SDK_ROOT"
fi

export ANDROID_SDK_ROOT
export ANDROID_HOME="$ANDROID_SDK_ROOT"

echo "==> Accepting Android SDK licenses"
yes | "$SDKMANAGER" --licenses >/dev/null 2>&1 || true

echo "==> Installing SDK packages (platform-tools, $PLATFORM, $BUILD_TOOLS)"
"$SDKMANAGER" "platform-tools" "$PLATFORM" "$BUILD_TOOLS"

# Gradle's Android plugin resolves the SDK from local.properties (gitignored).
echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
echo "==> Wrote local.properties (sdk.dir=$ANDROID_SDK_ROOT)"

chmod +x gradlew

echo "==> Warming Gradle build (assembleDebug)"
./gradlew --no-daemon assembleDebug

echo "==> Cloud Agent setup complete."
