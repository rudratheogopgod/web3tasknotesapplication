package com.rudra.web3tasknotesapplication.ui.adaptive

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListScreen
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListUiState
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme

@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Preview(name = "Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Landscape phone", widthDp = 800, heightDp = 360)
@Preview(name = "Small tablet", widthDp = 600, heightDp = 960)
@Preview(name = "Portrait tablet", widthDp = 840, heightDp = 1280)
@Preview(name = "Landscape tablet", widthDp = 1280, heightDp = 800)
@Preview(name = "Desktop", widthDp = 1920, heightDp = 1080)
@Preview(name = "Short split-screen", widthDp = 360, heightDp = 240, fontScale = 2f)
private annotation class WorkspaceSizes

@PreviewScreenSizes
@WorkspaceSizes
@Composable
private fun LightWorkspacePreview() = PreviewWorkspace(ThemeMode.LIGHT)

@WorkspaceSizes
@Composable
private fun DarkWorkspacePreview() = PreviewWorkspace(ThemeMode.DARK)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun PreviewWorkspace(mode: ThemeMode) {
    Web3TaskNotesTheme(themeMode = mode) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val partitions = NotesWindowLayout.paneCount(
                maxWidth >= NotesWindowLayout.TwoPaneMinWidth.dp, false, false,
            )
            val listWidth = NotesWindowLayout.listWidth(maxWidth >= NotesWindowLayout.ExpandedMinWidth.dp)
            val compactHeight = maxHeight < NotesWindowLayout.CompactHeight.dp
            val directive = PaneScaffoldDirective.Default.copy(maxHorizontalPartitions = partitions)
            val navigator = rememberListDetailPaneScaffoldNavigator<String>(scaffoldDirective = directive)
            val note = Note(1, "Weekend plans", "Visit the bookshop", 0, 0)
            NavigableListDetailPaneScaffold(
                navigator = navigator,
                listPane = {
                    AnimatedPane(Modifier.preferredWidth(listWidth)) {
                        NotesListScreen(
                            state = NotesListUiState.Loaded(listOf(note)),
                            onCreateNote = {},
                            onOpenNote = {},
                            compactHeight = compactHeight,
                        )
                    }
                },
                detailPane = {
                    AnimatedPane(Modifier.preferredWidth(NotesWindowLayout.DetailPreferredWidth)) {
                        EmptyDetailPane {}
                    }
                },
            )
        }
    }
}
