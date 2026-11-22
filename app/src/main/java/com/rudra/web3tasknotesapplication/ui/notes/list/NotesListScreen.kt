package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            Text(
                text = stringResource(R.string.all_notes),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(Spacing.Large),
            )
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = NotesShapes.Sheet,
                color = MaterialTheme.colorScheme.surface,
            ) {
                NotesListContent(state, onCreateNote, onOpenNote, onRequestDelete)
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
) {
    when (state) {
        NotesListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        NotesListUiState.Empty -> EmptyNotes(onCreateNote)
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
