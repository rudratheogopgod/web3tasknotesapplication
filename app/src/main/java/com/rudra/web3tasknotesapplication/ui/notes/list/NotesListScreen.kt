package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import com.rudra.web3tasknotesapplication.ui.components.NotesSearchBar
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.components.NoteCard
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

private val MinimumCardWidth = 144.dp
private val FabClearance = 72.dp

@Composable
internal fun NotesListScreen(
    state: NotesListUiState,
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    snackbarHostState: SnackbarHostState? = null,
    onRequestDelete: ((Long) -> Unit)? = null,
    query: String = "",
    sortOrder: NoteSortOrder = NoteSortOrder.LAST_UPDATED,
    onQueryChange: (String) -> Unit = {},
    onSortOrderChange: (NoteSortOrder) -> Unit = {},
) {
    val insets = WindowInsets.safeDrawing.union(WindowInsets.captionBar)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = insets,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                shape = NotesShapes.Pill,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(painterResource(R.drawable.ic_add_note), stringResource(R.string.create_note))
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            NotesHeader(sortOrder, onSortOrderChange)
            NotesSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 640.dp)
                    .fillMaxWidth().padding(horizontal = Spacing.Large).padding(bottom = Spacing.Large),
            )
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = NotesShapes.Sheet,
                color = MaterialTheme.colorScheme.surface,
            ) {
                NotesListContent(state, onCreateNote, onOpenNote, onRequestDelete, { onQueryChange("") })
            }
        }
    }
}

@Composable
private fun NotesListContent(
    state: NotesListUiState,
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    onRequestDelete: ((Long) -> Unit)?,
    onClearSearch: () -> Unit,
) {
    when (state) {
        NotesListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        NotesListUiState.Empty -> EmptyNotes(onCreateNote)
        is NotesListUiState.NoResults -> NoResults(state.query, onClearSearch)
        NotesListUiState.Unavailable -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.notes_unavailable), Modifier.padding(Spacing.Large))
        }
        is NotesListUiState.Loaded -> LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(MinimumCardWidth),
            contentPadding = PaddingValues(
                start = Spacing.Large, end = Spacing.Large,
                top = Spacing.Large, bottom = FabClearance,
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalItemSpacing = Spacing.Medium,
        ) {
            items(state.notes, key = { it.id }) { note ->
                NoteCard(note = note, onClick = { onOpenNote(note.id) },
                    onDelete = onRequestDelete?.let { request -> { request(note.id) } })
            }
        }
    }
}

@Composable
private fun NotesHeader(order: NoteSortOrder, onSortOrderChange: (NoteSortOrder) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(start = Spacing.Large, end = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.all_notes), Modifier.weight(1f).padding(vertical = Spacing.Large),
            style = MaterialTheme.typography.titleLarge)
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(painterResource(R.drawable.ic_notes_overflow), stringResource(R.string.sort_notes))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                NoteSortOrder.entries.forEach { option ->
                    val selectedOrder = option == order
                    DropdownMenuItem(
                        text = { Text(stringResource(when (option) {
                            NoteSortOrder.LAST_UPDATED -> R.string.sort_updated
                            NoteSortOrder.TITLE -> R.string.sort_title
                        })) },
                        leadingIcon = { RadioButton(selected = selectedOrder, onClick = null) },
                        modifier = Modifier.semantics { selected = selectedOrder },
                        onClick = {
                            expanded = false
                            onSortOrderChange(option)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NoResults(query: String, onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = Spacing.Large, end = Spacing.Large, top = Spacing.Large, bottom = FabClearance),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Small, Alignment.CenterVertically),
    ) {
        Text(stringResource(R.string.notes_no_results, query), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.notes_search_suggestion), textAlign = TextAlign.Center)
        TextButton(onClick = onClearSearch) { Text(stringResource(R.string.clear_search)) }
    }
}

@Composable
private fun EmptyNotes(onCreateNote: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = Spacing.Large, end = Spacing.Large,
                top = Spacing.ExtraLarge, bottom = FabClearance),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Small, Alignment.CenterVertically),
    ) {
        Icon(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
            modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Text(stringResource(R.string.notes_empty_title), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.notes_empty_body), textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onCreateNote) { Text(stringResource(R.string.create_first_note)) }
    }
}
