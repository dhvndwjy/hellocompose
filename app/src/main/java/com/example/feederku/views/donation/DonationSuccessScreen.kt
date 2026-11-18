package com.example.feederku.views.donation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationSuccessScreen(
    type: String, // "donation" or "campaign"
    onBackToCampaigns: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Success", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = headerFooter,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = ColorFogCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = buttonGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Ikon Sukses",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (type == "donation") {
                Text(
                    text = "Donation Successful!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBrown
                )
                Text(
                    text = "Thank you for your generous contribution",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = ColorLinenWarm,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        SuccessRow(label = "CAMPAIGN", value = "Save Beach Cats")
                        Spacer(modifier = Modifier.height(12.dp))
                        SuccessRow(label = "AMOUNT DONATED", value = "Rp 100,000")
                        Spacer(modifier = Modifier.height(12.dp))
                        SuccessRow(label = "TRANSACTION ID", value = "FKU-827394622", valueColor = buttonGreen)
                    }
                }
            } else {
                Text(
                    text = "Campaign successfully submitted!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBrown,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Feederku team will verify the documents foundation and your campaign within 1x24 working hours",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = ColorLinenWarm,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        SuccessRow(label = "Verify foundation documents", value = "1x24 hours", valueColor = greenDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        SuccessRow(label = "Review campaign content", value = "4 hours", valueColor = greenDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        SuccessRow(label = "Campaign is live & accepting donations", value = "Once approved", valueColor = greenDark)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToCampaigns,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (type == "donation") "Back to Campaigns" else "Success",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SuccessRow(label: String, value: String, valueColor: Color = textBrown) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
