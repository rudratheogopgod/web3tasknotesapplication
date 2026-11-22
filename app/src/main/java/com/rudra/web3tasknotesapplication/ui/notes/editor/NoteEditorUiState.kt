package com.rudra.web3tasknotesapplication.ui.notes.editor

/** Keeps the editable draft and the actions currently available to the user. */
internal data class NoteEditorUiState(
    val isNewNote: Boolean,
    val title: String = "",
    val content: String = "",
    val updatedAt: Long? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val error: EditorError? = null,
)

internal enum class EditorError { BLANK, SAVE_FAILED, LOAD_FAILED }

internal enum class EditorEffect { SAVED, CLOSE, NOT_FOUND }
