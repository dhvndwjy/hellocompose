package com.example.feederku.views.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feederku.R
import com.example.feederku.ui.theme.*
import com.example.feederku.views.community.model.Message

@Composable
fun DirectMessageRoute(
    friendName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ChatViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DirectMessageScreen(
        friendName = friendName,
        uiState = uiState,
        onInputChange = viewModel::onInputChange,
        onSendClick = { viewModel.onSendClick("default_group") },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun DirectMessageScreen(
    friendName: String,
    uiState: com.example.feederku.views.community.model.ChatUiState,
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
        // Top Header
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(36.dp)) {
                        Image(
                            painter = painterResource(R.drawable.profile_circle),
                            contentDescription = friendName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(buttonGreen, CircleShape)
                                .align(Alignment.BottomEnd)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                    Column {
                        Text(
                            text = friendName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = DMSans
                        )
                        Text(
                            text = "Active Now",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = DMSans
                        )
                    }
                }
            }
        }

        // Messages list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val sampleDm = listOf(
                Message("dm1", "other", friendName, "Hey", System.currentTimeMillis() - 120_000),
                Message("dm2", "other", friendName, "How's the cat near your home? Is he okay?", System.currentTimeMillis() - 60_000),
                Message("dm3", "me", "You", "He's okay, just a little scratch", System.currentTimeMillis() - 10_000)
            )
            val displayMessages = if (uiState.messages.isEmpty()) sampleDm else uiState.messages

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayMessages) { message ->
                    DmMessageBubble(
                        message = message,
                        isMine = message.senderId == uiState.currentUserId || message.senderName == "You"
                    )
                }
            }
        }

        // Message Input Bar
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

@Composable
private fun DmMessageBubble(message: Message, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
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
            }
        }
    }
}
