package com.rudra.web3tasknotesapplication.ui.notes.list

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteColor
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.theme.ThemePicker
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme

@Preview(name = "Light notes", widthDp = 360, heightDp = 640)
@Preview(
    name = "Dark notes · 320 dp · large text", widthDp = 320, heightDp = 640,
    fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(name = "Landscape notes · large text", widthDp = 640, heightDp = 320, fontScale = 2f)
@Composable
private fun LoadedNotesPreview() {
    val pinned = Note(1, "Shopping list for a long weekend away", "Milk, eggs and coffee", 0, 0,
        isPinned = true, colorId = NoteColor.MINT)
    val other = Note(2, "Weekend plans", "Visit the bookshop", 0, 0)
    Web3TaskNotesTheme {
        NotesListScreen(
            state = NotesListUiState.Loaded(listOf(pinned, other)),
            onCreateNote = {},
            onOpenNote = {},
            recent = listOf(other),
            onSeeAll = {},
        )
    }
}

@Preview(name = "Light empty notes", widthDp = 320, heightDp = 560)
@Preview(name = "Dark empty notes", fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyNotesPreview() {
    Web3TaskNotesTheme {
        NotesListScreen(NotesListUiState.Empty, {}, {})
    }
}

@Preview(name = "Light search results", widthDp = 320, heightDp = 640)
@Preview(name = "Dark search results", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoResultsPreview() {
    Web3TaskNotesTheme {
        NotesListScreen(NotesListUiState.NoResults("books"), {}, {}, query = "books")
    }
}

@Preview(name = "Light categories", widthDp = 320, heightDp = 640)
@Preview(name = "Dark categories", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoryPickerPreview() {
    Web3TaskNotesTheme {
        CategoryPicker(NoteCategory.JOURNAL, mapOf(NoteCategory.JOURNAL to 3), {}, {})
    }
}

@Preview(name = "Light theme picker", widthDp = 320, heightDp = 640)
@Preview(name = "Dark theme picker", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemePickerPreview() {
    Web3TaskNotesTheme {
        ThemePicker(ThemeMode.SYSTEM, {}, {})
    }
}
