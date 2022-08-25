package com.example.workmanagertutorial

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class OurBackgroundWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val channel =
            NotificationChannel(
                "example_channel_worker",
                "Test channel worker",
                NotificationManager.IMPORTANCE_LOW
            )
        channel.description = "Test foreground service"
        notificationManager.createNotificationChannel(channel)

        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification: Notification =
            Notification.Builder(applicationContext, "example_channel_worker")
                .setContentTitle("Test")
                .setContentText("This work belongs to ${applicationContext.packageName}")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .addAction(
                    Notification.Action.Builder(
                        Icon.createWithResource(
                            applicationContext,
                            android.R.drawable.ic_delete
                        ), "Cancel", intent
                    ).build()
                )
                .build()

        setForeground(ForegroundInfo(id.hashCode(), notification))
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, "Work has started", Toast.LENGTH_SHORT).show()
        }

        delay(10 * 1000)

        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, "Work has ended", Toast.LENGTH_SHORT).show()
        }

        return Result.success()
    }
}