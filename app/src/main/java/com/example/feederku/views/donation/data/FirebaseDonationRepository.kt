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
                trySend(campaigns)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getCampaignById(id: String): DonationCampaign? {
        return try {
            val doc = db.collection("campaigns").document(id).get().await()
            doc.toObject(DonationCampaign::class.java)?.copy(id = doc.id)
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
                trySend(donors)
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
            
            // Add donation record
            val donationRef = db.collection("donations").document()
            batch.set(donationRef, donationData)
            
            // Update campaign stats
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
