package com.example.alarmapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private int NOTIFICATION_ID = 1;
    private int REQUEST_CODE = 0;
    private int FLAGS = 0;

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람을 BroadcastReceiver로 받는다.
        // 받으면 Noti를 주면됨.
        this.context = context;
        Log.d("kyi123", "Alarm Received");
        doNotify(); // 노티
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alim); // 소리
//        mediaPlayer.start();
        ringVibration(); // 진동
    }
//
    private void doNotify() {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                        context,
                        NOTIFICATION_ID,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                context.getString(R.string.notification_channel_id)
        );

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText("동일 간격 노티 알람 만들기!!");
        builder.setContentIntent(contentPendingIntent);
        builder.setAutoCancel(true);
//        builder.setOngoing(true); // Ongoing 붙이면 wear로 noti 안감.

//        builder.setStyle(R.drawable.ic_launcher_background);
//        builder.setLargeIcon(R.drawable.ic_launcher_background);
//        builder.addAction(R.drawable.ic_launcher_foreground,
//                context.getString(R.string.app_name),
//                );
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager notificationManager = context.getSystemService(
                NotificationManager.class
        );
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void ringVibration() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] timings = new long[]{1000L, 1000L, 1000L, 1000L, 1000L};
        int[] amplitudes = new int[]{0, 100, 200, 100, 200};
        //vibrator.vibrate(VibrationEffect.createOneShot(1000, 50));
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes,-1));
    }

}
