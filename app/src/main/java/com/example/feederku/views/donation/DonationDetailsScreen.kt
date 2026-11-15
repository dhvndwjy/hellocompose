package com.example.feederku.views.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.navigation.BottomNavbar
import com.example.feederku.navigation.Routes
import com.example.feederku.ui.theme.*
import com.example.feederku.views.donation.model.Donor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDetailsScreen(
    campaignId: String,
    onBackClick: () -> Unit,
    onDonateClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onChatClick: () -> Unit,
    viewModel: DonationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(campaignId) {
        viewModel.selectCampaign(campaignId)
    }

    val campaign = uiState.selectedCampaign

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Campaign Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Support local stray rescue & medical care", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorFogCream)
            )
        },
        bottomBar = {
            BottomNavbar(
                currentRoute = "", // Details page not in navbar
                onNavigate = { route ->
                    when (route) {
                        Routes.Dashboard.route -> onHomeClick()
                        Routes.Map.route -> onMapClick()
                        Routes.Chat.route -> onChatClick()
                        Routes.Donation.route -> onBackClick()
                    }
                }
            )
        },
        containerColor = ColorFogCream
    ) { padding ->
        if (campaign == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = buttonGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    // Campaign Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Text("Campaign Hero Image", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    }
                }

                item {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = campaign.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = textBrown,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = campaign.organizationName,
                                fontSize = 16.sp,
                                color = textBrown.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (campaign.isVerified) {
                                Surface(
                                    color = greenDark,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VERIFIED",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = campaign.description,
                            fontSize = 15.sp,
                            color = textBrown.copy(alpha = 0.9f),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progress Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = ColorLinenWarm,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                val progress = campaign.collectedAmount.toFloat() / campaign.targetAmount.toFloat()
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(CircleShape),
                                    color = buttonGreen,
                                    trackColor = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Rp ${campaign.collectedAmount} / Rp ${campaign.targetAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textBrown
                                    )
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = greenDark
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Donors", fontSize = 12.sp, color = Color.Gray)
                                        Text("${campaign.donorCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Remaining", fontSize = 12.sp, color = Color.Gray)
                                        Text("${campaign.daysRemaining} days", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Recent Donors",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textBrown
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                items(uiState.recentDonors) { donor ->
                    DonorItem(donor)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onDonateClick(campaign.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Donate Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun DonorItem(donor: Donor) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = ColorLinenWarm
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = donor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    text = "Donated Rp ${donor.amount} • ${getTimeAgo(donor.timestamp)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val hours = diff / 3600000
    return if (hours < 24) "$hours hrs ago" else "${hours / 24} days ago"
}
