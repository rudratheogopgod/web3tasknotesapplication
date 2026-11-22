package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.rudra.web3tasknotesapplication.ui.components.DeleteNoteDialog
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun NoteEditorRoute(
    onResult: (EditorEffect) -> Unit,
    onDelete: (Long) -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val deliverResult by rememberUpdatedState(onResult)
    LaunchedEffect(viewModel) { viewModel.effects.collect { deliverResult(it) } }
    PredictiveBackHandler(enabled = state.hasUnsavedChanges || state.isSaving) { progress ->
        progress.collect {}
        viewModel.requestBack()
    }
    NoteEditorScreen(
        state = state,
        onTitleChange = viewModel::changeTitle,
        onContentChange = viewModel::changeContent,
        onOptionsChange = viewModel::changeOptions,
        onBack = viewModel::requestBack,
        onSave = viewModel::save,
        onDiscard = viewModel::discard,
        onKeepEditing = viewModel::keepEditing,
        onRequestDelete = { confirmDelete = true },
    )
    if (confirmDelete) {
        DeleteNoteDialog(
            onConfirm = {
                confirmDelete = false
                viewModel.noteId?.let(onDelete)
            },
            onCancel = { confirmDelete = false },
        )
    }
}
