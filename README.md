# ElectriCalc Android

Native Android electrical calculator built with Kotlin and Jetpack Compose.

## Project details
- Application ID: `com.electricalc.app`
- Minimum Android: 8.0 (API 26)
- UI: Jetpack Compose / Material 3
- Default appearance: Graphite + Amber dark theme
- Offline-first: calculations run locally

## Open in Android Studio
Open this folder as an existing Gradle project. Use JDK 17 and Gradle 8.13.

## Build locally
```bash
gradle testDebugUnitTest
gradle assembleDebug
```

The debug APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions
The workflow at `.github/workflows/build-android.yml` runs tests and builds a debug APK on pushes to `main`, pull requests, and manual dispatch. Download the APK from the workflow run's Artifacts section.

## Current implementation
- Initial Compose home screen with category search
- Eight approved category tiles
- Initial tested Ohm's Law solver (SI units)
- Automated test and APK build workflow

The calculator category tiles are currently UI placeholders. The 16 Phase 1 calculators will be implemented and validated incrementally.
