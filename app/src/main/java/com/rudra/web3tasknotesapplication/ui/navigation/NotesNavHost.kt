package com.rudra.web3tasknotesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rudra.web3tasknotesapplication.ui.adaptive.NotesWorkspace
import kotlinx.serialization.Serializable

@Serializable
private data object Home

internal const val EDITOR_NOTE_ID = "noteId"

/** Keeps the notes workspace in one destination so resizing never changes the app back stack. */
@Composable
internal fun NotesNavHost(onOpenTheme: () -> Unit) {
    NavHost(navController = rememberNavController(), startDestination = Home) {
        composable<Home> { NotesWorkspace(onOpenTheme = onOpenTheme) }
    }
}
