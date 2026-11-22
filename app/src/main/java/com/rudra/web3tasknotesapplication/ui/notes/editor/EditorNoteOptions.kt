package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
import com.rudra.web3tasknotesapplication.ui.theme.LocalNoteColors
import com.rudra.web3tasknotesapplication.ui.theme.Spacing
import com.rudra.web3tasknotesapplication.ui.theme.forNote

/** Lets the editor choose one category and one color without placing text over color alone. */
@Composable
internal fun EditorNoteOptions(options: NoteOptions, enabled: Boolean, onChange: (NoteOptions) -> Unit) {
    Column {
        Row(
            Modifier.horizontalScroll(rememberScrollState()).selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        ) {
            NoteCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == options.category,
                    onClick = { onChange(options.copy(category = category)) },
                    label = { Text(stringResource(category.labelId)) },
                    enabled = enabled,
                )
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()).selectableGroup()) {
            NoteColor.entries.forEach { color ->
                ColorSwatch(color, color == options.colorId, enabled) {
                    onChange(options.copy(colorId = color))
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: NoteColor, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val description = stringResource(color.labelId)
    val palette = LocalNoteColors.current
    Box(
        modifier = Modifier.size(48.dp)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            color = palette.forNote(color),
            contentColor = palette.onColor,
            shape = CircleShape,
            border = BorderStroke(
                if (selected) 2.dp else 1.dp,
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            ),
        ) {
            if (selected) Box(contentAlignment = Alignment.Center) {
                Surface(Modifier.size(8.dp), color = palette.onColor, shape = CircleShape) {}
            }
        }
    }
}

private val NoteCategory.labelId: Int
    get() = when (this) {
        NoteCategory.QUICK_NOTE -> R.string.category_quick_notes
        NoteCategory.TODO_LIST -> R.string.category_todo
        NoteCategory.JOURNAL -> R.string.category_journal
    }

private val NoteColor.labelId: Int
    get() = when (this) {
        NoteColor.DEFAULT -> R.string.note_color_default
        NoteColor.ROSE -> R.string.note_color_rose
        NoteColor.PEACH -> R.string.note_color_peach
        NoteColor.SAND -> R.string.note_color_sand
        NoteColor.MINT -> R.string.note_color_mint
        NoteColor.LAVENDER -> R.string.note_color_lavender
    }
