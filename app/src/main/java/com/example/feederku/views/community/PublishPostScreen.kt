package com.example.feederku.views.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*

@Composable
fun PublishPostRoute(
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ChatViewModel.Factory)
) {
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    PublishPostScreen(
        caption = caption,
        onCaptionChange = { caption = it },
        location = location,
        onLocationChange = { location = it },
        onSpotClick = { spot -> location = spot },
        onBackClick = onBackClick,
        onPublishClick = {
            if (caption.isNotBlank()) {
                viewModel.createPost("default_group", caption)
                onPublishSuccess()
            }
        },
        modifier = modifier
    )
}

@Composable
fun PublishPostScreen(
    caption: String,
    onCaptionChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    onSpotClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "New Feeding Post",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = DMSans
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add Photo or Video Box (Dashed border look)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(BorderStroke(2.dp, buttonGreen), RoundedCornerShape(16.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = buttonGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = buttonGreen, modifier = Modifier.size(26.dp))
                            }
                        }
                        Text(
                            text = "Add Photo or Video",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textBrown,
                            fontFamily = DMSans
                        )
                        Text(
                            text = "Supported formats: JPG, PNG, MP4",
                            fontSize = 11.sp,
                            color = textBrown.copy(alpha = 0.5f),
                            fontFamily = DMSans
                        )
                    }
                }

                // Caption Input
                OutlinedTextField(
                    value = caption,
                    onValueChange = onCaptionChange,
                    placeholder = { Text("Write a caption about the feeding activity...", color = textBrown.copy(alpha = 0.4f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttonGreen,
                        unfocusedBorderColor = textBrown.copy(alpha = 0.2f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = textBrown,
                        unfocusedTextColor = textBrown
                    )
                )

                // Location Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textBrown,
                        fontFamily = DMSans
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = onLocationChange,
                        placeholder = { Text("Enter location...", color = textBrown.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttonGreen,
                            unfocusedBorderColor = textBrown.copy(alpha = 0.2f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = textBrown,
                            unfocusedTextColor = textBrown
                        )
                    )
                }

                // Recent Feeding Spots Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Recent Feeding Spot",
                        fontSize = 13.sp,
                        color = textBrown.copy(alpha = 0.7f),
                        fontFamily = DMSans
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Block C", "Benton", "Kemang").forEach { spot ->
                            FilterChip(
                                selected = false,
                                onClick = { onSpotClick(spot) },
                                label = { Text(spot) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = cardLinen,
                                    labelColor = textBrown
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

            // Publish Button
            Button(
                onClick = onPublishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Publish Post", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp, fontFamily = DMSans)
            }
        }
    }
}
