package com.example.feederku.views.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCampaignPostScreen(
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedSpot by remember { mutableStateOf("Block C") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Donation Campaigns", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = headerFooter,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = ColorFogCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            // Add Photo/Video Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(buttonGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .clickable { /* Handle upload */ },
                contentAlignment = Alignment.Center
            ) {
                // Dashed border is hard in Compose without custom drawing, using a simple stroke
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, buttonGreen.copy(alpha = 0.5f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = buttonGreen
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Add Photo or Video", fontWeight = FontWeight.Bold, color = textBrown)
                        Text("Supported formats: JPG, PNG, MP4", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Caption Field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write a caption about the feeding activity...", fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedBorderColor = buttonGreen
                )
            )

            Spacer(Modifier.height(20.dp))

            // Location Field
            Text("Location", fontWeight = FontWeight.Bold, color = textBrown, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedBorderColor = buttonGreen
                )
            )

            Spacer(Modifier.height(20.dp))

            // Recent Feeding Spot
            Text("Recent Feeding Spot", fontWeight = FontWeight.Bold, color = textBrown, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Block C", "Benton", "Kemang")) { spot ->
                    val isSelected = selectedSpot == spot
                    Surface(
                        modifier = Modifier.clickable { selectedSpot = spot },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) buttonGreen else ColorLinenWarm,
                        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
                    ) {
                        Text(
                            text = spot,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onPublishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Publish Post", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
