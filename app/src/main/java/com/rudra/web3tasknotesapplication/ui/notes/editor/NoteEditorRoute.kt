package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun NoteEditorRoute(
    onResult: (EditorEffect) -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
        onBack = viewModel::requestBack,
        onSave = viewModel::save,
        onDiscard = viewModel::discard,
        onKeepEditing = viewModel::keepEditing,
    )
}
