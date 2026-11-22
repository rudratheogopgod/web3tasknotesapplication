package com.rudra.web3tasknotesapplication.ui.notes.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
import com.rudra.web3tasknotesapplication.ui.components.noteDateLabel
import com.rudra.web3tasknotesapplication.ui.theme.Spacing

private val EditorMaxWidth = 720.dp
private val CompactEditorHeight = 320.dp
private const val CompactBodyMaxLines = 6

/** Keeps the editor actions visible while its fields scroll in short windows. */
@Composable
internal fun NoteEditorScreen(
    state: NoteEditorUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit,
    onRequestDelete: (() -> Unit)? = null,
    onOptionsChange: (NoteOptions) -> Unit,
) {
    val fieldsEnabled = !state.isLoading && !state.isSaving && state.error != EditorError.LOAD_FAILED
    val keyboard = LocalSoftwareKeyboardController.current
    val compactHeight = CompactEditorHeight * LocalDensity.current.fontScale.coerceAtLeast(1f)
    val systemInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
    Scaffold(
        contentWindowInsets = systemInsets,
        topBar = {
            EditorToolbar(
                state = state,
                insets = systemInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                fieldsEnabled = fieldsEnabled,
                onBack = {
                    keyboard?.hide()
                    onBack()
                },
                onSave = onSave,
                onRequestDelete = onRequestDelete,
                onOptionsChange = onOptionsChange,
            )
        },
    ) { padding ->
        BoxWithConstraints(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error == EditorError.LOAD_FAILED -> Text(
                    stringResource(R.string.note_load_failed), Modifier.padding(Spacing.Large),
                )
                else -> EditorFields(state, onTitleChange, onContentChange, fieldsEnabled,
                    compact = maxHeight < compactHeight, onOptionsChange = onOptionsChange)
            }
        }
    }
    if (state.showDiscardDialog) {
        AlertDialog(
            onDismissRequest = onKeepEditing,
            title = { Text(stringResource(R.string.discard_changes)) },
            confirmButton = {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onKeepEditing) { Text(stringResource(R.string.keep_editing)) }
                    TextButton(onClick = onDiscard) { Text(stringResource(R.string.discard_note)) }
                    TextButton(onClick = onSave) { Text(stringResource(R.string.save_note)) }
                }
            },
        )
    }
}

@Composable
private fun EditorToolbar(
    state: NoteEditorUiState,
    insets: WindowInsets,
    fieldsEnabled: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onRequestDelete: (() -> Unit)?,
    onOptionsChange: (NoteOptions) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().windowInsetsPadding(insets).padding(vertical = Spacing.ExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack, enabled = !state.isSaving) {
            Icon(painterResource(R.drawable.ic_editor_back), stringResource(R.string.navigate_back))
        }
        Text(
            stringResource(if (state.isNewNote) R.string.new_note else R.string.edit_note),
            Modifier.weight(1f).semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
        )
        PinNoteAction(state.options, fieldsEnabled, onOptionsChange)
        if (!state.isNewNote && onRequestDelete != null) {
            IconButton(onClick = onRequestDelete, enabled = fieldsEnabled) {
                Icon(painterResource(R.drawable.ic_delete_note), stringResource(R.string.delete_note))
            }
        }
        TextButton(onClick = onSave, enabled = fieldsEnabled) {
            Text(stringResource(if (state.isSaving) R.string.saving_note else R.string.save_note))
        }
    }
}

@Composable
private fun EditorFields(
    state: NoteEditorUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    enabled: Boolean,
    compact: Boolean,
    onOptionsChange: (NoteOptions) -> Unit,
) {
    val titleFocus = remember { FocusRequester() }
    val bodyFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var focusedNewNote by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.isNewNote, enabled) {
        if (state.isNewNote && enabled && !focusedNewNote) {
            titleFocus.requestFocus()
            keyboard?.show()
            focusedNewNote = true
        }
    }
    val scroll = rememberScrollState()
    val form = Modifier.widthIn(max = EditorMaxWidth).fillMaxSize()
    Column(
        (if (compact) form.verticalScroll(scroll) else form).padding(Spacing.Large),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        state.error?.let {
            val message = if (it == EditorError.BLANK) R.string.note_blank else R.string.note_save_failed
            Text(stringResource(message), color = MaterialTheme.colorScheme.error)
        }
        OutlinedTextField(
            value = state.title, onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth().focusRequester(titleFocus),
            label = { Text(stringResource(R.string.note_title)) },
            textStyle = MaterialTheme.typography.titleMedium,
            singleLine = true, enabled = enabled,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { bodyFocus.requestFocus() }),
        )
        state.updatedAt?.let {
            Text(stringResource(R.string.note_last_edited, noteDateLabel(it)),
                style = MaterialTheme.typography.labelMedium)
        }
        OutlinedTextField(
            value = state.content, onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth()
                .then(if (compact) Modifier else Modifier.weight(1f))
                .focusRequester(bodyFocus),
            label = { Text(stringResource(R.string.note_body)) },
            minLines = if (compact) 3 else 1,
            maxLines = if (compact) CompactBodyMaxLines else Int.MAX_VALUE,
            enabled = enabled,
        )
        EditorNoteOptions(state.options, enabled, onOptionsChange)
    }
}
