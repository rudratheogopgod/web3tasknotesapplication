package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class NotesListViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    val query = savedState.getStateFlow("search_query", "")
    private val orderName = savedState.getStateFlow("sort_order", NoteSortOrder.LAST_UPDATED.name)
    val sortOrder = orderName.map(NoteSortOrder::valueOf)
        .stateIn(viewModelScope, SharingStarted.Eagerly, NoteSortOrder.valueOf(orderName.value))

    fun changeQuery(value: String) {
        savedState["search_query"] = value
    }

    fun changeSortOrder(order: NoteSortOrder) {
        savedState["sort_order"] = order.name
    }
    private val writes = mutableSetOf<Long>()
    private val pending = MutableStateFlow<List<DeleteMessage>>(emptyList())
    val deleteMessages = pending.asStateFlow()

    val uiState = query.map(String::trim).distinctUntilChanged()
        .flatMapLatest { term ->
            flow {
                if (term.isNotEmpty()) delay(250)
                emitAll(sortOrder.flatMapLatest { order -> observeResults(term, order) })
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesListUiState.Loading)

    private fun observeResults(term: String, order: NoteSortOrder) =
        repository.observeAll(term, order).map { notes ->
            val state: NotesListUiState = when {
                notes.isNotEmpty() -> NotesListUiState.Loaded(notes)
                term.isNotEmpty() -> NotesListUiState.NoResults(term)
                else -> NotesListUiState.Empty
            }
            state
        }
        .catch { failure ->
            if (failure !is IOException && failure !is android.database.sqlite.SQLiteException) {
                throw failure
            }
            emit(NotesListUiState.Unavailable)
        }
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
