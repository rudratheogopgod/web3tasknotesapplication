# Design decisions

## Why MVVM

I wanted the database to remain the source of truth regardless of how much UI is visible. [NoteDao](../app/src/main/java/com/rudra/web3tasknotesapplication/data/local/NoteDao.kt) publishes Room flows. The repository maps rows, the [list ViewModel](../app/src/main/java/com/rudra/web3tasknotesapplication/ui/notes/list/NotesListViewModel.kt) combines them with search, sort, and category state, and the [list route](../app/src/main/java/com/rudra/web3tasknotesapplication/ui/notes/list/NotesListRoute.kt) collects the result with lifecycle awareness. Screen callbacks send actions back to the ViewModel.

## Why keep a repository

[NotesRepositoryImpl](../app/src/main/java/com/rudra/web3tasknotesapplication/data/repository/NotesRepositoryImpl.kt) owns Room transactions and the mapping between persistence and domain models. The UI does not need to know the shape of a Room row or how to build a search pattern. A hand-written fake can then exercise ViewModel behavior without running SQLite.

## How I prevent invalid and duplicate saves

The [editor ViewModel](../app/src/main/java/com/rudra/web3tasknotesapplication/ui/notes/editor/NoteEditorViewModel.kt) marks the draft as saving before launching the asynchronous write. Repeated presses during that write are ignored. [NoteSaveRules](../app/src/main/java/com/rudra/web3tasknotesapplication/data/repository/NoteSaveRules.kt) rejects whitespace-only notes, supplies the localized untitled label when there is body text, and preserves timestamps on an unchanged save. The repository compares with the latest row and writes in one transaction.

## Search and sorting

The list ViewModel trims the query, debounces nonblank input, and cancels stale observations. [NoteSearchPattern](../app/src/main/java/com/rudra/web3tasknotesapplication/data/repository/NoteSearchPattern.kt) escapes literal percent, underscore, and exclamation marks before binding the query. The DAO searches titles and bodies. Updated ordering breaks ties with ID, and title ordering uses SQLite NOCASE, then updated time and ID. The recent row independently uses creation time. SQLite gives the app fast enough search for a personal notebook, with limited Unicode case folding and a scan for substring queries.

## What changed for large windows

I chose a [single workspace](../app/src/main/java/com/rudra/web3tasknotesapplication/ui/adaptive/NotesWorkspace.kt) because resizing should change which panes appear without pushing another screen onto the app's navigation stack. [Window layout rules](../app/src/main/java/com/rudra/web3tasknotesapplication/ui/adaptive/NotesWindowLayout.kt) derive pane count, widths, and hinge exclusions from the current window. A separating vertical hinge divides the list and editor. In tabletop mode, text and controls occupy the upper and lower regions. Each selected ID gets its own keyed editor store and saved state; a new draft has a separate selection key. The shared Room database lets windows observe the same committed changes.

## A real trade-off

The app originally kept the splash on screen until a Compose side effect reported that the first frame had the correct theme. The editor instrumentation flow stalled at startup. [MainActivity](../app/src/main/java/com/rudra/web3tasknotesapplication/MainActivity.kt) now waits for DataStore in the activity lifecycle and sets up the window and composition using that mode. The connected suite passed after that change. On Android versions before 12, Android itself still draws the initial splash using the device theme.

## Backup and another day of work

The app stores notes locally and has no sync client. The manifest leaves Android's system backup enabled, so backup depends on the platform and device settings.

With another day, I would profile FTS4 and pagination with a large note database, add user-initiated export/import, and verify real device posture, window resizing, and keyboard interactions across the remaining layout matrix.
