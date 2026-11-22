package com.rudra.web3tasknotesapplication.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.ui.theme.LocalNoteColors
import com.rudra.web3tasknotesapplication.ui.theme.NotesShapes
import com.rudra.web3tasknotesapplication.ui.theme.Spacing
import com.rudra.web3tasknotesapplication.ui.theme.forNote
import java.text.SimpleDateFormat
import java.util.Date

private const val PreviewLength = 240

/** Shows an excerpt and provides one accessible target for opening the note or showing its actions. */
@Composable
internal fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onTogglePin: (() -> Unit)? = null,
) {
    val palette = LocalNoteColors.current
    val haptics = LocalHapticFeedback.current
    var menuOpen by remember { mutableStateOf(false) }
    val actionsLabel = stringResource(R.string.note_actions)
    val pinnedLabel = stringResource(R.string.note_pinned_status)
    val date = noteDateLabel(note.updatedAt)
    val preview = remember(note.content) { notePreview(note.content) }
    val summary = if (preview.isBlank()) {
        stringResource(R.string.note_card_summary, note.title, date)
    } else {
        stringResource(R.string.note_card_summary_with_preview, note.title, date, preview)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = NotesShapes.Card,
        color = palette.forNote(note.colorId),
        contentColor = palette.onColor,
    ) {
        Box(
            Modifier.combinedClickable(
                onClick = onClick,
                onLongClickLabel = if (onDelete != null) actionsLabel else null,
                onLongClick = onDelete?.let { { menuOpen = true } },
            ).semantics {
                contentDescription = summary
                if (note.isPinned) stateDescription = pinnedLabel
            },
        ) {
            NoteCardContent(note, date, preview)
            if (onDelete != null) {
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    if (onTogglePin != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(if (note.isPinned) R.string.unpin_note else R.string.pin_note)) },
                            onClick = {
                                menuOpen = false
                                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                onTogglePin()
                            },
                        )
                    }
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
private fun NoteCardContent(note: Note, date: String, preview: String) {
    val spokenByCard = Modifier.semantics { hideFromAccessibility() }
    Column(
        Modifier.padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
    ) {
        Text(date, spokenByCard, style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
            Text(
                note.title, Modifier.weight(1f).then(spokenByCard),
                style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            if (note.isPinned) {
                Icon(painterResource(R.drawable.ic_note_pin), contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
        if (preview.isNotBlank()) {
            Text(
                preview, spokenByCard, style = MaterialTheme.typography.bodyMedium,
                maxLines = 3, overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Bounds both layout work and spoken excerpts while keeping a surrogate pair intact. */
internal fun notePreview(content: String): String {
    if (content.length <= PreviewLength) return content
    val end = if (content[PreviewLength - 1].isHighSurrogate()) PreviewLength - 1 else PreviewLength
    return content.substring(0, end).trimEnd() + "…"
}

/** Formats the timestamp for display without using the user's locale or clock settings for sorting. */
@Composable
internal fun noteDateLabel(timestamp: Long): String {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val date = Date(timestamp)
    val day = SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMMd"), locale)
        .format(date).uppercase(locale)
    return stringResource(R.string.note_date_time, day, DateFormat.getTimeFormat(context).format(date))
}
