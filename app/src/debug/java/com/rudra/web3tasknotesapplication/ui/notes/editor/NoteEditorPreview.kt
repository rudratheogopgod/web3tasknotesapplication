package com.rudra.web3tasknotesapplication.ui.notes.editor

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.web3tasknotesapplication.ui.components.DeleteNoteDialog
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme

@Preview(name = "Light editor", widthDp = 360, heightDp = 640)
@Preview(
    name = "Dark editor · 320 dp · large text", widthDp = 320, heightDp = 640,
    fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(name = "Landscape editor · large text", widthDp = 640, heightDp = 320, fontScale = 2f)
@Composable
private fun NoteEditorPreview() {
    Web3TaskNotesTheme {
        NoteEditorScreen(
            state = NoteEditorUiState(isNewNote = false, title = "Weekend plans",
                content = "Visit the bookshop", updatedAt = 0),
            onTitleChange = {}, onContentChange = {}, onBack = {}, onOptionsChange = {},
            onSave = {}, onDiscard = {}, onKeepEditing = {}, onRequestDelete = {},
        )
    }
}

@Preview(name = "Light delete confirmation", widthDp = 320, heightDp = 640)
@Preview(name = "Dark delete confirmation", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeleteConfirmationPreview() {
    Web3TaskNotesTheme {
        DeleteNoteDialog(onConfirm = {}, onCancel = {})
    }
}
