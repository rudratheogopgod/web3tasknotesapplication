package com.rudra.web3tasknotesapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode

internal val LightColorScheme = lightColorScheme(
    primary = Charcoal,
    onPrimary = White,
    primaryContainer = LightCard,
    onPrimaryContainer = LightText,
    inversePrimary = OffWhite,
    secondary = LightSecondaryText,
    onSecondary = White,
    secondaryContainer = LightCard,
    onSecondaryContainer = LightText,
    tertiary = Charcoal,
    onTertiary = White,
    tertiaryContainer = LightCard,
    onTertiaryContainer = LightText,
    background = OffWhite,
    onBackground = LightText,
    surface = White,
    onSurface = LightText,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightSecondaryText,
    surfaceTint = Charcoal,
    inverseSurface = DarkCard,
    inverseOnSurface = OffWhite,
    error = LightError,
    onError = White,
    errorContainer = LightErrorContainer,
    onErrorContainer = OnLightErrorContainer,
    outline = LightOutline,
    outlineVariant = LightDivider,
    scrim = Black,
    surfaceBright = White,
    surfaceDim = LightDivider,
    surfaceContainerLowest = White,
    surfaceContainerLow = OffWhite,
    surfaceContainer = LightCard,
    surfaceContainerHigh = LightCard,
    surfaceContainerHighest = LightDivider,
    primaryFixed = OffWhite,
    primaryFixedDim = LightDivider,
    onPrimaryFixed = LightText,
    onPrimaryFixedVariant = LightSecondaryText,
    secondaryFixed = LightCard,
    secondaryFixedDim = LightDivider,
    onSecondaryFixed = LightText,
    onSecondaryFixedVariant = LightSecondaryText,
    tertiaryFixed = OffWhite,
    tertiaryFixedDim = LightDivider,
    onTertiaryFixed = LightText,
    onTertiaryFixedVariant = LightSecondaryText,
)

internal val DarkColorScheme = darkColorScheme(
    primary = OffWhite,
    onPrimary = Charcoal,
    primaryContainer = DarkCard,
    onPrimaryContainer = OffWhite,
    inversePrimary = Charcoal,
    secondary = DarkSecondaryText,
    onSecondary = Charcoal,
    secondaryContainer = DarkCard,
    onSecondaryContainer = OffWhite,
    tertiary = OffWhite,
    onTertiary = Charcoal,
    tertiaryContainer = DarkCard,
    onTertiaryContainer = OffWhite,
    background = DarkBackground,
    onBackground = OffWhite,
    surface = DarkSurface,
    onSurface = OffWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkSecondaryText,
    surfaceTint = OffWhite,
    inverseSurface = OffWhite,
    inverseOnSurface = LightText,
    error = DarkError,
    onError = OnLightErrorContainer,
    errorContainer = DarkErrorContainer,
    onErrorContainer = OnDarkErrorContainer,
    outline = DarkOutline,
    outlineVariant = DarkDivider,
    scrim = Black,
    surfaceBright = DarkDivider,
    surfaceDim = DarkBackground,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCard,
    surfaceContainerHighest = DarkDivider,
    primaryFixed = OffWhite,
    primaryFixedDim = LightDivider,
    onPrimaryFixed = LightText,
    onPrimaryFixedVariant = LightSecondaryText,
    secondaryFixed = LightCard,
    secondaryFixedDim = LightDivider,
    onSecondaryFixed = LightText,
    onSecondaryFixedVariant = LightSecondaryText,
    tertiaryFixed = OffWhite,
    tertiaryFixedDim = LightDivider,
    onTertiaryFixed = LightText,
    onTertiaryFixedVariant = LightSecondaryText,
)

/** Resolves overrides against the system at composition time, so changes recompose. */
@Composable
fun ThemeMode.isDarkTheme(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

/** Applies the branded palette without wallpaper-based dynamic color. */
@Composable
fun Web3TaskNotesTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themeMode.isDarkTheme()
    CompositionLocalProvider(
        LocalNoteColors provides if (darkTheme) NoteColors.Dark else NoteColors.Light,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
