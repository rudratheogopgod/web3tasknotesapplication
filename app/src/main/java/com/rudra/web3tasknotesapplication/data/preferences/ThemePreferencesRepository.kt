package com.rudra.web3tasknotesapplication.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

private val ThemeModeKey = stringPreferencesKey("theme_mode")
private const val ReadRetryDelayMillis = 1_000L

/** Stores the user's override independently of the device's current appearance. */
internal class ThemePreferencesRepository @Inject constructor(
    private val store: DataStore<Preferences>,
) {
    val mode: Flow<ThemeMode> = store.data
        .map { preferences ->
            ThemeMode.entries.find { it.name == preferences[ThemeModeKey] } ?: ThemeMode.SYSTEM
        }
        .retryWhen { error, attempt ->
            if (error !is IOException) return@retryWhen false
            if (attempt == 0L) emit(ThemeMode.SYSTEM)
            delay(ReadRetryDelayMillis)
            true
        }

    /** Reports storage errors so the caller can let the user retry. */
    suspend fun setMode(mode: ThemeMode): Result<Unit> = try {
        store.edit { it[ThemeModeKey] = mode.name }
        Result.success(Unit)
    } catch (error: IOException) {
        Result.failure(error)
    }
}
