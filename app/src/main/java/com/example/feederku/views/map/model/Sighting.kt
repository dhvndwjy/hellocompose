package com.example.feederku.views.map.model

import org.maplibre.android.geometry.LatLng

enum class SightingCategory(val label: String) {
    MANY_CATS("Many Cats"),
    URGENT_HELP("Urgent Help"),
    ALREADY_FED("Already Fed"),
    VET("Vet")
}

enum class SightingFilter(val label: String){
    URGENT_SPOTS("Urgent Spots"),
    RECENTLY_FED("Recently Fed"),
    MANY_CATS("Many Cats"),
    VET("Vet")
}
data class Sighting(
    val id: String = "",
    val title: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
    val distance: String = "",
    val category: SightingCategory = SightingCategory.MANY_CATS,
    val description: String = "",
    val status: String = "", // e.g., "URGENT", "HUNGRY", "FED", "OPEN"
    val imageUrl: String? = null,
    val address: String = ""
)
