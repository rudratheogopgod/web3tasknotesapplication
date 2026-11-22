package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Keeps Room as the source of truth and shares the latest list with the screen. */
@HiltViewModel
internal class NotesListViewModel @Inject constructor(repository: NotesRepository) : ViewModel() {
    val uiState = repository.observeAll()
        .map { notes ->
            val state: NotesListUiState =
                if (notes.isEmpty()) NotesListUiState.Empty else NotesListUiState.Loaded(notes)
            state
        }
        .catch { failure ->
            if (failure !is IOException && failure !is android.database.sqlite.SQLiteException) {
                throw failure
            }
            emit(NotesListUiState.Unavailable)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesListUiState.Loading)
}
