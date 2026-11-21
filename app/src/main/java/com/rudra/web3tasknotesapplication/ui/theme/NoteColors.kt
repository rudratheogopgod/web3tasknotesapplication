package com.rudra.web3tasknotesapplication.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Pastel card surfaces paired with a single readable foreground in each theme. */
@Immutable
data class NotePalette(
    val default: Color,
    val rose: Color,
    val peach: Color,
    val sand: Color,
    val mint: Color,
    val lavender: Color,
    val onColor: Color,
)

/** Light and dark variants are designed separately to retain readable contrast. */
object NoteColors {
    val Light = NotePalette(
        default = LightCard,
        rose = Color(0xFFF9E1E5),
        peach = Color(0xFFFFE6D5),
        sand = Color(0xFFF5EDCA),
        mint = Color(0xFFDDEEE3),
        lavender = Color(0xFFE9E2F5),
        onColor = LightText,
    )
    val Dark = NotePalette(
        default = DarkCard,
        rose = Color(0xFF40282E),
        peach = Color(0xFF422F25),
        sand = Color(0xFF3C3623),
        mint = Color(0xFF253A2F),
        lavender = Color(0xFF332D43),
        onColor = OffWhite,
    )
}

/** Provides note colors from the effective app theme, including explicit overrides. */
val LocalNoteColors = staticCompositionLocalOf { NoteColors.Light }
