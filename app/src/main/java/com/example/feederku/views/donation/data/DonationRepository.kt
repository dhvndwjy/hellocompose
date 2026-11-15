package com.example.feederku.views.donation.data

import com.example.feederku.views.donation.model.DonationCampaign
import com.example.feederku.views.donation.model.Donor
import kotlinx.coroutines.flow.Flow

interface DonationRepository {
    fun getCampaigns(): Flow<List<DonationCampaign>>
    suspend fun getCampaignById(id: String): DonationCampaign?
    fun getRecentDonors(campaignId: String): Flow<List<Donor>>
    suspend fun registerCampaign(campaign: DonationCampaign): Result<Unit>
    suspend fun submitDonation(campaignId: String, amount: Long, donorName: String): Result<Unit>
}
