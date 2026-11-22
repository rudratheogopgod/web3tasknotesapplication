package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryControls(
    selected: NoteCategory?,
    counts: Map<NoteCategory, Int>,
    onSelect: (NoteCategory?) -> Unit,
    browseOpen: Boolean,
    onBrowseChange: (Boolean) -> Unit,
) {
    val options = listOf(null) + NoteCategory.entries
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.notes_categories), Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { onBrowseChange(true) }) { Text(stringResource(R.string.browse_categories)) }
        }
        Surface(shape = NotesShapes.Pill, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Row(Modifier.horizontalScroll(rememberScrollState()).selectableGroup().padding(Spacing.ExtraSmall)) {
                options.forEach { option ->
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
    if (browseOpen) {
        ModalBottomSheet(onDismissRequest = { onBrowseChange(false) }) {
            Column(Modifier.padding(bottom = Spacing.Large)) {
                Text(stringResource(R.string.notes_categories), Modifier.padding(Spacing.Large),
                    style = MaterialTheme.typography.titleMedium)
                options.forEach { option ->
                    ListItem(
                        trailingContent = { Text((option?.let { counts[it] ?: 0 } ?: counts.values.sum()).toString()) },
                        modifier = Modifier.selectable(option == selected, role = Role.RadioButton) {
                            onSelect(option)
                            onBrowseChange(false)
                        },
                    ) { Text(stringResource(categoryLabel(option))) }
                }
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
