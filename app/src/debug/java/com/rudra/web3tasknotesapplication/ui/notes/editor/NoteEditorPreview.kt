package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme

@Preview(name = "Light editor", widthDp = 360, heightDp = 640)
@Preview(name = "Dark editor with large text", widthDp = 360, heightDp = 640,
    fontScale = 2f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteEditorPreview() {
    Web3TaskNotesTheme(themeMode = ThemeMode.SYSTEM) {
        NoteEditorScreen(
            state = NoteEditorUiState(isNewNote = false, title = "Weekend plans",
                content = "Visit the bookshop", updatedAt = 0),
            onTitleChange = {}, onContentChange = {}, onBack = {},
            onSave = {}, onDiscard = {}, onKeepEditing = {},
        )
    }
}
