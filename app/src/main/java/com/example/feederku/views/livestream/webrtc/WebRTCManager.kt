package com.example.feederku.views.livestream.webrtc

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebRTCManager(private val context: Context) {

    private val signalingRepository = FirebaseSignalingRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var isStreaming: Boolean = false
        private set

    private val iceServers = listOf(
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302"
    )

    fun startLocalCamera(roomId: String = "default_room") {
        isStreaming = true
        Log.d("WebRTCManager", "Kamera lokal aktif. Menghubungkan ke STUN Server: ${iceServers[0]}")
        
        scope.launch {
            try {
                signalingRepository.createRoom(roomId, "sample_sdp_offer_placeholder")
                Log.d("WebRTCManager", "Room $roomId created on Firebase Firestore signaling.")
            } catch (e: Exception) {
                Log.e("WebRTCManager", "Error setting up signaling room", e)
            }
        }
    }

    fun stopCamera() {
        isStreaming = false
        Log.d("WebRTCManager", "Siaran dihentikan, kamera ditutup.")
    }
}
