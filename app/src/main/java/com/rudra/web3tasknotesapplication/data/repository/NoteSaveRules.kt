package com.rudra.web3tasknotesapplication.data.repository

import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.core.time.TimeProvider

internal class NoteSaveRules(private val time: TimeProvider, private val untitled: () -> String) {
    fun prepare(existing: Note?, title: String, content: String): Note {
        if (title.isBlank() && content.isBlank()) throw BlankNoteException()
        val savedTitle = title.ifBlank(untitled)
        if (existing != null && existing.title == savedTitle && existing.content == content) {
            return existing
        }
        val now = time.nowMillis()
        return existing?.copy(title = savedTitle, content = content, updatedAt = now)
            ?: Note(0, savedTitle, content, now, now)
    }
}
