package com.example.feederku.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.headerFooter

@Composable
fun BottomNavbar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = headerFooter,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.Dashboard.route,
            onClick = { onNavigate(Routes.Dashboard.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = buttonGreen,
                unselectedIconColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Map.route,
            onClick = { onNavigate(Routes.Map.route) },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = buttonGreen,
                unselectedIconColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Chat.route,
            onClick = { onNavigate(Routes.Chat.route) },
            icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = buttonGreen,
                unselectedIconColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Donation.route,
            onClick = { onNavigate(Routes.Donation.route) },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = buttonGreen,
                unselectedIconColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
