package com.example.feederku.views.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feederku.R
import com.example.feederku.model.FeedPost
import com.example.feederku.ui.theme.*
import com.example.feederku.views.community.data.GroupItem
import com.example.feederku.views.community.model.Message
import com.example.feederku.views.dashboard.components.FeedPostCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf("hub") } // "hub", "room", "request", "dm", "publish"
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedFriendName by remember { mutableStateOf<String?>(null) }
    var currentTab by remember { mutableStateOf(0) } // 0: Posts, 1: Chat

    when (currentScreen) {
        "room" -> {
            CommunityChatRoomScreen(
                title = selectedGroupName ?: "Chat",
                uiState = uiState,
                posts = posts,
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                onInputChange = viewModel::onInputChange,
                onSendClick = { viewModel.onSendClick(selectedGroupId ?: "default_group") },
                onBackClick = { currentScreen = "hub" },
                modifier = modifier
            )
        }
        "request" -> {
            RequestScreen(
                onBackClick = { currentScreen = "hub" },
                onMessageClick = { friendName ->
                    selectedFriendName = friendName
                    currentScreen = "dm"
                },
                modifier = modifier
            )
        }
        "dm" -> {
            DirectMessageScreen(
                friendName = selectedFriendName ?: "Friend",
                uiState = uiState,
                onInputChange = viewModel::onInputChange,
                onSendClick = { viewModel.onSendClick("default_group") },
                onBackClick = { currentScreen = "hub" },
                modifier = modifier
            )
        }
        "publish" -> {
            PublishPostScreen(
                caption = uiState.input,
                onCaptionChange = viewModel::onInputChange,
                location = "",
                onLocationChange = {},
                onSpotClick = {},
                onBackClick = { currentScreen = "room" },
                onPublishClick = {
                    if (uiState.input.isNotBlank()) {
                        viewModel.createPost(selectedGroupId ?: "default_group", uiState.input)
                        currentScreen = "room"
                    }
                },
                modifier = modifier
            )
        }
        else -> {
            FeederGroupsHubScreen(
                groups = groups,
                onRequestClick = { currentScreen = "request" },
                onFriendClick = { friendName ->
                    selectedFriendName = friendName
                    currentScreen = "dm"
                },
                onGroupClick = { groupId, groupName ->
                    selectedGroupId = groupId
                    selectedGroupName = groupName
                    viewModel.selectGroup(groupId)
                    currentScreen = "room"
                },
                onCreateGroupClick = { currentScreen = "publish" },
                modifier = modifier
            )
        }
    }
}

// ==========================================
// 1. FEEDER GROUPS HUB SCREEN
// ==========================================
@Composable
fun FeederGroupsHubScreen(
    groups: List<GroupItem>,
    onRequestClick: () -> Unit,
    onFriendClick: (String) -> Unit,
    onGroupClick: (String, String) -> Unit,
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        // Dark Brown Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(textBrown)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Feeder Groups",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = DMSans
                    )
                    Text(
                        text = "Join regional stray care networks",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = DMSans
                    )
                }
                Icon(
                    Icons.Default.Groups,
                    contentDescription = "Groups",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search groups or friends...", color = textBrown.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = textBrown) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttonGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Friends Online Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Friends Online",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textBrown,
                            fontFamily = DMSans
                        )
                        Text(
                            text = "Request",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = buttonGreen,
                            fontFamily = DMSans,
                            modifier = Modifier.clickable { onRequestClick() }
                        )
                    }

                    val friends = listOf(
                        "Andi W." to R.drawable.profile_circle,
                        "Dewi L." to R.drawable.profile_circle,
                        "Budi U." to R.drawable.profile_circle,
                        "Melasti" to R.drawable.profile_circle,
                        "Andi W." to R.drawable.profile_circle
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(friends) { friend ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onFriendClick(friend.first) }
                            ) {
                                Box {
                                    Image(
                                        painter = painterResource(friend.second),
                                        contentDescription = friend.first,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(buttonGreen, CircleShape)
                                            .align(Alignment.BottomEnd)
                                            .border(2.dp, Color.White, CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = friend.first,
                                    fontSize = 12.sp,
                                    color = textBrown,
                                    fontFamily = DMSans
                                )
                            }
                        }
                    }
                }
            }

            // Recent Chats Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recent Chats",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textBrown,
                        fontFamily = DMSans
                    )

                    RecentChatCard(
                        name = "Dewi Lestari",
                        message = "I just bought some kitten wet food today...",
                        time = "2m ago",
                        onClick = { onFriendClick("Dewi Lestari") }
                    )
                    RecentChatCard(
                        name = "Budi Utomo",
                        message = "Is the ginger cat still under the bridge?",
                        time = "1h ago",
                        onClick = { onFriendClick("Budi Utomo") }
                    )
                }
            }

            // My Groups Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "My Groups",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textBrown,
                        fontFamily = DMSans
                    )

                    if (groups.isEmpty()) {
                        Text(text = "No groups found.", fontSize = 12.sp, color = textBrown)
                    } else {
                        groups.forEach { group ->
                            GroupCard(
                                initials = group.initials,
                                groupName = group.name,
                                lastMessage = group.lastMessage,
                                time = group.time,
                                unreadCount = group.unreadCount,
                                onClick = { onGroupClick(group.id, group.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = onCreateGroupClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.5.dp, buttonGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Group")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Create New Group",
                            fontWeight = FontWeight.Bold,
                            fontFamily = DMSans
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentChatCard(name: String, message: String, time: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.profile_circle),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textBrown, fontFamily = DMSans)
                    Text(text = time, fontSize = 11.sp, color = Color.Gray, fontFamily = DMSans)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = message, fontSize = 12.sp, color = textBrown.copy(alpha = 0.7f), maxLines = 1, fontFamily = DMSans)
            }
        }
    }
}

@Composable
fun GroupCard(initials: String, groupName: String, lastMessage: String, time: String, unreadCount: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(cardLinen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, fontWeight = FontWeight.Bold, color = textBrown, fontFamily = DMSans)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = groupName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textBrown, fontFamily = DMSans)
                    Text(text = time, fontSize = 11.sp, color = Color.Gray, fontFamily = DMSans)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lastMessage,
                        fontSize = 12.sp,
                        color = textBrown.copy(alpha = 0.7f),
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        fontFamily = DMSans
                    )
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(buttonGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = unreadCount.toString(), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. COMMUNITY CHAT ROOM & POSTS SCREEN
// ==========================================
@Composable
fun CommunityChatRoomScreen(
    title: String,
    uiState: com.example.feederku.views.community.model.ChatUiState,
    posts: List<FeedPost>,
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Dark Brown Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(textBrown)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = DMSans
                    )
                    Text(
                        text = "143 members • Active community",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = DMSans
                    )
                }
            }
        }

        // Tabs (Posts / Chat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(textBrown),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TabItem(title = "Posts", selected = currentTab == 0, onClick = { onTabSelected(0) })
            TabItem(title = "Chat", selected = currentTab == 1, onClick = { onTabSelected(1) })
        }

        // Content Area (Posts vs Chat)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (currentTab == 0) {
                // Posts Tab Content (Fetched from Firestore)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        FeedPostCard(post = post)
                    }
                }
            } else {
                // Chat Tab Content (Fetched from Firestore)
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = buttonGreen
                    )

                    uiState.messages.isEmpty() -> Text(
                        text = "No messages yet.\nStart the conversation!",
                        color = textBrown,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages) { message ->
                            MessageBubble(
                                message = message,
                                isMine = message.senderId == uiState.currentUserId
                            )
                        }
                    }
                }
            }
        }

        // Message Input Bar (Only show in Chat tab)
        if (currentTab == 1) {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = textBrown)
                    }
                    OutlinedTextField(
                        value = uiState.input,
                        onValueChange = onInputChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type your message...", color = textBrown.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttonGreen,
                            unfocusedBorderColor = textBrown.copy(alpha = 0.3f),
                            focusedTextColor = textBrown,
                            unfocusedTextColor = textBrown,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    IconButton(
                        onClick = onSendClick,
                        enabled = uiState.input.isNotBlank(),
                        modifier = Modifier
                            .background(if (uiState.input.isNotBlank()) buttonGreen else Color.LightGray, CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(title: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = DMSans
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(3.dp)
                    .background(buttonGreen, RoundedCornerShape(1.5.dp))
            )
        }
    }
}

@Composable
private fun MessageBubble(message: Message, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (!isMine) {
            Text(
                text = message.senderName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textBrown,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                fontFamily = DMSans
            )
        }
        Surface(
            color = if (isMine) buttonGreen else Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.text,
                    color = if (isMine) Color.White else textBrown,
                    fontFamily = DMSans
                )
                Text(
                    text = formatTime(message.timestamp),
                    fontSize = 10.sp,
                    color = if (isMine) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = DMSans
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
