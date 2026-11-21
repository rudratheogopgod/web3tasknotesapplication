package com.rudra.web3tasknotesapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rudra.web3tasknotesapplication.ui.theme.Web3TaskNotesApplicationTheme

/** Hosts the Compose app using the project's theme. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Web3TaskNotesApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {}
            }
        }
    }
}
