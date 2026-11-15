package com.example.feederku.views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feederku.ui.theme.*
import com.example.feederku.views.dashboard.components.*

@Composable
fun DashboardRoute(
    onProfileClick: () -> Unit,
    onGoLiveClick: () -> Unit,
    onLiveStreamClick: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToDonation: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(creamBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                DashboardHeader(
                    userName = uiState.userName,
                    userTitle = uiState.userTitle,
                    onAddClick = onProfileClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                SearchBarSection(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionHeader(title = "Recent Live Streams", onSeeAllClick = {})
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.recentStreams) { stream ->
                        LiveStreamCard(
                            stream = stream,
                            onClick = { onLiveStreamClick(stream.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                SectionHeader(title = "Your Impact This Month", onSeeAllClick = {})
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImpactCard(value = "${uiState.impactStats.catsFed}", label = "Cats Fed")
                    ImpactCard(value = "${uiState.impactStats.locations}", label = "Locations")
                    ImpactCard(value = "${uiState.impactStats.activeVolunteers}", label = "Volunteers")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                SectionHeader(title = "Recent Community Feed", onSeeAllClick = {})
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.feedPosts) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    FeedPostCard(post = post)
                }
            }
        }

        Button(
            onClick = onGoLiveClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Go Live", tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Go Live", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = DMSans)
        }

        DashboardFooter(
            onHomeClick = {},
            onMapClick = onNavigateToMap,
            onChatClick = onNavigateToChat,
            onDonateClick = onNavigateToDonation,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
