package com.rudra.web3tasknotesapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesTheme
import com.rudra.web3tasknotesapplication.ui.theme.isDarkTheme

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {}
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
