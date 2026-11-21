package com.rudra.web3tasknotesapplication.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode

@Preview(name = "Light", showSystemUi = true, widthDp = 360, heightDp = 800)
@Preview(name = "Light large text", fontScale = 2f, widthDp = 360, heightDp = 800)
@Composable
private fun LightDesignSystemPreview() {
    DesignSystemSample(themeMode = ThemeMode.LIGHT)
}

@Preview(
    name = "Dark",
    showSystemUi = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Dark large text",
    fontScale = 2f,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun DarkDesignSystemPreview() {
    DesignSystemSample(themeMode = ThemeMode.DARK)
}
