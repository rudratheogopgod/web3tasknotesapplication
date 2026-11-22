package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.ui.test.assertIsDisplayed
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
        val viewModel = NotesListViewModel(repository)
        try {
            compose.setContent {
                Web3TaskNotesTheme { NotesListRoute({}, {}, viewModel) }
            }
            awaitText("No notes yet")
            runBlocking { repository.save(null, "Weekend plans", "Pick up groceries").getOrThrow() }
            awaitText("Weekend plans")
            compose.onNodeWithText("Pick up groceries").assertIsDisplayed()
        } finally {
            viewModel.viewModelScope.cancel()
            db.close()
        }
    }

    private fun awaitText(text: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(text).assertIsDisplayed()
    }
}
