package com.rudra.web3tasknotesapplication.ui.notes.list

import com.rudra.web3tasknotesapplication.domain.model.Note

/** Represents the first load separately from an empty database. */
internal sealed interface NotesListUiState {
    data object Loading : NotesListUiState
    data object Empty : NotesListUiState
    data class NoResults(val query: String) : NotesListUiState
    data object Unavailable : NotesListUiState
    data class Loaded(val notes: List<Note>) : NotesListUiState
}
