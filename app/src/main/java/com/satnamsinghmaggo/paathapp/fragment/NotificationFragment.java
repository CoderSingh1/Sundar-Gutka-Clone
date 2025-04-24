package com.satnamsinghmaggo.paathapp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.ReminderReceiver;

import java.util.Calendar;

public class NotificationFragment extends Fragment {

    private Button btnMorning, btnEvening, btnNight, btnAddCustom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnMorning = view.findViewById(R.id.btnMorning);
        btnEvening = view.findViewById(R.id.btnEvening);
        btnNight = view.findViewById(R.id.btnNight);
        btnAddCustom = view.findViewById(R.id.btnAddCustomReminder);

        // Request notification permission and create channel
        requestNotificationPermission();
        createNotificationChannel();

        SharedPreferences prefs = requireContext().getSharedPreferences("paath_prefs", Context.MODE_PRIVATE);
        boolean defaultSet = prefs.getBoolean("default_alarms_set", false);
        if (!defaultSet) {
            setReminder("Japji Sahib", 7, 0);
            setReminder("Rehras Sahib", 17, 0);
            setReminder("Kirtan Sohila", 21, 30);
            prefs.edit().putBoolean("default_alarms_set", true).apply();
        }

        btnMorning.setOnClickListener(v -> showTimePicker("morning"));
        btnEvening.setOnClickListener(v -> showTimePicker("evening"));
        btnNight.setOnClickListener(v -> showTimePicker("night"));
        btnAddCustom.setOnClickListener(v -> showTimePicker("custom"));
    }

    private void showTimePicker(String type) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new android.app.TimePickerDialog(getContext(), (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
            String time = selectedHour + ":" + (selectedMinute < 10 ? "0" : "") + selectedMinute;

            String baniName = "";
            switch (type) {
                case "morning":
                    baniName = "Japji Sahib";
                    btnMorning.setText("Set: " + time);
                    break;
                case "evening":
                    baniName = "Rehras Sahib";
                    btnEvening.setText("Set: " + time);
                    break;
                case "night":
                    baniName = "Kirtan Sohila";
                    btnNight.setText("Set: " + time);
                    break;
                case "custom":
                    baniName = "Custom Bani";
                    btnAddCustom.setText("Custom: " + time);
                    break;
            }

            setReminder(baniName, selectedHour, selectedMinute);
            Toast.makeText(getContext(), baniName + " reminder set for " + time, Toast.LENGTH_SHORT).show();
        }, hour, minute, true).show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "bani_reminders", "Daily Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder(String baniName, int hour, int minute) {
        scheduleDailyNotification(
                requireContext(),
                hour,
                minute,
                baniName.hashCode(),
                "Paath Reminder",
                "It's time to read " + baniName
        );
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleDailyNotification(Context context, int hour, int minute, int requestCode, String title, String message) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("bani_name", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
