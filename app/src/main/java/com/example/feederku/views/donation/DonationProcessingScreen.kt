package com.example.feederku.views.donation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DonationProcessingScreen(
    onProcessingFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000) // Mock processing time
        onProcessingFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = buttonGreen,
                strokeWidth = 6.dp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Processing your donation...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textBrown
            )
            
            Text(
                text = "Please don't close this page or press back",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
