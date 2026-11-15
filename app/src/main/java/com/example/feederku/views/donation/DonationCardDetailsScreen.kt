package com.example.feederku.views.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Shield
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
fun DonationCardDetailsScreen(
    campaignId: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: DonationViewModel
) {
    var cardNumber by remember { mutableStateOf("4111 2222 3333 4444") }
    var expiryDate by remember { mutableStateOf("12 / 28") }
    var cvv by remember { mutableStateOf("***") }
    var cardholderName by remember { mutableStateOf("SARAH CHEN") }

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
                .padding(24.dp)
        ) {
            // Card Type Selector Placeholder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CreditCard, null, tint = textBrown)
                    Spacer(Modifier.width(12.dp))
                    Text("Credit/Debit Card", fontWeight = FontWeight.Bold, color = textBrown, modifier = Modifier.weight(1f))
                    Text("CHANGE", fontWeight = FontWeight.Bold, color = textBrown, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            CardInputField(label = "Card Number", value = cardNumber, onValueChange = { cardNumber = it }, leadingIcon = Icons.Default.CreditCard)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    CardInputField(label = "Expiry Date", value = expiryDate, onValueChange = { expiryDate = it })
                }
                Box(Modifier.weight(1f)) {
                    CardInputField(label = "CVV", value = cvv, onValueChange = { cvv = it })
                }
            }

            CardInputField(label = "Cardholder Name", value = cardholderName, onValueChange = { cardholderName = it })

            Spacer(Modifier.height(16.dp))

            // Encryption Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = buttonGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Your credentials are protected with 256-bit bank-grade encryption",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CardInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textBrown, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = textBrown) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = ColorLinenWarm,
                focusedContainerColor = ColorLinenWarm,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = buttonGreen
            )
        )
    }
}
