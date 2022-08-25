package com.example.workmanagertutorial

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.OutOfQuotaPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForegroundService : Service() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                "example_channel_foreground",
                "Test channel foreground",
                NotificationManager.IMPORTANCE_LOW
            )
        channel.description = "Test foreground service"
        notificationManager.createNotificationChannel(channel)


        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                PendingIntent.getActivity(
                    this, 11, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification: Notification = Notification.Builder(this, "example_channel_foreground")
            .setContentTitle("Test")
            .setContentText("This foreground service belongs to $packageName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        startForeground(121, notification)
        Toast.makeText(this, "Started foreground service", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            delay(10 * 1000)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "Stopped foreground service", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}