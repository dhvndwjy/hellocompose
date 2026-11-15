package com.example.feederku.views.community.model

data class CommunityGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val memberCount: Int = 0,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val imageUrl: String? = null
)
