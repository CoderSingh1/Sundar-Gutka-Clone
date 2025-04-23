package com.satnamsinghmaggo.paathapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String baniName = intent.getStringExtra("bani_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "bani_reminders")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Paath Reminder")
                .setContentText("It's time to read " + baniName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

