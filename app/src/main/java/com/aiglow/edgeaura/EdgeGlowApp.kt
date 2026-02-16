package com.aiglow.edgeaura

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class EdgeAuraApp : Application() {
    
    companion object {
        const val CHANNEL_ID = "edge_glow_channel"
        const val CHANNEL_NAME = "Edge Glow Service"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps Edge Glow running in the background"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
