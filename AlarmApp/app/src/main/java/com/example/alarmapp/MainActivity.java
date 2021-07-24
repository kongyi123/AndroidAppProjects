package com.example.alarmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
        );

        Log.d("kyi123", "here!");
    }

    public void onClick(View view) {
        EditText et = findViewById(R.id.notification_content);
        AlarmNotification.INSTANCE.setText(et.getText().toString());

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.putExtra("command", "show");
//        startService(intent);
        startForegroundService(intent); // foreground service 실행을 위해 이것만 있으면 됨. 윗줄의 startService(intent)는 필요 없음.
        diaryNotification();
    }

    public void onClick2(View view) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("state","alarm on");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        // 알람 Broadcast Intent를 만든다. -> alarmManager를 통해 특정시각에 broadcast 날리도록 예약할것이다.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 사용자가 매일 알람을 허용했다면
        if (alarmManager != null) {
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); // 예약
            alarmManager.cancel(pendingIntent);
        }
    }

    public void diaryNotification() {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("state","alarm on");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        // 알람 Broadcast Intent를 만든다. -> alarmManager를 통해 특정시각에 broadcast 날리도록 예약할것이다.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 사용자가 매일 알람을 허용했다면

        EditText et = findViewById(R.id.edit_text_millisecond);
        String text = et.getText().toString();
        Long sec = Long.parseLong(text);

        if (dailyNotify) {
            if (alarmManager != null) {
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); // 예약
                Log.d("kyi123", "sec = " + sec);
                alarmManager.setInexactRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime(),
                        sec,
                        pendingIntent);
            }
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    void createChannel(String channelId, String channelName) {
        Log.d("kyi123", "createChannel");
        NotificationChannel notificationChannel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setShowBadge(false);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription(getString(R.string.breakfast_notification_channel_description));
        NotificationManager notificationManager = getSystemService(
                NotificationManager.class
        );
        notificationManager.createNotificationChannel(notificationChannel);
    }
}