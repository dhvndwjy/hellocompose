package com.example.feederku.views.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.headerFooter

@Composable
fun DashboardFooter(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onChatClick: () -> Unit,
    onDonateClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentRoute: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(headerFooter)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeTint = if (currentRoute == "dashboard") buttonGreen else Color.White
        val mapTint = if (currentRoute == "map") buttonGreen else Color.White
        val chatTint = if (currentRoute == "chat") buttonGreen else Color.White
        val donateTint = if (currentRoute == "donation") buttonGreen else Color.White

        IconButton(onClick = onHomeClick) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = homeTint)
        }
        IconButton(onClick = onMapClick) {
            Icon(Icons.Default.LocationOn, contentDescription = "Map", tint = mapTint)
        }
        IconButton(onClick = onChatClick) {
            Icon(Icons.Default.MailOutline, contentDescription = "Community", tint = chatTint)
        }
        IconButton(onClick = onDonateClick) {
            Icon(Icons.Default.FavoriteBorder, contentDescription = "Donate", tint = donateTint)
        }
    }
}
