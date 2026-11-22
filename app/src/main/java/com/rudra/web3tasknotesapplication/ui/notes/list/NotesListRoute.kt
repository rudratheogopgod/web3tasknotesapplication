package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun NotesListRoute(
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    viewModel: NotesListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState? = null,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    NotesListScreen(state, onCreateNote, onOpenNote, snackbarHostState)
}
