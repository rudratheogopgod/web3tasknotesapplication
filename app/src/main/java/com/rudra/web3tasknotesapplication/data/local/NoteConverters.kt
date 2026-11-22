package com.rudra.web3tasknotesapplication.data.local

import androidx.room.TypeConverter
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor

/** Uses explicit database codes so enum order and display labels can change independently. */
class NoteConverters {
    @TypeConverter
    fun encodeColor(color: NoteColor): String = when (color) {
        NoteColor.DEFAULT -> "DEFAULT"
        NoteColor.ROSE -> "ROSE"
        NoteColor.PEACH -> "PEACH"
        NoteColor.SAND -> "SAND"
        NoteColor.MINT -> "MINT"
        NoteColor.LAVENDER -> "LAVENDER"
    }

    @TypeConverter
    fun decodeColor(code: String): NoteColor = when (code) {
        "DEFAULT" -> NoteColor.DEFAULT
        "ROSE" -> NoteColor.ROSE
        "PEACH" -> NoteColor.PEACH
        "SAND" -> NoteColor.SAND
        "MINT" -> NoteColor.MINT
        "LAVENDER" -> NoteColor.LAVENDER
        else -> error("Unknown note color code: $code")
    }

    @TypeConverter
    fun encodeCategory(category: NoteCategory): String = when (category) {
        NoteCategory.QUICK_NOTE -> "QUICK_NOTE"
        NoteCategory.TODO_LIST -> "TODO_LIST"
        NoteCategory.JOURNAL -> "JOURNAL"
    }

    @TypeConverter
    fun decodeCategory(code: String): NoteCategory = when (code) {
        "QUICK_NOTE" -> NoteCategory.QUICK_NOTE
        "TODO_LIST" -> NoteCategory.TODO_LIST
        "JOURNAL" -> NoteCategory.JOURNAL
        else -> error("Unknown note category code: $code")
    }
}
