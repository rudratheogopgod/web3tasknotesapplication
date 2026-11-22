package com.rudra.web3tasknotesapplication.data.repository

import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
import com.rudra.web3tasknotesapplication.core.time.TimeProvider

internal class NoteSaveRules(private val time: TimeProvider, private val untitled: () -> String) {
    fun prepare(existing: Note?, title: String, content: String, options: NoteOptions? = null): Note {
        if (title.isBlank() && content.isBlank()) throw BlankNoteException()
        val savedTitle = title.ifBlank(untitled)
        val savedOptions = options ?: existing?.options ?: NoteOptions()
        if (existing?.title == savedTitle && existing.content == content && existing.options == savedOptions) {
            return existing
        }
        val now = time.nowMillis()
        return Note(
            id = existing?.id ?: 0,
            title = savedTitle,
            content = content,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            isPinned = savedOptions.isPinned,
            colorId = savedOptions.colorId,
            category = savedOptions.category,
        )
    }
}
