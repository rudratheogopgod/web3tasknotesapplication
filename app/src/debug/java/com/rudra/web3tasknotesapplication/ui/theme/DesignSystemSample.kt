package com.rudra.web3tasknotesapplication.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.ThemeMode

/** A debug-only sample keeps theme review separate from app features. */
@Composable
internal fun DesignSystemSample(themeMode: ThemeMode) {
    Web3TaskNotesTheme(themeMode = themeMode) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().safeDrawingPadding()
                    .verticalScroll(rememberScrollState()).padding(Spacing.ExtraLarge),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large),
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                DesignSystemSheet()
            }
        }
    }
}

@Composable
private fun DesignSystemSheet() {
    Surface(shape = NotesShapes.Sheet, color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.DoubleExtraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
        ) {
            Text(
                stringResource(R.string.preview_design_system),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                stringResource(R.string.preview_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AccentSample()
            Text(stringResource(R.string.preview_label), style = MaterialTheme.typography.labelSmall)
            NotePaletteSample()
            ErrorSample()
        }
    }
}

@Composable
private fun AccentSample() {
    Surface(shape = NotesShapes.Pill, color = MaterialTheme.colorScheme.primary) {
        Text(
            stringResource(R.string.preview_accent),
            modifier = Modifier.padding(Spacing.Large),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotePaletteSample() {
    val palette = LocalNoteColors.current
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        listOf(
            palette.default, palette.rose, palette.peach,
            palette.sand, palette.mint, palette.lavender,
        ).forEachIndexed { index, color ->
            Surface(shape = NotesShapes.Card, color = color, contentColor = palette.onColor) {
                Text(
                    text = (index + 1).toString(),
                    modifier = Modifier.padding(
                        horizontal = Spacing.ExtraLarge,
                        vertical = Spacing.Large,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun ErrorSample() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Text(
            stringResource(R.string.preview_body),
            modifier = Modifier.padding(Spacing.ExtraSmall),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
