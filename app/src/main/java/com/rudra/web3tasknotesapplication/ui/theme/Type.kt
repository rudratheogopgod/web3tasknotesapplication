package com.rudra.web3tasknotesapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val NotesFontFamily = FontFamily.SansSerif

internal val Typography = Typography(
    displayLarge = notesTextStyle(57, 64),
    displayMedium = notesTextStyle(45, 52),
    displaySmall = notesTextStyle(36, 44),
    headlineLarge = notesTextStyle(32, 40, FontWeight.SemiBold),
    headlineMedium = notesTextStyle(28, 36, FontWeight.SemiBold),
    headlineSmall = notesTextStyle(24, 32, FontWeight.SemiBold),
    titleLarge = notesTextStyle(32, 40, FontWeight.Bold),
    titleMedium = notesTextStyle(20, 28, FontWeight.SemiBold),
    titleSmall = notesTextStyle(16, 24, FontWeight.SemiBold),
    bodyLarge = notesTextStyle(16, 24),
    bodyMedium = notesTextStyle(14, 22),
    bodySmall = notesTextStyle(12, 18),
    labelLarge = notesTextStyle(14, 20, FontWeight.Medium),
    labelMedium = notesTextStyle(12, 16, FontWeight.Medium),
    labelSmall = notesTextStyle(10, 16, FontWeight.Medium, letterSpacing = 0.8f),
)

private fun notesTextStyle(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = NotesFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
)
