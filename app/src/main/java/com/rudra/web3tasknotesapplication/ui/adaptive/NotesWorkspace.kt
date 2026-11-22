package com.rudra.web3tasknotesapplication.ui.adaptive

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.allHorizontalHingeBounds
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.navigation.EDITOR_NOTE_ID
import com.rudra.web3tasknotesapplication.ui.notes.editor.EditorEffect
import com.rudra.web3tasknotesapplication.ui.notes.editor.NoteEditorRoute
import com.rudra.web3tasknotesapplication.ui.notes.editor.NoteEditorViewModel
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListRoute
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListViewModel
import com.rudra.web3tasknotesapplication.ui.theme.Spacing
import kotlinx.coroutines.launch

private const val NewNote = "new"
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private val ReturnToList = BackNavigationBehavior.PopUntilCurrentDestinationChange

/** Shares list and editor state across pane changes and serializes navigation with the draft prompt. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun NotesWorkspace(
    onOpenTheme: () -> Unit,
    listViewModel: NotesListViewModel = hiltViewModel(),
) {
    val info = currentWindowAdaptiveInfoV2()
    val directive = notesWindowDirective(info)
    val tabletopHinge = if (info.windowPosture.isTabletop) {
        info.windowPosture.allHorizontalHingeBounds.firstOrNull()
    } else null
    val density = LocalDensity.current
    val maxListHeight = tabletopHinge?.let { with(density) { it.top.toDp() } }
    val navigator = rememberListDetailPaneScaffoldNavigator<String>(scaffoldDirective = directive)
    val selection = navigator.currentDestination?.contentKey
    val noteId = selection?.toLongOrNull()
    val editor = if (selection != null) key(selection) {
        val args = remember(noteId) {
            Bundle().apply { noteId?.let { putLong(EDITOR_NOTE_ID, it) } }
        }
        val owner = rememberViewModelStoreOwner(defaultArgs = args)
        hiltViewModel<NoteEditorViewModel>(owner, key = selection)
    } else null
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val workspaceFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { workspaceFocus.requestFocus() }
    var focusSearchRequest by remember { mutableIntStateOf(0) }
    val listState = rememberSaveableStateHolder()
    var pendingSelection by rememberSaveable { mutableStateOf<String?>(null) }
    var recent by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf<String?>(null) }
    var navigating by remember { mutableStateOf(false) }

    fun navigate(next: String?) {
        if (navigating) return
        navigating = true
        scope.launch {
            try {
                if (next == null) navigator.navigateBack(ReturnToList)
                else navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, next)
            } finally {
                navigating = false
            }
        }
    }

    fun select(next: String) {
        if (navigating || selection == next || editor?.state?.value?.isSaving == true) return
        if (editor?.state?.value?.hasUnsavedChanges == true) {
            pendingSelection = next
            editor.requestBack()
        } else {
            navigate(next)
        }
    }

    val dualPane = directive.maxHorizontalPartitions > 1
    val listVisible = navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] != PaneAdaptedValue.Hidden
    val compactHeight = !info.windowSizeClass.isHeightAtLeastBreakpoint(NotesWindowLayout.CompactHeight)
    val listWidth = NotesWindowLayout.listWidth(
        info.windowSizeClass.isWidthAtLeastBreakpoint(NotesWindowLayout.ExpandedMinWidth),
    )
    var wasDualPane by rememberSaveable { mutableStateOf(dualPane) }
    LaunchedEffect(dualPane, editor) {
        if (wasDualPane && !dualPane &&
            editor?.state?.value?.hasUnsavedChanges == true
        ) {
            editor.requestBack()
        }
        wasDualPane = dualPane
    }
    val message = when (result) {
        EditorEffect.SAVED.name -> stringResource(R.string.note_saved)
        EditorEffect.NOT_FOUND.name -> stringResource(R.string.note_not_found)
        else -> null
    }
    LaunchedEffect(message) {
        if (message != null) {
            snackbar.showSnackbar(message)
            result = null
        }
    }
    BackHandler(enabled = recent && selection == null) { recent = false }
    val query by listViewModel.query.collectAsStateWithLifecycle()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        defaultBackBehavior = ReturnToList,
        modifier = Modifier.fillMaxSize().focusRequester(workspaceFocus).onPreviewKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            when {
                event.isCtrlPressed && event.key == Key.N -> { select(NewNote); true }
                event.isCtrlPressed && event.key == Key.S && editor != null -> { editor.save(); true }
                event.isCtrlPressed && event.key == Key.F && listVisible && !recent -> {
                    focusSearchRequest++
                    true
                }
                event.key == Key.Escape && listVisible && query.isNotEmpty() -> {
                    listViewModel.changeQuery("")
                    true
                }
                else -> false
            }
        }.focusable(),
        listPane = {
            AnimatedPane(Modifier.preferredWidth(listWidth)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    val listModifier = if (maxListHeight == null) Modifier.fillMaxSize()
                    else Modifier.fillMaxWidth().heightIn(max = maxListHeight)
                    Box(listModifier) {
                        listState.SaveableStateProvider(recent) {
                            NotesListRoute(
                                onCreateNote = { select(NewNote) },
                                onOpenNote = { select(it.toString()) },
                                viewModel = listViewModel,
                                snackbarHostState = snackbar,
                                onSeeAll = { recent = true },
                                onOpenTheme = onOpenTheme,
                                onBackFromRecent = if (recent) ({ recent = false }) else null,
                                selectedNoteId = noteId,
                                focusSearchRequest = focusSearchRequest,
                                compactHeight = compactHeight,
                                onNoteDeleted = { if (it == noteId) navigate(null) },
                            )
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane(Modifier.preferredWidth(NotesWindowLayout.DetailPreferredWidth)) {
                if (editor == null) {
                    EmptyDetailPane { select(NewNote) }
                } else {
                    key(selection) {
                        NoteEditorRoute(
                            viewModel = editor,
                            showBack = !dualPane,
                            compactHeight = compactHeight,
                            tabletopHinge = tabletopHinge,
                            onKeepEditing = { pendingSelection = null },
                            onDelete = { id ->
                                pendingSelection = null
                                listViewModel.deleteNote(id)
                                navigate(null)
                            },
                            onResult = { effect ->
                                if (effect != EditorEffect.CLOSE) result = effect.name
                                val next = pendingSelection
                                pendingSelection = null
                                navigate(next)
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
internal fun EmptyDetailPane(onCreate: () -> Unit) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(Spacing.Large),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(Spacing.DoubleExtraLarge),
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            stringResource(R.string.select_or_create_note),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        Button(onClick = onCreate) { Text(stringResource(R.string.create_note)) }
    }
}
