package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rudra.web3tasknotesapplication.core.dispatchers.DefaultDispatcherProvider
import com.rudra.web3tasknotesapplication.core.time.SystemTimeProvider
import com.rudra.web3tasknotesapplication.data.local.NotesDatabase
import com.rudra.web3tasknotesapplication.data.repository.NotesRepositoryImpl
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesHomeTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun homeDisplaysNoteAfterRoomInsert() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        val repository = NotesRepositoryImpl(db.noteDao(), db,
            DefaultDispatcherProvider, SystemTimeProvider, context)
        val viewModel = NotesListViewModel(repository, SavedStateHandle())
        try {
            compose.setContent {
                Web3TaskNotesTheme { NotesListRoute({}, {}, viewModel, onSeeAll = {}) }
            }
            awaitText("No notes yet")
            runBlocking { repository.save(null, "Weekend plans", "Pick up groceries").getOrThrow() }
            awaitText("Weekend plans")
            awaitText("Recently added")
            awaitText("Pick up groceries")
            compose.onNodeWithText("Search notes …").performTextInput("missing")
            awaitText("No notes match “missing”")
            compose.onNodeWithText("Recently added").assertDoesNotExist()
            compose.onNodeWithText("Create your first note").assertDoesNotExist()
            compose.onNodeWithText("Categories").assertDoesNotExist()
            compose.onNodeWithContentDescription("Clear search").performClick()
            awaitText("Weekend plans")
            compose.onAllNodes(hasText("Weekend plans"))[0].performSemanticsAction(SemanticsActions.OnLongClick)
            compose.onNodeWithText("Pin note").performClick()
            awaitText("Pinned")
            compose.onNodeWithText("Browse").performClick()
            compose.onNode(hasText("Journal") and hasText("0")).performClick()
            awaitText("No notes in this category")
            compose.onNodeWithText("Search notes …").performTextInput("Weekend")
            awaitText("Weekend plans")
            compose.onNodeWithText("Pinned").assertDoesNotExist()
            compose.onNodeWithText("Categories").assertDoesNotExist()
        } finally {
            viewModel.viewModelScope.cancel()
            db.close()
        }
    }

    private fun awaitText(text: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodes(androidx.compose.ui.test.hasText(text))[0].assertIsDisplayed()
    }
}
