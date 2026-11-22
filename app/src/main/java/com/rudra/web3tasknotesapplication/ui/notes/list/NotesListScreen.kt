package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.components.NoteCard
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

private val MinimumCardWidth = 152.dp
private val FabClearance = 72.dp
private val EmptyIllustrationMinHeight = 240.dp

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
    recent: List<Note> = emptyList(),
    onSeeAll: (() -> Unit)? = null,
    onOpenTheme: (() -> Unit)? = null,
    onBackFromRecent: (() -> Unit)? = null,
    category: NoteCategory? = null,
    categoryCounts: Map<NoteCategory, Int> = emptyMap(),
    onCategoryChange: (NoteCategory?) -> Unit = {},
    onTogglePin: ((Long) -> Unit)? = null,
) {
    var browseOpen by rememberSaveable { mutableStateOf(false) }
    val showSections = query.isBlank() && onBackFromRecent == null
    val insets = WindowInsets.safeDrawing.union(WindowInsets.captionBar)
    val gridState = rememberLazyStaggeredGridState()
    val focus = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current
    val dismissKeyboardOnDrag = remember(focus) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput && available != Offset.Zero) focus.clearFocus()
                return Offset.Zero
            }
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = insets,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            if (onBackFromRecent == null) FloatingActionButton(
                onClick = onCreateNote,
                shape = NotesShapes.Pill,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(painterResource(R.drawable.ic_add_note), stringResource(R.string.create_note))
            }
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding.withoutBottom(layoutDirection))
                .consumeWindowInsets(padding).nestedScroll(dismissKeyboardOnDrag),
        ) {
            if (onBackFromRecent != null) {
                RecentScreenHeader(onBackFromRecent)
            } else {
                NotesHeader(sortOrder, onSortOrderChange, onOpenTheme)
                NotesSearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 640.dp)
                        .fillMaxWidth().padding(horizontal = Spacing.Large).padding(bottom = Spacing.Large),
                )
            }
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = NotesShapes.Sheet,
                color = MaterialTheme.colorScheme.surface,
            ) {
                NotesListContent(
                    state, onCreateNote, onOpenNote, onRequestDelete, { onQueryChange("") },
                    recent = if (query.isBlank()) recent else emptyList(), onSeeAll = onSeeAll,
                    recentOnly = onBackFromRecent != null,
                    showSections = showSections, onTogglePin = onTogglePin,
                    gridState = gridState, bottomInset = padding.calculateBottomPadding(),
                    categoryControls = {
                        CategoryControls(category, onCategoryChange, onBrowse = { browseOpen = true })
                    },
                )
            }
        }
    }
    if (browseOpen && showSections && state is NotesListUiState.Loaded) {
        CategoryPicker(category, categoryCounts, onCategoryChange, onDismiss = { browseOpen = false })
    }
}

/** Keeps the sheet edge-to-edge while letting the last item scroll clear of the system bar. */
private fun PaddingValues.withoutBottom(direction: LayoutDirection) = PaddingValues(
    start = calculateStartPadding(direction),
    end = calculateEndPadding(direction),
    top = calculateTopPadding(),
)

@Composable
private fun NotesListContent(
    state: NotesListUiState,
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
    onRequestDelete: ((Long) -> Unit)?,
    onClearSearch: () -> Unit,
    recent: List<Note>,
    onSeeAll: (() -> Unit)?,
    recentOnly: Boolean,
    showSections: Boolean,
    onTogglePin: ((Long) -> Unit)?,
    gridState: LazyStaggeredGridState,
    bottomInset: Dp,
    categoryControls: @Composable () -> Unit,
) {
    val bottomPadding = bottomInset + if (recentOnly) Spacing.Large else FabClearance
    when (state) {
        NotesListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        NotesListUiState.Empty -> if (recentOnly) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.notes_empty_title))
            }
        } else EmptyNotes(onCreateNote, bottomPadding)
        is NotesListUiState.NoResults -> NoResults(state.query, onClearSearch, bottomPadding)
        NotesListUiState.Unavailable -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.notes_unavailable), Modifier.padding(Spacing.Large))
        }
        is NotesListUiState.Loaded -> LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(MinimumCardWidth),
            state = gridState,
            contentPadding = PaddingValues(
                start = Spacing.Large, end = Spacing.Large,
                top = Spacing.Large, bottom = bottomPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalItemSpacing = Spacing.Medium,
        ) {
            if (recent.isNotEmpty() && onSeeAll != null) {
                item(key = "recently-added", span = StaggeredGridItemSpan.FullLine) {
                    RecentlyAddedRow(recent, onOpenNote, onSeeAll, onRequestDelete, onTogglePin)
                }
            }
            if (showSections) {
                item(key = "categories", span = StaggeredGridItemSpan.FullLine) { categoryControls() }
            }
            val pinned = if (showSections) state.notes.filter(Note::isPinned) else emptyList()
            val remaining = if (showSections) state.notes.filterNot(Note::isPinned) else state.notes
            if (pinned.isNotEmpty()) {
                item(key = "pinned", span = StaggeredGridItemSpan.FullLine) {
                    Text(stringResource(R.string.notes_pinned), Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleMedium)
                }
            }
            if (state.notes.isEmpty()) {
                item(key = "category-empty", span = StaggeredGridItemSpan.FullLine) {
                    Text(stringResource(R.string.category_empty), Modifier.padding(vertical = Spacing.Medium))
                }
            }
            noteItems(pinned, onOpenNote, onRequestDelete, onTogglePin)
            if (pinned.isNotEmpty() && remaining.isNotEmpty()) {
                item(key = "pinned-end", span = StaggeredGridItemSpan.FullLine) { Spacer(Modifier.height(1.dp)) }
            }
            noteItems(remaining, onOpenNote, onRequestDelete, onTogglePin)
        }
    }
}

private fun LazyStaggeredGridScope.noteItems(
    notes: List<Note>,
    onOpenNote: (Long) -> Unit,
    onRequestDelete: ((Long) -> Unit)?,
    onTogglePin: ((Long) -> Unit)?,
) {
    items(notes, key = { it.id }) { note ->
        NoteCard(note = note, onClick = { onOpenNote(note.id) },
            onDelete = onRequestDelete?.let { request -> { request(note.id) } },
            onTogglePin = onTogglePin?.let { toggle -> { toggle(note.id) } })
    }
}

@Composable
private fun NotesHeader(
    order: NoteSortOrder,
    onSortOrderChange: (NoteSortOrder) -> Unit,
    onOpenTheme: (() -> Unit)?,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(start = Spacing.Large, end = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.all_notes),
            Modifier.weight(1f).padding(vertical = Spacing.Large).semantics { heading() },
            style = MaterialTheme.typography.titleLarge)
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(painterResource(R.drawable.ic_notes_overflow), stringResource(R.string.notes_menu))
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
                if (onOpenTheme != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.theme_picker)) },
                        onClick = {
                            expanded = false
                            onOpenTheme()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NoResults(query: String, onClearSearch: () -> Unit, bottomPadding: Dp) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = Spacing.Large, end = Spacing.Large, top = Spacing.Large, bottom = bottomPadding),
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
private fun EmptyNotes(onCreateNote: () -> Unit, bottomPadding: Dp) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val showIllustration = maxHeight - bottomPadding >= EmptyIllustrationMinHeight
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = Spacing.Large, end = Spacing.Large,
                    top = Spacing.ExtraLarge, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Small, Alignment.CenterVertically),
        ) {
            if (showIllustration) {
                Icon(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
                    modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(stringResource(R.string.notes_empty_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.notes_empty_body), textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onCreateNote) { Text(stringResource(R.string.create_first_note)) }
        }
    }
}
