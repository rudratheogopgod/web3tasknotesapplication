package com.rudra.web3tasknotesapplication.ui.notes.list

import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun transitionsFromLoadingToEmptyToLoadedAndBack() = runTest(dispatcher) {
        val repository = FakeRepository()
        val viewModel = NotesListViewModel(repository, SavedStateHandle())
        assertEquals(NotesListUiState.Loading, viewModel.uiState.value)
        backgroundScope.launch(dispatcher) { viewModel.uiState.collect {} }
        testScheduler.runCurrent()
        assertEquals(NotesListUiState.Empty, viewModel.uiState.value)

        val notes = listOf(Note(2, "Newest", "Preview", 1, 3), Note(1, "Older", "", 1, 2))
        repository.notes.value = notes
        testScheduler.runCurrent()
        assertEquals(NotesListUiState.Loaded(notes), viewModel.uiState.value)

        repository.notes.value = emptyList()
        testScheduler.runCurrent()
        assertEquals(NotesListUiState.Empty, viewModel.uiState.value)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun undoRestoresTheExactSnapshotOnceAndRoomDrivesTheList() = runTest(dispatcher) {
        val original = Note(9, "Draft", "Original text", 10, 42)
        val repository = FakeRepository().apply { notes.value = listOf(original) }
        val model = NotesListViewModel(repository, SavedStateHandle())
        backgroundScope.launch(dispatcher) { model.uiState.collect {} }
        testScheduler.runCurrent()

        model.deleteNote(original.id)
        model.deleteNote(original.id)
        testScheduler.runCurrent()
        assertEquals(1, repository.deleteCalls)
        assertEquals(NotesListUiState.Empty, model.uiState.value)
        val message = model.deleteMessages.value.single() as DeleteMessage.Deleted
        assertEquals(original, message.note)

        model.finishDeleteMessage(message, undo = true)
        model.finishDeleteMessage(message, undo = true)
        testScheduler.runCurrent()
        assertEquals(1, repository.restoreCalls)
        assertEquals(NotesListUiState.Loaded(listOf(original)), model.uiState.value)
        assertEquals(emptyList<DeleteMessage>(), model.deleteMessages.value)
        model.viewModelScope.cancel()
    }

    @Test
    fun onlyQueriesTheLatestTermAfterDebounceAndResortsImmediately() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = NotesListViewModel(repository, SavedStateHandle())
        backgroundScope.launch(dispatcher) { model.uiState.collect {} }
        testScheduler.runCurrent()
        model.changeQuery("old")
        testScheduler.runCurrent()
        testScheduler.advanceTimeBy(200)
        model.changeQuery("  newest  ")
        testScheduler.runCurrent()
        testScheduler.advanceTimeBy(249)
        testScheduler.runCurrent()
        assertEquals(listOf("" to NoteSortOrder.LAST_UPDATED), repository.queries)
        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()
        assertEquals("newest" to NoteSortOrder.LAST_UPDATED, repository.queries.last())
        assertEquals(NotesListUiState.NoResults("newest"), model.uiState.value)

        model.changeSortOrder(NoteSortOrder.TITLE)
        testScheduler.runCurrent()
        assertEquals("newest" to NoteSortOrder.TITLE, repository.queries.last())
        model.changeQuery(" ")
        testScheduler.runCurrent()
        assertEquals("" to NoteSortOrder.TITLE, repository.queries.last())
        assertEquals(NotesListUiState.Empty, model.uiState.value)
        model.viewModelScope.cancel()
    }

    private class FakeRepository : NotesRepository {
        val notes = MutableStateFlow(emptyList<Note>())
        var deleteCalls = 0
        var restoreCalls = 0
        val queries = mutableListOf<Pair<String, NoteSortOrder>>()
        override fun observeAll(query: String, order: NoteSortOrder): MutableStateFlow<List<Note>> {
            queries += query to order
            return notes
        }
        override fun observeById(id: Long) = error("Only list observation is used")
        override suspend fun save(id: Long?, title: String, content: String): Result<Note> =
            error("Only list observation is used")
        override suspend fun delete(id: Long): Result<Note> {
            deleteCalls++
            val note = notes.value.first { it.id == id }
            notes.value = notes.value.filterNot { it.id == id }
            return Result.success(note)
        }

        override suspend fun restore(note: Note): Result<Unit> {
            restoreCalls++
            notes.value = notes.value + note
            return Result.success(Unit)
        }
    }
}
