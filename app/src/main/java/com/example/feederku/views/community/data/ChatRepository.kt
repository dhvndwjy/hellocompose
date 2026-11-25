package com.example.feederku.views.community.data

import com.example.feederku.model.FeedPost
import com.example.feederku.views.community.model.Message
import kotlinx.coroutines.flow.Flow

data class GroupItem(
    val id: String,
    val name: String,
    val initials: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int
)

interface ChatRepository {
    val currentUserId: String
    fun observeGroups(): Flow<List<GroupItem>>
    suspend fun createGroup(name: String, initials: String)
    fun observeMessages(groupId: String, limit: Int): Flow<List<Message>>
    suspend fun sendMessage(groupId: String, text: String)
    fun observePosts(groupId: String): Flow<List<FeedPost>>
    suspend fun createPost(groupId: String, content: String)
}
