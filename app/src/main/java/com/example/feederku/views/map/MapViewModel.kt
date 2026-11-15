package com.example.feederku.views.map

import androidx.lifecycle.ViewModel
import com.example.feederku.views.map.model.Sighting
import com.example.feederku.views.map.model.SightingCategory
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(
    val sightings: List<Sighting> = emptyList(),
    val selectedSighting: Sighting? = null,
    val isReporting: Boolean = false,
    val reportAddress: String = "",
    val reportCategory: SightingCategory? = null,
    val reportNotes: String = "",
    val isPlacingSighting: Boolean = false,
    val pendingSightingLocation: GeoPoint? = null,
    val selectedFilter: String = "Urgent Spots",
    val isShowingRoute: Boolean = false,
    val routeDestination: GeoPoint? = null
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(
        sightings = listOf(
            Sighting(
                id = "1",
                title = "Benton Junction",
                location = GeoPoint(-6.225, 106.611),
                distance = "500m away",
                category = SightingCategory.URGENT_HELP,
                status = "URGENT",
                description = "Estimated 4 cats reported here. They haven't been fed since yesterday noon. Near Family Mart",
                address = "Benton Junction, Lippo Karawaci"
            ),
            Sighting(
                id = "2",
                title = "U-Residence 1",
                location = GeoPoint(-6.226, 106.612),
                distance = "1 km away",
                category = SightingCategory.MANY_CATS,
                status = "HUNGRY",
                description = "Oh no!! estimated 4 cats reported here. Hasn't been fed yet.",
                address = "U-Residence 1"
            ),
            Sighting(
                id = "3",
                title = "U-Residence 3",
                location = GeoPoint(-6.224, 106.610),
                distance = "1 km away",
                category = SightingCategory.MANY_CATS,
                status = "MANY CATS",
                description = "Estimated 5 cats reported here.",
                address = "U-Residence 3"
            ),
            Sighting(
                id = "4",
                title = "Pet Care Plus",
                location = GeoPoint(-6.224, 106.613),
                distance = "600m away",
                category = SightingCategory.VET,
                status = "OPEN",
                description = "All animals",
                address = "Pet Care Plus"
            )
        )
    ))
    val uiState = _uiState.asStateFlow()

    fun onSightingClick(sighting: Sighting) {
        _uiState.update { it.copy(selectedSighting = sighting, isReporting = false, isShowingRoute = false) }
    }

    fun onDismissSighting() {
        _uiState.update { it.copy(selectedSighting = null, isShowingRoute = false) }
    }

    fun onReportClick() {
        _uiState.update { it.copy(isReporting = true, selectedSighting = null, isShowingRoute = false) }
    }

    fun onDismissReport() {
        _uiState.update { it.copy(isReporting = false, isPlacingSighting = false) }
    }

    fun onStartPlacingSighting() {
        _uiState.update { it.copy(isPlacingSighting = true, isReporting = false) }
    }

    fun onMapClick(geoPoint: GeoPoint) {
        if (_uiState.value.isPlacingSighting) {
            _uiState.update { 
                it.copy(
                    pendingSightingLocation = geoPoint, 
                    isReporting = true, 
                    isPlacingSighting = false,
                    reportAddress = String.format("%.4f, %.4f", geoPoint.latitude, geoPoint.longitude)
                ) 
            }
        } else {
            onDismissSighting()
        }
    }

    fun onReportAddressChange(address: String) {
        _uiState.update { it.copy(reportAddress = address) }
    }

    fun onReportCategorySelect(category: SightingCategory) {
        _uiState.update { it.copy(reportCategory = category) }
    }

    fun onReportNotesChange(notes: String) {
        if (notes.length <= 200) {
            _uiState.update { it.copy(reportNotes = notes) }
        }
    }

    fun onFilterSelect(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onGetRouteClick() {
        _uiState.update { it.copy(isShowingRoute = true, routeDestination = it.selectedSighting?.location) }
    }

    fun onSubmitReport() {
        _uiState.update { it.copy(isReporting = false, reportAddress = "", reportCategory = null, reportNotes = "") }
    }
}
