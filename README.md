# Cool Notes

An offline notes app built in Kotlin with Jetpack Compose and Material 3.

The existing project is `web3tasknotesapplication`; its application ID is
`com.rudra.web3tasknotesapplication`.

Phase 0 provides a blank themed screen. Notes features are not implemented yet.

## Build and test

Open the project in Android Studio with Android SDK 37.2 installed.
The Gradle daemon toolchain is configured for JDK 25.

On Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

On macOS/Linux:

```shell
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Run the `app` configuration on an emulator or device running Android 8.0
(API 26) or later. The launcher label comes from the `app_name` resource.
The baseline has no Internet permission.

The template's backup settings are retained for this baseline; the local-data
backup policy will be documented when persistence is implemented.

Commits are made manually using Conventional Commits after each completed phase.
