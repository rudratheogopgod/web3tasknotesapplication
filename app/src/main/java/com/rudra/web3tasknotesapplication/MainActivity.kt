package com.rudra.web3tasknotesapplication

import android.app.UiModeManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toDrawable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.navigation.NotesNavHost
import com.rudra.web3tasknotesapplication.ui.theme.ThemePicker
import com.rudra.web3tasknotesapplication.ui.theme.ThemeViewModel
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme
import com.rudra.web3tasknotesapplication.ui.theme.isDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Hosts the Compose app and keeps the window appearance aligned with its theme. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var themeReady = false
        splash.setKeepOnScreenCondition { !themeReady }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeViewModel.saveFailures.collect {
                    Toast.makeText(this@MainActivity, R.string.theme_save_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            val initialMode = themeViewModel.mode.filterNotNull().first()
            val night = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            updateSystemBars(initialMode == ThemeMode.DARK ||
                (initialMode == ThemeMode.SYSTEM && night == Configuration.UI_MODE_NIGHT_YES))
            setContent { NotesApp(initialMode) }
            themeReady = true
        }
    }

    @Composable
    private fun NotesApp(initialMode: ThemeMode) {
        val savedMode by themeViewModel.mode.collectAsStateWithLifecycle()
        val mode = savedMode ?: initialMode
        val darkTheme = mode.isDarkTheme()
        LaunchedEffect(mode) { syncLaunchTheme(mode) }
        SideEffect { updateSystemBars(darkTheme) }
        Web3TaskNotesTheme(themeMode = mode) {
            var pickerOpen by rememberSaveable { mutableStateOf(false) }
            NotesNavHost(onOpenTheme = { pickerOpen = true })
            if (pickerOpen) {
                ThemePicker(
                    mode = mode,
                    onSelect = {
                        pickerOpen = false
                        themeViewModel.selectMode(it)
                    },
                    onDismiss = { pickerOpen = false },
                )
            }
        }
    }

    private fun syncLaunchTheme(mode: ThemeMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val platformMode = when (mode) {
                ThemeMode.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
                ThemeMode.LIGHT -> UiModeManager.MODE_NIGHT_NO
                ThemeMode.DARK -> UiModeManager.MODE_NIGHT_YES
            }
            getSystemService(UiModeManager::class.java).setApplicationNightMode(platformMode)
        }
    }

    private fun updateSystemBars(darkTheme: Boolean) {
        val statusStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
        val navigationScrim = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) Color.BLACK else Color.TRANSPARENT
        val navigationStyle = SystemBarStyle.auto(Color.TRANSPARENT, navigationScrim) { darkTheme }
        enableEdgeToEdge(statusBarStyle = statusStyle, navigationBarStyle = navigationStyle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val themeConfig = Configuration(resources.configuration).apply {
            val appearance = if (darkTheme) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or appearance
        }
        val themeResources = createConfigurationContext(themeConfig)
        window.setBackgroundDrawable(themeResources.getColor(R.color.app_background).toDrawable())
    }
}
