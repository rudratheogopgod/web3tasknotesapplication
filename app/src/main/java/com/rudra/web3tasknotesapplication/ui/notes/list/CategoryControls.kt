package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.ui.adaptive.NotesWindowLayout
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

private val CategoryOptions = listOf(null) + NoteCategory.entries

/** Keeps categories keyboard- and TalkBack-selectable even when the row must scroll. */
@Composable
internal fun CategoryControls(
    selected: NoteCategory?,
    onSelect: (NoteCategory?) -> Unit,
    onBrowse: () -> Unit,
) {
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.notes_categories), Modifier.weight(1f).semantics { heading() },
                style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onBrowse) { Text(stringResource(R.string.browse_categories)) }
        }
        Surface(
            modifier = Modifier.widthIn(max = NotesWindowLayout.SegmentsMaxWidth).fillMaxWidth(),
            shape = NotesShapes.Pill,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Row(Modifier.horizontalScroll(rememberScrollState()).selectableGroup().padding(Spacing.ExtraSmall)) {
                CategoryOptions.forEach { option ->
                    val active = option == selected
                    Surface(
                        shape = NotesShapes.Pill,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    ) {
                        Box(Modifier.selectable(active, role = Role.RadioButton, onClick = { onSelect(option) })
                            .heightIn(min = 48.dp).padding(horizontal = Spacing.Medium),
                            contentAlignment = Alignment.Center) {
                            Text(stringResource(categoryLabel(option)), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

/** Owns the sheet outside the lazy grid so its lifetime does not depend on the category row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryPicker(
    selected: NoteCategory?,
    counts: Map<NoteCategory, Int>,
    onSelect: (NoteCategory?) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetMaxWidth = NotesWindowLayout.SheetMaxWidth) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).selectableGroup().padding(bottom = Spacing.Large),
        ) {
            Text(
                stringResource(R.string.notes_categories), Modifier.padding(Spacing.Large).semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
            )
            CategoryOptions.forEach { option ->
                ListItem(
                    trailingContent = { Text((option?.let { counts[it] ?: 0 } ?: counts.values.sum()).toString()) },
                    modifier = Modifier.selectable(option == selected, role = Role.RadioButton) {
                        onSelect(option)
                        onDismiss()
                    },
                ) { Text(stringResource(categoryLabel(option))) }
            }
        }
    }
}

private fun categoryLabel(category: NoteCategory?): Int = when (category) {
    null -> R.string.category_all
    NoteCategory.QUICK_NOTE -> R.string.category_quick_notes
    NoteCategory.TODO_LIST -> R.string.category_todo
    NoteCategory.JOURNAL -> R.string.category_journal
}
