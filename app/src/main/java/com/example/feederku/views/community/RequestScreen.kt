package com.example.feederku.views.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.R
import com.example.feederku.ui.theme.*

@Composable
fun RequestScreen(
    onBackClick: () -> Unit,
    onMessageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamBackground)
            .statusBarsPadding()
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
                    text = "Request",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = DMSans
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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

            // Requests List
            val requests = listOf(
                "Bunga ulala" to "I just bought some kitten wet food today...",
                "Fikri" to "Is the ginger cat still under the bridge?"
            )

            items(requests) { req ->
                Surface(
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
                            contentDescription = req.first,
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
                                Text(text = req.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textBrown, fontFamily = DMSans)
                                Text(text = "1w ago", fontSize = 11.sp, color = Color.Gray, fontFamily = DMSans)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = req.second, fontSize = 12.sp, color = textBrown.copy(alpha = 0.7f), maxLines = 1, fontFamily = DMSans)
                        }
                    }
                }
            }

            // Suggested Feeders Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Suggested Feeders",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBrown,
                    fontFamily = DMSans
                )
                Spacer(modifier = Modifier.height(8.dp))

                val suggestions = listOf(
                    "Dewi Lestari" to "@dewi_l",
                    "Budi Utomo" to "@budi_u",
                    "Melati Kusuma" to "@melati_k"
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    suggestions.forEach { sug ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(cardLinen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.profile_circle),
                                            contentDescription = sug.first,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column {
                                        Text(text = sug.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textBrown, fontFamily = DMSans)
                                        Text(text = sug.second, fontSize = 12.sp, color = textBrown.copy(alpha = 0.6f), fontFamily = DMSans)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { onMessageClick(sug.first) },
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, buttonGreen),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonGreen),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    Text("Message", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = DMSans)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
