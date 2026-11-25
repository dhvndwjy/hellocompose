package com.example.feederku.views.community.data

import com.example.feederku.model.FeedPost
import com.example.feederku.views.community.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlinx.coroutines.flow.map

class FakeChatRepository : ChatRepository {

    override val currentUserId = "me"

    private val now = System.currentTimeMillis()

    private val messages = MutableStateFlow(
        listOf(
            Message("1", "u1", "Sari", "Anyone available for feeding at Menteng Park this afternoon?", now - 600_000),
            Message("2", "u2", "Budi", "I can make it at 5 PM! Bring wet food as well.", now - 480_000),
            Message("3", "u1", "Sari", "Got it! Spotted 3 new cats there yesterday.", now - 300_000)
        )
    )

    private val groups = MutableStateFlow(
        listOf(
            GroupItem("group_ju", "Feeder Jakarta Utara", "JU", "Reza: We scheduled a massive feeding at 5 PM", "10m ago", 3),
            GroupItem("group_kr", "Kitten Rescue Club", "KR", "Santi: Little Milo got adopted!", "2h ago", 0)
        )
    )

    override fun observeGroups(): Flow<List<GroupItem>> = groups

    override suspend fun createGroup(name: String, initials: String) {
        groups.update { it + GroupItem(UUID.randomUUID().toString(), name, initials, "Group created", "Just now", 0) }
    }

    override fun observeMessages(groupId: String, limit: Int): Flow<List<Message>> =
        messages.map { it.takeLast(limit) }

    override suspend fun sendMessage(groupId: String, text: String) {
        messages.update { old ->
            old + Message(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                senderName = "You",
                text = text,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    override fun observePosts(groupId: String): Flow<List<FeedPost>> = flowOf(
        listOf(
            FeedPost(
                id = "p1",
                authorName = "Andi Wijaya",
                timeAgo = "2 hrs ago",
                content = "Just fed these local community cats at Block F parking lot. They were extremely hungry but very sweet! Please drop by if you have extra wet food.\n\n#streetfeeding #catlover #jakartafeeders"
            )
        )
    )

    override suspend fun createPost(groupId: String, content: String) {
        // no-op for fake
    }
}
