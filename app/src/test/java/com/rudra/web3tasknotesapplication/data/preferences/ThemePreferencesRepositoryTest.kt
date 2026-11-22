package com.rudra.web3tasknotesapplication.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ThemePreferencesRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun defaultsToSystemForMissingOrUnknownPreference() = runTest {
        val file = folder.root.resolve("theme.preferences_pb")
        val store = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
        val repository = ThemePreferencesRepository(store)

        assertEquals(ThemeMode.SYSTEM, repository.mode.first())
        store.edit { it[stringPreferencesKey("theme_mode")] = "UNKNOWN" }
        assertEquals(ThemeMode.SYSTEM, repository.mode.first())
    }

    @Test
    fun restoresEachSavedModeFromDisk() = runTest {
        for (mode in ThemeMode.entries) {
            // FileStorage's atomic replacement of an existing file can fail on Windows.
            val file = folder.root.resolve("${mode.name}.preferences_pb")
            val storeJob = Job(backgroundScope.coroutineContext[Job])
            val storeScope = CoroutineScope(backgroundScope.coroutineContext + storeJob)
            val store = PreferenceDataStoreFactory.create(scope = storeScope) { file }
            ThemePreferencesRepository(store).setMode(mode).getOrThrow()
            storeJob.cancelAndJoin()

            val reopenedStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
            assertEquals(mode, ThemePreferencesRepository(reopenedStore).mode.first())
        }
    }
}
