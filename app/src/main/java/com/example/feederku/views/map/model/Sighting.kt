package com.example.feederku.views.map.model

import org.osmdroid.util.GeoPoint

enum class SightingCategory {
    MANY_CATS, URGENT_HELP, ALREADY_FED, VET
}

data class Sighting(
    val id: String = "",
    val title: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val distance: String = "",
    val category: SightingCategory = SightingCategory.MANY_CATS,
    val description: String = "",
    val status: String = "", // e.g., "URGENT", "HUNGRY", "FED", "OPEN"
    val imageUrl: String? = null,
    val address: String = ""
)
