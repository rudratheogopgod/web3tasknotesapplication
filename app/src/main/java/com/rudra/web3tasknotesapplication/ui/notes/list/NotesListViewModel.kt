package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.lifecycle.ViewModel
import com.rudra.web3tasknotesapplication.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
internal class NotesListViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {
    private val writes = mutableSetOf<Long>()
    private val pending = MutableStateFlow<List<DeleteMessage>>(emptyList())
    val deleteMessages = pending.asStateFlow()

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

    fun deleteNote(id: Long) {
        if (!writes.add(id)) return
        viewModelScope.launch {
            try {
                val result = repository.delete(id)
                val message = result.fold(
                    onSuccess = { DeleteMessage.Deleted(it) },
                    onFailure = { DeleteMessage.DeleteFailed() },
                )
                pending.update { it + message }
            } finally {
                writes.remove(id)
            }
        }
    }

    fun finishDeleteMessage(message: DeleteMessage, undo: Boolean) {
        if (pending.value.firstOrNull() !== message) return
        pending.update { it.drop(1) }
        if (undo && message is DeleteMessage.Deleted) {
            viewModelScope.launch {
                if (repository.restore(message.note).isFailure) {
                    pending.update { it + DeleteMessage.RestoreFailed() }
                }
            }
        }
    }
}

/** Each message represents one write, even when two writes fail in succession. */
internal sealed interface DeleteMessage {
    class Deleted(val note: Note) : DeleteMessage
    class DeleteFailed : DeleteMessage
    class RestoreFailed : DeleteMessage
}
