package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions

/** Shows whether the current draft will be pinned when saved. */
@Composable
internal fun PinNoteAction(options: NoteOptions, enabled: Boolean, onOptionsChange: (NoteOptions) -> Unit) {
    val haptics = LocalHapticFeedback.current
    IconToggleButton(
        checked = options.isPinned,
        onCheckedChange = {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onOptionsChange(options.copy(isPinned = it))
        },
        enabled = enabled,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primary,
            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Icon(painterResource(R.drawable.ic_note_pin),
            stringResource(if (options.isPinned) R.string.unpin_note else R.string.pin_note))
    }
}
