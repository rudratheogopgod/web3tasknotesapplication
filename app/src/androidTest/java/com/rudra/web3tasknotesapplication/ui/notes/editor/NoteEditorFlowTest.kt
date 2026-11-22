package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rudra.web3tasknotesapplication.MainActivity
import com.rudra.web3tasknotesapplication.data.local.NotesDatabase
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteEditorFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun createsEditsDeletesAndUndoesThroughNavigation() {
        val title = "Editor test ${UUID.randomUUID()}"
        val context = compose.activity.applicationContext
        try {
            compose.onNodeWithContentDescription("Create note").performClick()
            compose.onNodeWithText("Title").performTextInput(title)
            compose.onNodeWithText("Save").performClick()
            awaitText("Note saved")
            awaitText(title)
            compose.onNodeWithText(title).performClick()
            awaitText("Start typing…")
            compose.onNodeWithText("Start typing…").performTextInput("Edited on device")
            compose.onNodeWithText("Save").performClick()
            awaitText("Edited on device")

            compose.onNodeWithText(title).performSemanticsAction(SemanticsActions.OnLongClick)
            compose.onNodeWithText("Delete").performClick()
            awaitText("Delete note?")
            compose.onNodeWithText("Cancel").performClick()

            compose.onNodeWithText(title).performClick()
            compose.onNodeWithContentDescription("Delete").performClick()
            awaitText("Delete note?")
            compose.onNodeWithText("Delete").performClick()
            awaitText("Note deleted")
            compose.onNodeWithText("Undo").performClick()
            awaitText("Edited on device")
        } finally {
            val database = Room.databaseBuilder(context, NotesDatabase::class.java, "notes.db").build()
            try {
                runBlocking {
                    val dao = database.noteDao()
                    dao.observeAll().first().filter { it.title == title }.forEach { dao.delete(it) }
                }
            } finally {
                database.close()
            }
        }
    }

    private fun awaitText(text: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
