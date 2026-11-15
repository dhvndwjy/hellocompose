package com.example.feederku.views.donation.model

data class DonationCampaign(
    val id: String = "",
    val title: String = "",
    val organizationName: String = "",
    val description: String = "",
    val targetAmount: Long = 0,
    val collectedAmount: Long = 0,
    val imageUrl: String = "",
    val category: String = "",
    val isVerified: Boolean = false,
    val daysRemaining: Int = 0,
    val donorCount: Int = 0,
    val startDate: Long = 0,
    val endDate: Long = 0
)

data class Donor(
    val name: String = "",
    val amount: Long = 0,
    val timestamp: Long = 0,
    val profileImageUrl: String? = null
)

data class DonationReview(
    val campaignName: String = "",
    val donationAmount: Long = 0,
    val platformFee: Long = 2000,
    val donorName: String = "",
    val paymentMethod: String = ""
)
