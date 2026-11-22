package com.rudra.web3tasknotesapplication.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.web3tasknotesapplication.data.preferences.ThemePreferencesRepository
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Keeps the resolved preference available across window configuration changes. */
@HiltViewModel
internal class ThemeViewModel @Inject constructor(
    private val repository: ThemePreferencesRepository,
) : ViewModel() {
    val mode: StateFlow<ThemeMode?> = repository.mode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val failures = Channel<Unit>(Channel.CONFLATED)
    val saveFailures = failures.receiveAsFlow()

    /** Publishes selection changes only after DataStore accepts them. */
    fun selectMode(mode: ThemeMode) {
        viewModelScope.launch {
            if (repository.setMode(mode).isFailure) failures.trySend(Unit)
        }
    }
}
