package com.example.feederku.views.livestream.webrtc

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSignalingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createRoom(roomId: String, offerSdp: String) {
        val roomMap = mapOf(
            "offer" to mapOf("type" to "offer", "sdp" to offerSdp),
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("livestreams").document(roomId).set(roomMap).await()
    }

    suspend fun getOffer(roomId: String): String? {
        val doc = db.collection("livestreams").document(roomId).get().await()
        val offer = doc.get("offer") as? Map<*, *>
        return offer?.get("sdp") as? String
    }

    suspend fun saveAnswer(roomId: String, answerSdp: String) {
        val answerMap = mapOf("answer" to mapOf("type" to "answer", "sdp" to answerSdp))
        db.collection("livestreams").document(roomId).update(answerMap).await()
    }

    suspend fun addIceCandidate(roomId: String, candidate: String, sdpMid: String, sdpMLineIndex: Int, isStreamer: Boolean) {
        val candidateMap = mapOf(
            "candidate" to candidate,
            "sdpMid" to sdpMid,
            "sdpMLineIndex" to sdpMLineIndex,
            "sender" to if (isStreamer) "streamer" else "viewer"
        )
        db.collection("livestreams").document(roomId)
            .collection("candidates")
            .add(candidateMap)
            .await()
    }
}
