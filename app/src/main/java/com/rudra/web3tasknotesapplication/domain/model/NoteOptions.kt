package com.rudra.web3tasknotesapplication.domain.model

/** Groups the metadata saved alongside a note's title and body. */
data class NoteOptions(
    val isPinned: Boolean = false,
    val colorId: NoteColor = NoteColor.DEFAULT,
    val category: NoteCategory = NoteCategory.QUICK_NOTE,
)
