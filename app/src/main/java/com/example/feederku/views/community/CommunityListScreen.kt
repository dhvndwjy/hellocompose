package com.example.feederku.views.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.feederku.views.community.model.CommunityGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToDonation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock groups
    val groups = listOf(
        CommunityGroup("1", "Jakarta Feeder Community", "Discuss feeding points in Jakarta", 124, "Any cats at Benton Junction today?", System.currentTimeMillis() - 3600000),
        CommunityGroup("2", "Tangerang Stray Care", "Volunteers from Tangerang", 86, "I have some extra kibbles!", System.currentTimeMillis() - 7200000),
        CommunityGroup("3", "Medan Cat Lovers", "Community in Medan", 52, "Sent 100k donation", System.currentTimeMillis() - 86400000)
    )

    Scaffold(
        topBar = {
            Surface(color = ColorTimber) {
                Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
                    Text("Community", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Connect with other feeders", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = ColorFogCream
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Icon(Icons.Default.Search, null, tint = textBrown.copy(alpha = 0.6f))
                            Spacer(Modifier.width(8.dp))
                            Text("Search groups...", color = textBrown.copy(alpha = 0.4f), fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavbar(
                currentRoute = Routes.CommunityList.route,
                onNavigate = { route ->
                    when (route) {
                        Routes.Dashboard.route -> onNavigateToHome()
                        Routes.Map.route -> onNavigateToMap()
                        Routes.Donation.route -> onNavigateToDonation()
                    }
                }
            )
        },
        containerColor = ColorFogCream
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(groups) { group ->
                GroupItem(group = group, onClick = { onNavigateToChat(group.id) })
            }
        }
    }
}

@Composable
fun GroupItem(group: CommunityGroup, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = ColorLinenWarm
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(buttonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Text(group.name.take(1), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = greenDark)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = group.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textBrown)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = formatTime(group.lastMessageTime), fontSize = 10.sp, color = Color.Gray)
                }
                Text(text = group.lastMessage, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                Text(text = "${group.memberCount} members", fontSize = 11.sp, color = buttonGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
