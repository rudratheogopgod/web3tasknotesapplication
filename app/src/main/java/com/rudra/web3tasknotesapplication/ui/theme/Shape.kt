package com.rudra.web3tasknotesapplication.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Shared corner treatments keep cards and controls visually consistent. */
object NotesShapes {
    val Card = RoundedCornerShape(20.dp)
    val Sheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val Pill = CircleShape
}

internal val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = NotesShapes.Card,
    extraLarge = RoundedCornerShape(32.dp),
)
