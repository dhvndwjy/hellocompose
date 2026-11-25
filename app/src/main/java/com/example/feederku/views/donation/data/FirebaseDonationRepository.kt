package com.example.feederku.views.donation.data

import com.example.feederku.views.donation.model.DonationCampaign
import com.example.feederku.views.donation.model.Donor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDonationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : DonationRepository {

    override fun getCampaigns(): Flow<List<DonationCampaign>> = callbackFlow {
        val subscription = db.collection("campaigns")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val campaigns = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(DonationCampaign::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                if (campaigns.isEmpty()) {
                    trySend(
                        listOf(
                            DonationCampaign(
                                id = "camp_1",
                                title = "Emergency Medical Care for Rescued Stray Cats",
                                organizationName = "Jakarta Animal Aid",
                                description = "Help us fund emergency surgeries and medications for rescued stray cats suffering from severe injuries.",
                                targetAmount = 5000000,
                                collectedAmount = 3450000,
                                category = "Medical",
                                isVerified = true,
                                daysRemaining = 12,
                                donorCount = 48
                            ),
                            DonationCampaign(
                                id = "camp_2",
                                title = "Monthly Food Supply for 100 Sheltered Cats",
                                organizationName = "Paws & Claws Shelter",
                                description = "Providing nutritious dry and wet food for over 100 cats currently sheltered at our rescue center.",
                                targetAmount = 10000000,
                                collectedAmount = 7800000,
                                category = "Feeding",
                                isVerified = true,
                                daysRemaining = 5,
                                donorCount = 92
                            )
                        )
                    )
                } else {
                    trySend(campaigns)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getCampaignById(id: String): DonationCampaign? {
        return try {
            val doc = db.collection("campaigns").document(id).get().await()
            if (doc.exists()) {
                doc.toObject(DonationCampaign::class.java)?.copy(id = doc.id)
            } else {
                // Fallback for sample campaigns if not in Firestore yet
                when (id) {
                    "camp_1" -> DonationCampaign(
                        id = "camp_1",
                        title = "Emergency Medical Care for Rescued Stray Cats",
                        organizationName = "Jakarta Animal Aid",
                        description = "Help us fund emergency surgeries and medications for rescued stray cats suffering from severe injuries.",
                        targetAmount = 5000000,
                        collectedAmount = 3450000,
                        category = "Medical",
                        isVerified = true,
                        daysRemaining = 12,
                        donorCount = 48
                    )
                    "camp_2" -> DonationCampaign(
                        id = "camp_2",
                        title = "Monthly Food Supply for 100 Sheltered Cats",
                        organizationName = "Paws & Claws Shelter",
                        description = "Providing nutritious dry and wet food for over 100 cats currently sheltered at our rescue center.",
                        targetAmount = 10000000,
                        collectedAmount = 7800000,
                        category = "Feeding",
                        isVerified = true,
                        daysRemaining = 5,
                        donorCount = 92
                    )
                    else -> null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun getRecentDonors(campaignId: String): Flow<List<Donor>> = callbackFlow {
        val subscription = db.collection("donations")
            .whereEqualTo("campaignId", campaignId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val donors = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Donor::class.java)
                } ?: emptyList()
                
                if (donors.isEmpty()) {
                    trySend(
                        listOf(
                            Donor("Budi Santoso", 100000, System.currentTimeMillis() - 3600000),
                            Donor("Siti Aminah", 250000, System.currentTimeMillis() - 7200000),
                            Donor("Ahmad Fauzi", 50000, System.currentTimeMillis() - 14400000)
                        )
                    )
                } else {
                    trySend(donors)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun registerCampaign(campaign: DonationCampaign): Result<Unit> {
        return try {
            db.collection("campaigns").add(campaign).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitDonation(campaignId: String, amount: Long, donorName: String): Result<Unit> {
        return try {
            val donationData = hashMapOf(
                "campaignId" to campaignId,
                "amount" to amount,
                "name" to donorName,
                "timestamp" to System.currentTimeMillis()
            )
            
            val batch = db.batch()
            
            val donationRef = db.collection("donations").document()
            batch.set(donationRef, donationData)
            
            val campaignRef = db.collection("campaigns").document(campaignId)
            batch.update(campaignRef, "collectedAmount", FieldValue.increment(amount))
            batch.update(campaignRef, "donorCount", FieldValue.increment(1))
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
