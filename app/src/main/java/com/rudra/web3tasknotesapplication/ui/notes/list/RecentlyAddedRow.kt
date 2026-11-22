package com.rudra.web3tasknotesapplication.ui.notes.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.ui.components.NoteCard
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

@Composable
internal fun RecentlyAddedRow(
    notes: List<Note>,
    onOpenNote: (Long) -> Unit,
    onSeeAll: () -> Unit,
    onRequestDelete: ((Long) -> Unit)?,
    onTogglePin: ((Long) -> Unit)?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.recently_added), Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onSeeAll) { Text(stringResource(R.string.see_all_recent)) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
            items(notes, key = { it.id }) { note ->
                Box(Modifier.width(160.dp)) {
                    NoteCard(note, onClick = { onOpenNote(note.id) },
                        onDelete = onRequestDelete?.let { request -> { request(note.id) } },
                        onTogglePin = onTogglePin?.let { toggle -> { toggle(note.id) } })
                }
            }
        }
    }
}

@Composable
internal fun RecentScreenHeader(onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(painterResource(R.drawable.ic_editor_back), stringResource(R.string.navigate_back))
        }
        Text(stringResource(R.string.recently_added), style = MaterialTheme.typography.titleLarge)
    }
}
