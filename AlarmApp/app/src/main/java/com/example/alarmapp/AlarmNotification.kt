package com.example.alarmapp

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

// Notification ID.
private const val NOTIFICATION_ID = 1

object AlarmNotification {

    fun createNotification(
            context: Context
    ) : Notification {
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
                context,
                context.getString(R.string.notification_channel_id)
        )

        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText("동일 간격 노티 알람 만들기!!")
        builder.setContentIntent(contentPendingIntent)
//        builder.setAutoCancel(true)
//        builder.setOngoing(true)

        builder.priority = NotificationCompat.PRIORITY_HIGH
        Log.d("kyi123", "made builder.build()")
        return builder.build()
    }
}