package com.example.alarmapp

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log

// Notification ID.

private const val NOTIFICATION_ID = 2 // 0이면 절대 안됨. 노티가 안나온다.

class MyService : Service() {
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    // 서비스쪽으로 데이터를 전달하고 싶은경우 자주 사용
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("kyi123", "onStartCommand()")

        if (intent != null) {
            startOnGoingNotification()
        } else {
            return Service.START_STICKY
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startOnGoingNotification() {
        Log.d("kyi123", "startForegroundService()")
        val notification = AlarmNotification.createNotification(this) // foreground Noti
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("kyi123", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("kyi123", "onDestroy")
    }
}
