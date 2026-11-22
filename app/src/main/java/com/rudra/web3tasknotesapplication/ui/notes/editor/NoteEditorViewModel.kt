package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.web3tasknotesapplication.data.repository.BlankNoteException
import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.ui.navigation.EDITOR_NOTE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Restores drafts independently of the database snapshot used to detect edits. */
@HiltViewModel
internal class NoteEditorViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: NotesRepository,
) : ViewModel() {
    private val noteId = savedState.get<Long>(EDITOR_NOTE_ID)
    private var originalTitle = ""
    private var originalContent = ""
    private val mutableState = MutableStateFlow(NoteEditorUiState(
        isNewNote = noteId == null,
        title = savedState[TITLE] ?: "",
        content = savedState[CONTENT] ?: "",
        isLoading = noteId != null,
    ))
    val state = mutableState.asStateFlow()
    private val events = Channel<EditorEffect>(Channel.BUFFERED)
    val effects = events.receiveAsFlow()

    init {
        if (noteId == null) {
            updateDraft(state.value.title, state.value.content)
        } else {
            loadNote(noteId)
        }
    }

    private fun loadNote(id: Long) = viewModelScope.launch {
        val note = try {
            repository.observeById(id).first()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            mutableState.update { it.copy(isLoading = false, error = EditorError.LOAD_FAILED) }
            return@launch
        }
        if (note == null) {
            events.send(EditorEffect.NOT_FOUND)
            return@launch
        }
        originalTitle = note.title
        originalContent = note.content
        val title = savedState[TITLE] ?: note.title
        val content = savedState[CONTENT] ?: note.content
        mutableState.update { it.copy(
            title = title, content = content, updatedAt = note.updatedAt, isLoading = false,
            hasUnsavedChanges = title != originalTitle || content != originalContent,
        ) }
    }

    fun changeTitle(title: String) = updateDraft(title, state.value.content)
    fun changeContent(content: String) = updateDraft(state.value.title, content)

    private fun updateDraft(title: String, content: String) {
        val current = state.value
        if (current.isSaving || current.isLoading || current.error == EditorError.LOAD_FAILED) return
        savedState[TITLE] = title
        savedState[CONTENT] = content
        mutableState.update {
            it.copy(title = title, content = content, error = null,
                hasUnsavedChanges = title != originalTitle || content != originalContent)
        }
    }

    fun requestBack() {
        val current = state.value
        if (current.isSaving) return
        if (current.hasUnsavedChanges) {
            mutableState.update { it.copy(showDiscardDialog = true) }
        } else {
            events.trySend(EditorEffect.CLOSE)
        }
    }

    fun keepEditing() = mutableState.update { it.copy(showDiscardDialog = false) }

    fun discard() {
        if (state.value.showDiscardDialog) {
            keepEditing()
            events.trySend(EditorEffect.CLOSE)
        }
    }

    fun save() {
        val draft = state.value
        if (draft.isSaving || draft.isLoading || draft.error == EditorError.LOAD_FAILED) return
        mutableState.update { it.copy(isSaving = true, showDiscardDialog = false, error = null) }
        viewModelScope.launch {
            val result = repository.save(noteId, draft.title, draft.content)
            if (result.isSuccess) {
                events.send(EditorEffect.SAVED)
            } else {
                val error = if (result.exceptionOrNull() is BlankNoteException) EditorError.BLANK
                    else EditorError.SAVE_FAILED
                mutableState.update { it.copy(isSaving = false, error = error) }
            }
        }
    }

    private companion object {
        const val TITLE = "draft_title"
        const val CONTENT = "draft_content"
    }
}
