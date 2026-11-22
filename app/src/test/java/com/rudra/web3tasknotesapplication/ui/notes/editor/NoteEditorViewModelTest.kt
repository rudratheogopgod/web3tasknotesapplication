package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rudra.web3tasknotesapplication.data.repository.BlankNoteException
import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun rejectsBlankAndPreventsDuplicateSaveWhileWriting() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = NoteEditorViewModel(SavedStateHandle(), repository)
        model.changeTitle("  ")
        model.save()
        runCurrent()
        assertEquals(EditorError.BLANK, model.state.value.error)
        assertEquals(0, repository.saves)

        model.changeContent("Draft")
        model.save()
        model.save()
        runCurrent()
        assertTrue(model.state.value.isSaving)
        assertEquals(1, repository.saves)
        repository.finishSave.complete(Unit)
        assertEquals(EditorEffect.SAVED, model.effects.first())
        model.viewModelScope.cancel()
    }

    @Test fun restoredDraftDiffersFromPersistedNoteAndCanBeDiscarded() = runTest(dispatcher) {
        val original = Note(7, "Original", "Body", 10, 20)
        val repository = FakeRepository(original)
        val first = NoteEditorViewModel(SavedStateHandle(mapOf("noteId" to 7L)), repository)
        runCurrent()
        assertEquals("Original", first.state.value.title)
        assertFalse(first.state.value.hasUnsavedChanges)
        val handle = SavedStateHandle(mapOf("noteId" to 7L))
        val drafting = NoteEditorViewModel(handle, repository)
        runCurrent()
        drafting.changeTitle("New title")
        drafting.viewModelScope.cancel()
        val restored = NoteEditorViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any>(it) }), repository)
        runCurrent()
        assertEquals("New title", restored.state.value.title)
        assertEquals(20L, restored.state.value.updatedAt)
        restored.requestBack()
        assertTrue(restored.state.value.showDiscardDialog)
        restored.keepEditing()
        assertFalse(restored.state.value.showDiscardDialog)
        restored.requestBack()
        restored.discard()
        assertEquals(EditorEffect.CLOSE, restored.effects.first())
        restored.viewModelScope.cancel()
        first.viewModelScope.cancel()
    }

    @Test fun missingIdReturnsToHome() = runTest(dispatcher) {
        val model = NoteEditorViewModel(SavedStateHandle(mapOf("noteId" to 9L)), FakeRepository())
        assertEquals(EditorEffect.NOT_FOUND, model.effects.first())
        model.viewModelScope.cancel()
    }

    @Test
    fun metadataChangesRestoreAsAnUnsavedDraftAndArePassedToSave() = runTest(dispatcher) {
        val note = Note(7, "Title", "Body", 10, 20)
        val repository = FakeRepository(note)
        val handle = SavedStateHandle(mapOf("noteId" to note.id))
        val original = NoteEditorViewModel(handle, repository)
        runCurrent()
        val options = NoteOptions(true, NoteColor.MINT, NoteCategory.TODO_LIST)
        original.changeOptions(options)
        original.requestBack()
        assertTrue(original.state.value.showDiscardDialog)
        original.viewModelScope.cancel()

        val restoredHandle = SavedStateHandle(handle.keys().associateWith { handle.get<Any>(it) })
        val restored = NoteEditorViewModel(restoredHandle, repository)
        runCurrent()
        assertEquals(options, restored.state.value.options)
        assertTrue(restored.state.value.hasUnsavedChanges)
        repository.finishSave.complete(Unit)
        restored.save()
        assertEquals(EditorEffect.SAVED, restored.effects.first())
        assertEquals(options, repository.savedOptions)
        restored.viewModelScope.cancel()
    }

    private class FakeRepository(private val note: Note? = null) : NotesRepository {
        var saves = 0
        var savedOptions: NoteOptions? = null
        val finishSave = CompletableDeferred<Unit>()
        override fun observeAll(query: String, order: NoteSortOrder) = flowOf(listOfNotNull(note))
        override fun observeCreated(limit: Int) = error("Creation order is not used by this test")
        override fun observeById(id: Long) = flowOf(note?.takeIf { it.id == id })
        override suspend fun save(id: Long?, title: String, content: String, options: NoteOptions?): Result<Note> {
            if (title.isBlank() && content.isBlank()) return Result.failure(BlankNoteException())
            saves++
            savedOptions = options
            finishSave.await()
            return Result.success(Note(id ?: 1, title, content, 0, 0))
        }
        override suspend fun delete(id: Long): Result<Note> = error("Editor does not delete notes")
        override suspend fun restore(note: Note): Result<Unit> = error("Undo is not used in this test")
    }
}
