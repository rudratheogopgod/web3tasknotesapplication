package com.rudra.web3tasknotesapplication.domain.model

/** A saved note independent of its storage format. */
data class Note(val id: Long, val title: String, val content: String, val createdAt: Long, val updatedAt: Long)
