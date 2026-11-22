package com.rudra.web3tasknotesapplication.ui.adaptive

import androidx.compose.ui.test.hasText
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
class NotesWorkspaceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun currentWindowKeepsDraftAcrossRecreationAndProtectsPaneChanges() {
        val twoPanes = compose.activity.resources.configuration.screenWidthDp >= NotesWindowLayout.TwoPaneMinWidth
        val prefix = UUID.randomUUID().toString()
        val first = "First $prefix"
        val second = "Second $prefix"
        val placeholder = "Select a note or create a new one"
        try {
            if (twoPanes) awaitText(placeholder) else compose.onNodeWithText(placeholder).assertDoesNotExist()
            createNote(first)
            if (twoPanes) createNote(second)
            compose.onAllNodes(hasText(first))[0].performClick()
            awaitText("Edit note")
            if (twoPanes) {
                compose.onNodeWithText("All notes").assertExists()
                compose.onNodeWithContentDescription("Back").assertDoesNotExist()
            } else {
                compose.onNodeWithText("All notes").assertDoesNotExist()
                compose.onNodeWithContentDescription("Back").assertExists()
            }
            compose.onNodeWithText("Start typing…").performTextInput("Draft before recreation")
            compose.activityRule.scenario.recreate()
            awaitText("Draft before recreation")
            if (twoPanes) {
                compose.onAllNodes(hasText(second))[0].performClick()
                awaitText("Discard changes?")
                compose.onNodeWithText("Keep editing").performClick()
                awaitText("Draft before recreation")
                compose.onAllNodes(hasText(second))[0].performClick()
                compose.onNodeWithText("Discard").performClick()
                awaitText("Edit note")
                compose.onNodeWithText("Draft before recreation").assertDoesNotExist()
                compose.onNodeWithContentDescription("Delete").performClick()
                compose.onNodeWithText("Delete").performClick()
                awaitText(placeholder)
                awaitText("Note deleted")
            } else {
                compose.onNodeWithContentDescription("Back").performClick()
                compose.onNodeWithText("Discard").performClick()
                awaitText("All notes")
            }
        } finally {
            val context = compose.activity.applicationContext
            val database = Room.databaseBuilder(context, NotesDatabase::class.java, "notes.db").build()
            try {
                runBlocking {
                    val dao = database.noteDao()
                    dao.observeAll().first().filter { it.title.endsWith(prefix) }.forEach { dao.delete(it) }
                }
            } finally {
                database.close()
            }
        }
    }

    private fun createNote(title: String) {
        compose.onNodeWithContentDescription("Create note").performClick()
        awaitText("Title")
        compose.onNodeWithText("Title").performTextInput(title)
        compose.onNodeWithText("Save").performClick()
        awaitText(title)
    }

    private fun awaitText(text: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
