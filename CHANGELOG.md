## Unreleased

- Migrate the Android build to Built-in Kotlin: drop the `kotlin-android` plugin, the buildscript Kotlin Gradle Plugin and the explicit `kotlin-stdlib` dependency, and set the JVM target through `kotlin.compilerOptions`. Fixes Flutter's "plugins that apply Kotlin Gradle Plugin (KGP)" warning (#16).
- Replace `jvmToolchain(17)` with Java/Kotlin bytecode target 17, so building no longer requires an exact JDK 17 installation.
- Require Flutter 3.44 / Dart 3.12.

## 1.2.1

- Improve API documentation and fix static analysis warnings.

## 1.2.0

- Add iOS Swift Package Manager support.

## 1.1.3

- Update README.

## 1.1.2

- Fix error on iOS.

## 1.1.0

- Add battery temperature for Android.

## 1.0.0

- Initial release to fetch device thermal status information.
