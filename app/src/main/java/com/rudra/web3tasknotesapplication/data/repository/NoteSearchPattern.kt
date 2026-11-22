package com.rudra.web3tasknotesapplication.data.repository

/** Uses ! as the LIKE escape character and matches the trimmed input literally. */
internal fun noteSearchPattern(query: String): String? {
    val term = query.trim().ifEmpty { return null }
    return buildString {
        append('%')
        for (character in term) {
            if (character == '!' || character == '%' || character == '_') append('!')
            append(character)
        }
        append('%')
    }
}
