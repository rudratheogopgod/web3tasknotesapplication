package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.components.DeleteNoteDialog

@Composable
internal fun NotesListRoute(
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    viewModel: NotesListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    confirmedEditorDeleteId: Long? = null,
    onEditorDeleteConsumed: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmId by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(confirmedEditorDeleteId) {
        if (confirmedEditorDeleteId != null) {
            viewModel.deleteNote(confirmedEditorDeleteId)
            onEditorDeleteConsumed()
        }
    }
    DeleteSnackbar(viewModel, snackbarHostState)
    NotesListScreen(state, onCreateNote, onOpenNote, snackbarHostState,
        onRequestDelete = { confirmId = it })
    confirmId?.let { id ->
        DeleteNoteDialog(
            onConfirm = {
                confirmId = null
                viewModel.deleteNote(id)
            },
            onCancel = { confirmId = null },
        )
    }
}

@Composable
private fun DeleteSnackbar(viewModel: NotesListViewModel, host: SnackbarHostState) {
    val messages by viewModel.deleteMessages.collectAsStateWithLifecycle()
    val message = messages.firstOrNull() ?: return
    val text = stringResource(when (message) {
        is DeleteMessage.Deleted -> R.string.note_deleted
        is DeleteMessage.DeleteFailed -> R.string.note_delete_failed
        is DeleteMessage.RestoreFailed -> R.string.note_restore_failed
    })
    val action = if (message is DeleteMessage.Deleted) stringResource(R.string.undo_delete) else null
    LaunchedEffect(message, host) {
        val result = host.showSnackbar(text, actionLabel = action, duration = SnackbarDuration.Short)
        viewModel.finishDeleteMessage(message, undo = result == SnackbarResult.ActionPerformed)
    }
}
