package com.rudra.web3tasknotesapplication.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.notes.editor.EditorEffect
import com.rudra.web3tasknotesapplication.ui.notes.editor.NoteEditorRoute
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data object Home

@Serializable
internal data object RecentNotes

/** A null ID opens a fresh draft. Existing notes are loaded by ID. */
@Serializable
internal data class Editor(@SerialName(EDITOR_NOTE_ID) val noteId: Long? = null)

internal const val EDITOR_NOTE_ID = "noteId"

private const val EDITOR_DELETE_ID = "editor_delete_id"
private const val EDITOR_RESULT = "editor_result"

/** Hosts the typed destinations and reports editor results on the home screen. */
@Composable
internal fun NotesNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> { entry ->
            NotesDestination(entry,
                onCreateNote = { navController.navigate(Editor()) { launchSingleTop = true } },
                onOpenNote = { id -> navController.navigate(Editor(id)) { launchSingleTop = true } },
                onSeeAll = { navController.navigate(RecentNotes) { launchSingleTop = true } },
            )
        }
        composable<RecentNotes> { entry ->
            NotesDestination(entry,
                onCreateNote = { navController.navigate(Editor()) { launchSingleTop = true } },
                onOpenNote = { id -> navController.navigate(Editor(id)) { launchSingleTop = true } },
                onBackFromRecent = { navController.popBackStack() },
            )
        }
        composable<Editor> { entry ->
            NoteEditorRoute(onResult = { effect ->
                if (navController.currentBackStackEntry == entry) {
                    if (effect != EditorEffect.CLOSE) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(EDITOR_RESULT, effect.name)
                    }
                    navController.popBackStack()
                }
            }, onDelete = { noteId ->
                if (navController.currentBackStackEntry == entry) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(EDITOR_DELETE_ID, noteId)
                    navController.popBackStack()
                }
            })
        }
    }
}

@Composable
private fun NotesDestination(
    entry: NavBackStackEntry,
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    onSeeAll: (() -> Unit)? = null,
    onBackFromRecent: (() -> Unit)? = null,
) {
    val snackbar = remember { SnackbarHostState() }
    val deleteId by entry.savedStateHandle.getStateFlow<Long?>(EDITOR_DELETE_ID, null)
        .collectAsStateWithLifecycle()
    val result by entry.savedStateHandle.getStateFlow<String?>(EDITOR_RESULT, null)
        .collectAsStateWithLifecycle()
    val message = when (result) {
        EditorEffect.SAVED.name -> stringResource(R.string.note_saved)
        EditorEffect.NOT_FOUND.name -> stringResource(R.string.note_not_found)
        else -> null
    }
    LaunchedEffect(message) {
        if (message != null) {
            snackbar.showSnackbar(message)
            entry.savedStateHandle[EDITOR_RESULT] = null
        }
    }
    NotesListRoute(
        onCreateNote = onCreateNote,
        onOpenNote = onOpenNote,
        onSeeAll = onSeeAll,
        onBackFromRecent = onBackFromRecent,
        snackbarHostState = snackbar,
        confirmedEditorDeleteId = deleteId,
        onEditorDeleteConsumed = { entry.savedStateHandle[EDITOR_DELETE_ID] = null },
    )
}
