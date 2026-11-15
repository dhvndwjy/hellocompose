package com.example.feederku.views.map.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feederku.navigation.BottomNavbar
import com.example.feederku.navigation.Routes
import com.example.feederku.ui.theme.*
import com.example.feederku.views.map.MapViewModel
import com.example.feederku.views.map.model.Sighting
import com.example.feederku.views.map.model.SightingCategory
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToDonation: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavbar(
                currentRoute = Routes.Map.route,
                onNavigate = { route ->
                    when (route) {
                        Routes.Dashboard.route -> onNavigateToHome()
                        Routes.Donation.route -> onNavigateToDonation()
                        Routes.Chat.route -> onNavigateToChat()
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(ColorFogCream)) {
            // Map Content using osmdroid via AndroidView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(-6.225, 106.611))
                        setMultiTouchControls(true)
                        
                        // Handle Map Clicks
                        val overlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                viewModel.onMapClick(p)
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        })
                        overlays.add(overlay)
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it is Marker || it is Polyline }
                    
                    // Add Markers
                    uiState.sightings.forEach { sighting ->
                        val marker = Marker(mapView).apply {
                            position = sighting.location
                            title = sighting.title
                            setOnMarkerClickListener { m, _ ->
                                viewModel.onSightingClick(sighting)
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }

                    // Simple Route Line if active
                    if (uiState.isShowingRoute && uiState.routeDestination != null) {
                        val userLocation = GeoPoint(-6.228, 106.612) // Mock user location
                        val line = Polyline(mapView).apply {
                            setPoints(listOf(userLocation, uiState.routeDestination))
                            outlinePaint.color = android.graphics.Color.parseColor("#4CAF50") // buttonGreen
                            outlinePaint.strokeWidth = 10f
                        }
                        mapView.overlays.add(line)
                        
                        val userMarker = Marker(mapView).apply {
                            position = userLocation
                            // In osmdroid, we'd use a different icon for user location
                        }
                        mapView.overlays.add(userMarker)
                    }
                    
                    mapView.invalidate()
                }
            )

            // Top UI Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = searchBar,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = textBrown)
                        Spacer(Modifier.width(8.dp))
                        Text("Search for location...", color = textBrown.copy(alpha = 0.6f))
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("Urgent Spots", "Recently Fed", "Many Cats", "Vet")
                    filters.forEach { filter ->
                        FilterChipItem(
                            text = filter,
                            isSelected = uiState.selectedFilter == filter,
                            onClick = { viewModel.onFilterSelect(filter) }
                        )
                    }
                }
            }

            // Floating Action Button
            if (!uiState.isShowingRoute) {
                Button(
                    onClick = { viewModel.onReportClick() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Report Sighting")
                }
            }

            // Placement Prompt
            if (uiState.isPlacingSighting) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 120.dp),
                    color = buttonGreen,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Tap the map to place your sighting", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Details Overlay
            uiState.selectedSighting?.let { sighting ->
                SightingDetailsCard(
                    sighting = sighting,
                    isRouteMode = uiState.isShowingRoute,
                    onDismiss = { viewModel.onDismissSighting() },
                    onDetailsClick = { onNavigateToDetails(sighting.id) },
                    onGetRouteClick = { viewModel.onGetRouteClick() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }

            // Report Sighting Overlay
            if (uiState.isReporting) {
                ReportSightingOverlay(
                    address = uiState.reportAddress,
                    onAddressChange = { viewModel.onReportAddressChange(it) },
                    selectedCategory = uiState.reportCategory,
                    onCategorySelect = { viewModel.onReportCategorySelect(it) },
                    notes = uiState.reportNotes,
                    onNotesChange = { viewModel.onReportNotesChange(it) },
                    onSubmit = { viewModel.onSubmitReport() },
                    onDismiss = { viewModel.onDismissReport() },
                    onSelectOnMap = { viewModel.onStartPlacingSighting() }
                )
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) buttonGreen.copy(alpha = 0.2f) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) buttonGreen else Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = if (isSelected) greenDark else textBrown,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SightingDetailsCard(
    sighting: Sighting,
    isRouteMode: Boolean,
    onDismiss: () -> Unit,
    onDetailsClick: () -> Unit,
    onGetRouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ColorLinenWarm,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .size(40.dp, 4.dp)
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        .align(Alignment.TopCenter)
                )
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { onDismiss() }
                        .size(20.dp),
                    tint = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Green))
                Spacer(Modifier.width(8.dp))
                Text(sighting.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textBrown, modifier = Modifier.weight(1f))
                
                if (sighting.status.isNotEmpty()) {
                    Surface(
                        color = when(sighting.category) {
                            SightingCategory.URGENT_HELP -> liveRed
                            SightingCategory.MANY_CATS -> Color(0xFFF4A460) // SandyBrown for Many Cats
                            else -> buttonGreen
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            sighting.status,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text("• ${sighting.distance}", fontSize = 12.sp, color = textBrown.copy(alpha = 0.7f))

            Spacer(Modifier.height(12.dp))

            if (!isRouteMode) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.White)
                    }
                    
                    Spacer(Modifier.width(12.dp))
                    Text(
                        sighting.description,
                        fontSize = 13.sp,
                        color = textBrown,
                        lineHeight = 18.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDetailsClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, buttonGreen)
                    ) {
                        Text("Details", color = greenDark)
                    }
                    Button(
                        onClick = onGetRouteClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen)
                    ) {
                        Text("Get Route")
                    }
                }
            } else {
                // Route Info Mode
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    RouteInfoItem("Walk Time", "3 min", modifier = Modifier.weight(1f))
                    RouteInfoItem("Distance", "500m", modifier = Modifier.weight(1f))
                    if (sighting.category == SightingCategory.URGENT_HELP || sighting.category == SightingCategory.MANY_CATS) {
                        RouteInfoItem(
                            label = if (sighting.category == SightingCategory.MANY_CATS) "Cats" else "Cats", 
                            value = if (sighting.category == SightingCategory.MANY_CATS) "5" else "3-4",
                            valueColor = if (sighting.category == SightingCategory.MANY_CATS) greenDark else liveRed,
                            modifier = Modifier.weight(1f),
                            isStatus = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RouteInfoItem(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = textBrown, isStatus: Boolean = false) {
    Surface(
        modifier = modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isStatus) buttonGreen.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun ReportSightingOverlay(
    address: String,
    onAddressChange: (String) -> Unit,
    selectedCategory: SightingCategory?,
    onCategorySelect: (SightingCategory) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    onSelectOnMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth()
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(24.dp),
            color = ColorLinenWarm
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .size(40.dp, 4.dp)
                            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .align(Alignment.TopCenter)
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onDismiss() }
                            .size(20.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Green))
                    Spacer(Modifier.width(8.dp))
                    Text("Report Sighting", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textBrown)
                }
                Text("Fill standard info to help nearby feeders", fontSize = 12.sp, color = textBrown.copy(alpha = 0.6f))

                Spacer(Modifier.height(20.dp))

                Text("Sighting Address", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = textBrown)
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp).clickable { onSelectOnMap() }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if(address.isEmpty()) "Search for location..." else address, color = Color.Gray, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Category", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = textBrown)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryItem(
                        "Many Cats", 
                        icon = Icons.Default.Pets,
                        isSelected = selectedCategory == SightingCategory.MANY_CATS,
                        onClick = { onCategorySelect(SightingCategory.MANY_CATS) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryItem(
                        "Urgent Help", 
                        icon = Icons.Default.ErrorOutline,
                        isSelected = selectedCategory == SightingCategory.URGENT_HELP,
                        onClick = { onCategorySelect(SightingCategory.URGENT_HELP) },
                        modifier = Modifier.weight(1f),
                        selectedColor = liveRed
                    )
                    CategoryItem(
                        "Already Fed", 
                        icon = Icons.Default.CheckCircleOutline,
                        isSelected = selectedCategory == SightingCategory.ALREADY_FED,
                        onClick = { onCategorySelect(SightingCategory.ALREADY_FED) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text("Additional Sighting Notes", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = textBrown)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    placeholder = { Text("Add any helpful details...", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttonGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Text(
                    "${notes.length} / 200",
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Submit Report", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = buttonGreen
) {
    Surface(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) selectedColor else Color.LightGray)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) selectedColor.copy(alpha = 0.1f) else Color.Transparent
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (isSelected) selectedColor else Color.Gray,
                    modifier = Modifier.size(24.dp).padding(4.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                label, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Bold,
                color = if (isSelected) selectedColor else textBrown
            )
        }
    }
}
