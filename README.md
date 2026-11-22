# Cool Notes

An offline notes app built in Kotlin with Jetpack Compose and Material 3.

The existing project is `web3tasknotesapplication`; its application ID is
`com.rudra.web3tasknotesapplication`.

Create, edit, search, and organize notes locally. Notes stay on the device and
are available offline.

## Adaptive layouts

Compact windows show the notes list or editor one at a time. At 600 dp and
above, the list and editor appear side by side. The selected note is highlighted,
and the editor column has a maximum width for comfortable reading.

The layout recalculates from the current window width. A separating vertical
hinge can divide the two panes. In tabletop posture, the editor puts the note
above the horizontal hinge and the actions below it.

Each editor selection has its own ViewModel scope and saved draft. Switching
notes with an unsaved draft opens the Save, Discard, or Keep editing dialog.
Room is shared across windows within the app process.

With a hardware keyboard, use Ctrl+N for a new note, Ctrl+S to save, and Ctrl+F
to focus search. Right-click a note to open its actions.

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

Commits are made manually using Conventional Commits after each completed phase.
