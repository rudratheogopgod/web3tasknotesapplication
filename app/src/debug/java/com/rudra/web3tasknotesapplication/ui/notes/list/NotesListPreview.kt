package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme

@Preview(name = "Light notes")
@Composable
private fun LightNotesPreview() {
    Web3TaskNotesTheme(themeMode = ThemeMode.LIGHT) {
        NotesListScreen(NotesListUiState.Loaded(listOf(
            Note(1, "Shopping list", "Milk, eggs and coffee", 0, 0),
            Note(2, "Weekend plans", "Visit the bookshop", 0, 0),
        )), {}, {})
    }
}

@Preview(name = "Dark empty notes", fontScale = 2f)
@Composable
private fun DarkEmptyNotesPreview() {
    Web3TaskNotesTheme(themeMode = ThemeMode.DARK) {
        NotesListScreen(NotesListUiState.Empty, {}, {})
    }
}
