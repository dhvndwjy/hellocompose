package com.example.feederku.views.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feederku.navigation.BottomNavbar
import com.example.feederku.navigation.Routes
import com.example.feederku.ui.theme.*
import com.example.feederku.views.donation.model.DonationCampaign

@Composable
fun DonationRoute(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DonationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DonationScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelect = viewModel::onCategorySelect,
        onCampaignClick = onNavigateToDetails,
        onRegisterClick = onNavigateToRegister,
        onHomeClick = onNavigateToHome,
        onMapClick = onNavigateToMap,
        onChatClick = onNavigateToChat,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    uiState: DonationUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onCampaignClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Surface(
                color = ColorTimber,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Donation Campaigns",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Support local stray rescue & medical care",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                        IconButton(
                            onClick = onRegisterClick,
                            modifier = Modifier
                                .background(buttonGreen, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = ColorFogCream
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = textBrown.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchQueryChange,
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (uiState.searchQuery.isEmpty()) {
                                        Text("Search groups or friends...", color = textBrown.copy(alpha = 0.4f), fontSize = 14.sp)
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavbar(
                currentRoute = Routes.Donation.route,
                onNavigate = { route ->
                    when (route) {
                        Routes.Dashboard.route -> onHomeClick()
                        Routes.Map.route -> onMapClick()
                        Routes.Chat.route -> onChatClick()
                    }
                }
            )
        },
        containerColor = ColorFogCream
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Category Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "Medical", "Rescue", "Food", "Shelter")
                items(categories) { category ->
                    val isSelected = uiState.selectedCategory == category
                    Surface(
                        modifier = Modifier.clickable { onCategorySelect(category) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) buttonGreen else Color.White,
                        border = if (!isSelected) BorderStroke(1.dp, buttonGreen.copy(alpha = 0.3f)) else null
                    ) {
                        Text(
                            text = category,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            color = if (isSelected) Color.White else greenDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Campaigns List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(uiState.filteredCampaigns) { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        onClick = { onCampaignClick(campaign.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: DonationCampaign,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = ColorLinenWarm,
        shadowElevation = 2.dp
    ) {
        Column {
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                // In actual app, use AsyncImage
                Text("Beach cats image placeholder", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = campaign.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBrown
                )
                Text(
                    text = campaign.organizationName,
                    fontSize = 14.sp,
                    color = textBrown.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = campaign.description,
                    fontSize = 13.sp,
                    color = textBrown.copy(alpha = 0.8f),
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                val progress = campaign.collectedAmount.toFloat() / campaign.targetAmount.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = buttonGreen,
                    trackColor = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp ${campaign.collectedAmount} / Rp ${campaign.targetAmount}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textBrown
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = greenDark
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Donate", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
