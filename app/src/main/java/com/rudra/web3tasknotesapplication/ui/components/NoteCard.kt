package com.rudra.web3tasknotesapplication.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.ui.theme.LocalNoteColors
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing
import com.rudra.web3tasknotesapplication.ui.theme.forNote
import java.text.SimpleDateFormat
import java.util.Date

/** Shows the most recent edit and a bounded excerpt without changing the note text. */
@Composable
internal fun NoteCard(note: Note, onClick: () -> Unit, onDelete: (() -> Unit)? = null) {
    val palette = LocalNoteColors.current
    var menuOpen by remember { mutableStateOf(false) }
    val actionsLabel = stringResource(R.string.note_actions)
    Surface(modifier = Modifier.fillMaxWidth(), shape = NotesShapes.Card,
        color = palette.forNote(note.colorId), contentColor = palette.onColor) {
        Box(Modifier.combinedClickable(
            onClick = onClick,
            onLongClickLabel = if (onDelete != null) actionsLabel else null,
            onLongClick = onDelete?.let { { menuOpen = true } },
        )) {
            Column(Modifier.padding(Spacing.Medium), verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                Text(noteDateLabel(note.updatedAt), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(note.title, style = MaterialTheme.typography.titleSmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (note.content.isNotBlank()) {
                    Text(note.content, style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
            if (onDelete != null) {
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.delete_note)) }, onClick = {
                        menuOpen = false
                        onDelete()
                    })
                }
            }
        }
    }
}

@Composable
internal fun noteDateLabel(timestamp: Long): String {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val date = Date(timestamp)
    val day = SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMMd"), locale)
        .format(date).uppercase(locale)
    return stringResource(R.string.note_date_time, day, DateFormat.getTimeFormat(context).format(date))
}
