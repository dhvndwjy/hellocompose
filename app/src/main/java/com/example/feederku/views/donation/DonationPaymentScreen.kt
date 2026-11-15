package com.example.feederku.views.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationPaymentScreen(
    campaignId: String,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    viewModel: DonationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val campaign = uiState.selectedCampaign
    var agreed by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Details", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Please Review Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textBrown)
            Text(text = "Ensure everything is correct before executing payment", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    DetailRow(label = "CAMPAIGN NAME", value = campaign?.title ?: "Save the Beach District Cats")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailRow(label = "DONATION", value = "Rp 100,000", modifier = Modifier.weight(1f))
                        DetailRow(label = "PLATFORM FEE", value = "Rp 2,000", modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailRow(label = "DONOR NAME", value = user?.displayName ?: "Sarah Chen", modifier = Modifier.weight(1f))
                        DetailRow(label = "PAYMENT METHOD", value = "Credit Card (**** 4444)", modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TOTAL AMOUNT", fontWeight = FontWeight.Bold, color = textBrown)
                        Text("Rp 102,000", fontWeight = FontWeight.Bold, color = buttonGreen, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreed, onCheckedChange = { agreed = it }, colors = CheckboxDefaults.colors(checkedColor = buttonGreen))
                Text(text = "I agree to the terms of donation and support policy", fontSize = 12.sp, color = textBrown)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.submitDonation(
                        campaignId = campaignId,
                        amount = 100000L,
                        donorName = user?.displayName ?: "Anonymous",
                        onSuccess = onConfirmClick
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                shape = RoundedCornerShape(28.dp),
                enabled = agreed && !uiState.isLoading
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Confirm & Pay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 15.sp, color = textBrown, fontWeight = FontWeight.Bold)
    }
}
