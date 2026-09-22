# Stashy

Stashy is an offline personal finance and savings goal tracker for Android, built with Jetpack Compose and Material 3.

## Features

- Automatic balance calculation and monthly financial summary.
- Fast transaction logging with Indonesian Rupiah (Rp) formatting and category filtering.
- Savings goals with completion estimation based on weekly savings history.
- 100% offline with zero internet permissions.
- Local JSON backup and restore via Android Storage Access Framework.

## Tech Stack

- Kotlin, Jetpack Compose, Material 3
- MVVM Architecture with Coroutines and StateFlow
- Room Database (KSP)
- Navigation Compose

## Build

Prerequisites: JDK 17, Android SDK (API 34).

```bash
# Build Release APK
./gradlew assembleRelease

# Run unit tests
./gradlew testDebugUnitTest
```

The compiled release APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`
