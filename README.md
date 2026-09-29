# MongoMovies

**Status: work in progress.** A Kotlin/Jetpack Compose Android movie-browsing app using TMDB, Ktor, Room, Paging, and Coil.

The existing source includes home and search screens, movie details, trailers, and a local watchlist. Existing UI changes were preserved as found; they were not functionally verified during repository preparation.

## Local setup

1. Open this repository folder in Android Studio and configure your Android SDK. The app compiles against and targets SDK 36, with minimum SDK 26.
2. Copy `local.properties.template` to `local.properties` if the file does not exist. Configure your SDK path and your own TMDB credentials there. Never commit this file.
3. Synchronize Gradle and choose an appropriate Gradle JDK. The checked-in wrapper currently specifies Gradle 9.1.0; the version catalog specifies Android Gradle Plugin 8.13.2 and Kotlin 1.9.24. The app requests a Java 17 toolchain. These settings need a compatibility/build check before the setup is considered verified.
4. Run the `app` module on a device or emulator.

From a configured terminal, `./gradlew :app:assembleDebug` builds a debug APK. No build was run during the storage cleanup because this task preserved the baseline and removed generated storage.

See [SECURITY.md](SECURITY.md) for the existing credential setup guide. Credentials are injected into the Android client at build time; excluding them from Git does not make them secret inside a distributed APK. Review credential handling before distributing builds.

## Preserved baseline

On 2026-09-29, existing uncommitted UI, navigation, theme, and build-configuration changes were saved without rewriting application code. Git history was retained. Project build outputs and caches were removed; source, resources, the Gradle wrapper, local credentials, and local IDE settings remain available.

The old large ZIP was replaced by a private local recovery archive that preserves its non-generated files, including its historical source snapshot and Git metadata. Local credentials and IDE settings are excluded from publication. A scan of reachable history and the current publishable files found no matches for the local credentials or the credential patterns checked; this is not a complete security audit.

See [ROADMAP.md](ROADMAP.md) for future improvements.
