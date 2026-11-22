package com.rudra.web3tasknotesapplication.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
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
import java.text.SimpleDateFormat
import java.util.Date

/** Shows the most recent edit and a bounded excerpt without changing the note text. */
@Composable
internal fun NoteCard(note: Note, onClick: () -> Unit) {
    val palette = LocalNoteColors.current
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = NotesShapes.Card,
        color = palette.default, contentColor = palette.onColor) {
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
    }
}

@Composable
private fun noteDateLabel(timestamp: Long): String {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val date = Date(timestamp)
    val day = SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMMd"), locale)
        .format(date).uppercase(locale)
    return stringResource(R.string.note_date_time, day, DateFormat.getTimeFormat(context).format(date))
}
