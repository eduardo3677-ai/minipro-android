package com.echosmart.flashlabs.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.echosmart.flashlabs.R

class ProgrammerService : Service() {

    private val binder = LocalBinder()
    private val channelId = "FlashLabs_Service_Channel"

    inner class LocalBinder : Binder() {
        fun getService(): ProgrammerService = this@ProgrammerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundServiceNotification()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FlashLabs T48 Hardware Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceNotification() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("FlashLabs T48 Running")
            .setContentText("Hardware USB OTG Connection Active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1001, notification)
    }
}
