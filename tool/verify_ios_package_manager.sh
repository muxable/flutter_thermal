#!/bin/zsh

set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "usage: $0 <cocoapods|spm>" >&2
  exit 1
fi

mode="$1"
flutter_bin="${FLUTTER_BIN:-flutter}"

case "$mode" in
  cocoapods)
    "$flutter_bin" config --no-enable-swift-package-manager
    ;;
  spm)
    "$flutter_bin" config --enable-swift-package-manager
    ;;
  *)
    echo "unsupported mode: $mode" >&2
    exit 1
    ;;
esac

"$flutter_bin" pub get
pushd example >/dev/null
rm -rf ios/Pods ios/.symlinks ios/Flutter/ephemeral ios/Flutter/Generated.xcconfig ios/Flutter/flutter_export_environment.sh
"$flutter_bin" clean
"$flutter_bin" pub get
"$flutter_bin" build ios --simulator --debug
popd >/dev/null
