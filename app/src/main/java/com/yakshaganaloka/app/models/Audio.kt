package com.yakshaganaloka.app.models

import com.google.firebase.firestore.DocumentId

data class Audio(
    @DocumentId
    val audioId: String = "",
    val title: String = "",
    val audioUrl: String = "",
    val category: String = "", // e.g., Talamaddale, Bhagavatike
    val artistIds: List<String> = emptyList(),
    val artistNames: List<String> = emptyList(),
    val artistName: String = "",
    val description: String = "",
    val youtubeUrl: String = "",
    val videoId: String = "",
    val thumbnailUrl: String = "",
    val isFeatured: Boolean = false
)
