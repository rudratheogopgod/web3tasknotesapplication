package com.rudra.web3tasknotesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rudra.web3tasknotesapplication.ui.notes.list.NotesListRoute
import kotlinx.serialization.Serializable

@Serializable
internal data object Home

/** Hosts the typed destinations for the notes flow. */
@Composable
internal fun NotesNavHost() {
    NavHost(navController = rememberNavController(), startDestination = Home) {
        composable<Home> { NotesListRoute(onCreateNote = {}, onOpenNote = {}) }
    }
}
