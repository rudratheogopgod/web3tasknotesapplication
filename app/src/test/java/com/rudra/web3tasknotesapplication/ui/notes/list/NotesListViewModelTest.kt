package com.rudra.web3tasknotesapplication.ui.notes.list

import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import androidx.lifecycle.viewModelScope
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
        val viewModel = NotesListViewModel(repository)
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

    private class FakeRepository : NotesRepository {
        val notes = MutableStateFlow(emptyList<Note>())
        override fun observeAll() = notes
        override fun observeById(id: Long) = error("Only list observation is used")
        override suspend fun save(id: Long?, title: String, content: String): Result<Note> =
            error("Only list observation is used")
        override suspend fun delete(note: Note): Result<Unit> = error("Only list observation is used")
    }
}
