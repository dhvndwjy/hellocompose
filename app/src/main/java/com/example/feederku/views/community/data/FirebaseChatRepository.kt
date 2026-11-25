package com.example.feederku.views.community.data

import com.example.feederku.model.FeedPost
import com.example.feederku.views.community.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ChatRepository {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override fun observeGroups(): Flow<List<GroupItem>> = callbackFlow {
        val listener = db.collection("groups")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val groups = snapshot?.documents?.map { doc ->
                    GroupItem(
                        id = doc.id,
                        name = doc.getString("name").orEmpty(),
                        initials = doc.getString("initials").orEmpty(),
                        lastMessage = doc.getString("lastMessage").orEmpty(),
                        time = doc.getString("time").orEmpty(),
                        unreadCount = doc.getLong("unreadCount")?.toInt() ?: 0
                    )
                }.orEmpty()

                if (groups.isEmpty()) {
                    trySend(
                        listOf(
                            GroupItem("group_ju", "Feeder Jakarta Utara", "JU", "Reza: We scheduled a massive feeding at 5 PM", "10m ago", 3),
                            GroupItem("group_kr", "Kitten Rescue Club", "KR", "Santi: Little Milo got adopted!", "2h ago", 0)
                        )
                    )
                } else {
                    trySend(groups)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createGroup(name: String, initials: String) {
        val data = hashMapOf(
            "name" to name,
            "initials" to initials,
            "lastMessage" to "Group created",
            "time" to "Just now",
            "unreadCount" to 0,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("groups").add(data).await()
    }

    override fun observeMessages(groupId: String, limit: Int): Flow<List<Message>> = callbackFlow {
        val targetGroup = groupId.ifBlank { "default_group" }
        val listener = db.collection("groups").document(targetGroup)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.map { doc ->
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId").orEmpty(),
                        senderName = doc.getString("senderName").orEmpty(),
                        text = doc.getString("text").orEmpty(),
                        timestamp = doc.getTimestamp(
                            "timestamp",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()?.time ?: System.currentTimeMillis()
                    )
                }.orEmpty().reversed()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(groupId: String, text: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Not logged in")
        val targetGroup = groupId.ifBlank { "default_group" }
        val data = hashMapOf(
            "senderId" to user.uid,
            "senderName" to (user.displayName ?: user.email?.substringBefore("@") ?: "Anonymous"),
            "text" to text,
            "timestamp" to FieldValue.serverTimestamp()
        )
        val groupRef = db.collection("groups").document(targetGroup)
        groupRef.collection("messages").add(data).await()
        groupRef.update(
            mapOf(
                "lastMessage" to "${user.displayName ?: "User"}: $text",
                "time" to "Just now"
            )
        ).await()
    }

    override fun observePosts(groupId: String): Flow<List<FeedPost>> = callbackFlow {
        val targetGroup = groupId.ifBlank { "default_group" }
        val listener = db.collection("groups").document(targetGroup)
            .collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.map { doc ->
                    FeedPost(
                        id = doc.id,
                        authorName = doc.getString("authorName").orEmpty(),
                        timeAgo = doc.getString("timeAgo").orEmpty(),
                        content = doc.getString("content").orEmpty(),
                        imageUrl = doc.getString("imageUrl")
                    )
                }.orEmpty()

                if (posts.isEmpty()) {
                    trySend(
                        listOf(
                            FeedPost(
                                id = "p1",
                                authorName = "Andi Wijaya",
                                timeAgo = "2 hrs ago",
                                content = "Just fed these local community cats at Block F parking lot. They were extremely hungry but very sweet! Please drop by if you have extra wet food.\n\n#streetfeeding #catlover #jakartafeeders"
                            )
                        )
                    )
                } else {
                    trySend(posts)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createPost(groupId: String, content: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Not logged in")
        val targetGroup = groupId.ifBlank { "default_group" }
        val data = hashMapOf(
            "authorName" to (user.displayName ?: user.email?.substringBefore("@") ?: "Anonymous"),
            "timeAgo" to "Just now",
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("groups").document(targetGroup).collection("posts").add(data).await()
    }
}
