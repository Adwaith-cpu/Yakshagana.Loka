package com.yakshaganaloka.app.models

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val role: String = "user",
    val notificationsEnabled: Boolean = true,
    val newsletterEnabled: Boolean = false,
    val favoriteArtists: List<String> = emptyList()
)
