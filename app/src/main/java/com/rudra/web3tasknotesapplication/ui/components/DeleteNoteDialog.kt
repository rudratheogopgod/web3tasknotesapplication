package com.rudra.web3tasknotesapplication.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rudra.web3tasknotesapplication.R

/** Uses the same confirmation language for both delete entry points. */
@Composable
internal fun DeleteNoteDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.confirm_delete_note)) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.delete_note)) } },
        dismissButton = { TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel_action)) } },
    )
}
