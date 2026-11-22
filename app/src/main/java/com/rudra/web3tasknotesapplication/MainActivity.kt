package com.rudra.web3tasknotesapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme
import com.rudra.web3tasknotesapplication.ui.theme.isDarkTheme

import com.rudra.web3tasknotesapplication.ui.navigation.NotesNavHost
import dagger.hilt.android.AndroidEntryPoint

/** Hosts the Compose app and keeps the window appearance aligned with its theme. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode = ThemeMode.SYSTEM
            val darkTheme = themeMode.isDarkTheme()
            SideEffect { updateSystemBars(darkTheme) }
            Web3TaskNotesTheme(themeMode = themeMode) {
                NotesNavHost()
            }
        }
    }

    private fun updateSystemBars(darkTheme: Boolean) {
        val style = SystemBarStyle.auto(
            lightScrim = android.graphics.Color.TRANSPARENT,
            darkScrim = android.graphics.Color.TRANSPARENT,
        ) { darkTheme }
        enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }
}
