package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.web3tasknotesapplication.data.repository.BlankNoteException
import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
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
    val noteId = savedState.get<Long>(EDITOR_NOTE_ID)
    private var originalTitle = ""
    private var originalContent = ""
    private var originalOptions = NoteOptions()
    private val mutableState = MutableStateFlow(NoteEditorUiState(
        isNewNote = noteId == null,
        title = savedState[TITLE] ?: "",
        content = savedState[CONTENT] ?: "",
        options = restoreOptions(originalOptions),
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
        originalOptions = note.options
        val title = savedState[TITLE] ?: note.title
        val content = savedState[CONTENT] ?: note.content
        val options = restoreOptions(originalOptions)
        mutableState.update { it.copy(
            title = title, content = content, updatedAt = note.updatedAt, isLoading = false,
            options = options,
            hasUnsavedChanges = isChanged(title, content, options),
        ) }
    }

    fun changeTitle(title: String) = updateDraft(title, state.value.content)
    fun changeContent(content: String) = updateDraft(state.value.title, content)

    fun changeOptions(options: NoteOptions) = updateDraft(state.value.title, state.value.content, options)

    private fun updateDraft(title: String, content: String, options: NoteOptions = state.value.options) {
        val current = state.value
        if (current.isSaving || current.isLoading || current.error == EditorError.LOAD_FAILED) return
        savedState[TITLE] = title
        savedState[CONTENT] = content
        savedState[PINNED] = options.isPinned
        savedState[COLOR] = options.colorId.name
        savedState[CATEGORY] = options.category.name
        mutableState.update {
            it.copy(title = title, content = content, options = options, error = null,
                hasUnsavedChanges = isChanged(title, content, options))
        }
    }

    private fun isChanged(title: String, content: String, options: NoteOptions): Boolean =
        title != originalTitle || content != originalContent || options != originalOptions

    private fun restoreOptions(defaults: NoteOptions) = NoteOptions(
        isPinned = savedState[PINNED] ?: defaults.isPinned,
        colorId = NoteColor.entries.find { it.name == savedState.get<String>(COLOR) } ?: defaults.colorId,
        category = NoteCategory.entries.find { it.name == savedState.get<String>(CATEGORY) } ?: defaults.category,
    )

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
            val result = repository.save(noteId, draft.title, draft.content, draft.options)
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
        const val PINNED = "draft_pinned"
        const val COLOR = "draft_color"
        const val CATEGORY = "draft_category"
    }
}
