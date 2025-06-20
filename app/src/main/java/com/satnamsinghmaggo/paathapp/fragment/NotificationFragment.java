package com.satnamsinghmaggo.paathapp.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.ReminderReceiver;
import com.satnamsinghmaggo.paathapp.model.Reminder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationFragment extends Fragment {

    private EditText etBaniTitle;
    private TextView tvTime;
    private LinearLayout reminderContainer;

    private LayoutInflater layoutInflater;
    private final String PREF_REMINDERS = "reminders";
    private final Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private List<Reminder> reminderList = new ArrayList<>();
    private ActivityResultLauncher<Intent> exactAlarmPermissionLauncher;
    private final String PREF_FIRST_VISIT = "first_visit_done";

    private ActivityResultLauncher<String> notificationPermissionLauncher;


    @SuppressLint("ScheduleExactAlarm")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        etBaniTitle = view.findViewById(R.id.etBaniTitle);
        tvTime = view.findViewById(R.id.tvTime);
        Button btnAddReminder = view.findViewById(R.id.btnAddCustomReminder);
        reminderContainer = view.findViewById(R.id.reminderContainer);
        layoutInflater = LayoutInflater.from(getContext());
        sharedPreferences = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        loadReminders();
        tvTime.setText(DateFormat.format("hh:mm aa", Calendar.getInstance()));

        exactAlarmPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (canScheduleExactAlarms()) {
                        Toast.makeText(requireContext(), "Permission granted! Scheduling alarms...", Toast.LENGTH_SHORT).show();
                        for (Reminder reminder : reminderList) {
                            if (reminder.isEnabled) {
                                int hour = getHour(reminder.time);
                                int minute = getMinute(reminder.time);
                                scheduleDailyNotification(requireContext(), hour, minute, reminder.requestCode, reminder.title, "Reminder: " + reminder.title);
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Exact Alarm permission still not granted", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show();
                        addReminder();  // Retry after permission granted
                    } else {
                        Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );



        tvTime.setOnClickListener(v -> showTimePickerDialog());

        btnAddReminder.setOnClickListener(v -> addReminder());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            boolean firstVisitDone = sharedPreferences.getBoolean(PREF_FIRST_VISIT, false);

            if (!firstVisitDone) {
                sharedPreferences.edit().putBoolean(PREF_FIRST_VISIT, true).apply();  // mark as visited
                if (!canScheduleExactAlarms() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    exactAlarmPermissionLauncher.launch(intent);
                }
            }
        }
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);




        TimePickerDialog timePickerDialog = new TimePickerDialog(
                new ContextThemeWrapper(getContext(), R.style.CustomTimePickerDialog),
                (view, hourOfDay, minute1) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                            (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                            minute1,
                            (hourOfDay < 12) ? "AM" : "PM");
                    tvTime.setText(formattedTime);
                },
                hour,
                minute,
                false
        );

        timePickerDialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void addReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }

        String title = etBaniTitle.getText().toString().trim();
        String time = tvTime.getText().toString().trim();

        if (title.isEmpty()) {
            etBaniTitle.setError("Title required");
            return;
        }

        View reminderView = layoutInflater.inflate(R.layout.item_reminder_card, null, false);

        EditText etTitle = reminderView.findViewById(R.id.etBaniTitle);
        TextView tvReminderTime = reminderView.findViewById(R.id.tvTime);
        SwitchCompat switchReminder = reminderView.findViewById(R.id.switchReminder);
        Button btnDelete = reminderView.findViewById(R.id.btnDeleteReminder);

        etTitle.setText(title);
        etTitle.setEnabled(false);
        tvReminderTime.setText(time);

        // Parse time string to hour and minute
        int hour = 10, minute = 0; // Default fallback
        try {
            String[] parts = time.split("[: ]");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
            if (parts[2].equalsIgnoreCase("PM") && hour != 12) hour += 12;
            if (parts[2].equalsIgnoreCase("AM") && hour == 12) hour = 0;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid time format", Toast.LENGTH_SHORT).show();
            return;
        }




        int requestCode = (int) System.currentTimeMillis();

        if (switchReminder.isChecked()) {
            if (canScheduleExactAlarms()) {
                scheduleDailyNotification(requireContext(), hour, minute, requestCode, title, "Reminder: " + title);
                Toast.makeText(getContext(), "Reminder Set", Toast.LENGTH_SHORT).show();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                exactAlarmPermissionLauncher.launch(intent);
                Toast.makeText(getContext(), "Please allow alarm permission to set reminders.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Reminder reminder = new Reminder(title, time, switchReminder.isChecked(), requestCode);
        reminderList.add(reminder);
        saveReminders();

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.isEnabled = isChecked;
            int hour1 = getHour(reminder.time);
            int minute1 = getMinute(reminder.time);
            if (isChecked) {
                if (canScheduleExactAlarms()) {
                    scheduleDailyNotification(requireContext(), hour1, minute1, reminder.requestCode, reminder.title, "Reminder: " + reminder.title);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    exactAlarmPermissionLauncher.launch(intent);
                    switchReminder.setChecked(false);
                }
            } else {
                cancelReminder(reminder.requestCode);
            }
            saveReminders();
        });


        btnDelete.setOnClickListener(v -> {
            reminderContainer.removeView(reminderView);
            cancelReminder(reminder.requestCode);

            for (int i = 0; i < reminderList.size(); i++) {
                if (reminderList.get(i).requestCode == reminder.requestCode) {
                    reminderList.remove(i);
                    break;
                }
            }

            saveReminders();
        });

        reminderContainer.addView(reminderView);

        etBaniTitle.setText("");

    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleDailyNotification(Context context, int hour, int minute, int requestCode, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("daily_channel", "Daily Notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    private void saveReminders() {
        String json = gson.toJson(reminderList);
        sharedPreferences.edit().putString(PREF_REMINDERS, json).apply();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void loadReminders() {
        String json = sharedPreferences.getString(PREF_REMINDERS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();
            reminderList = gson.fromJson(json, type);

            for (Reminder reminder : reminderList) {
                renderReminder(reminder); // show on UI
                if (reminder.isEnabled) {
                    int hour = getHour(reminder.time);
                    int minute = getMinute(reminder.time);
                    scheduleDailyNotification(requireContext(), hour, minute, reminder.requestCode, reminder.title, "Reminder: " + reminder.title);
                }
            }
        }
    }

    private int getHour(String time) {
        String[] parts = time.split("[: ]");
        int hour = Integer.parseInt(parts[0]);
        if (parts[2].equalsIgnoreCase("PM") && hour != 12) hour += 12;
        if (parts[2].equalsIgnoreCase("AM") && hour == 12) hour = 0;
        return hour;
    }

    private int getMinute(String time) {
        String[] parts = time.split("[: ]");
        return Integer.parseInt(parts[1]);
    }

    private void cancelReminder(int requestCode) {
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(), requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    @SuppressLint("ScheduleExactAlarm")
    private void renderReminder(Reminder reminder) {
        View reminderView = layoutInflater.inflate(R.layout.item_reminder_card, null, false);

        EditText etTitle = reminderView.findViewById(R.id.etBaniTitle);
        TextView tvReminderTime = reminderView.findViewById(R.id.tvTime);
        SwitchCompat switchReminder = reminderView.findViewById(R.id.switchReminder);
        Button btnDelete = reminderView.findViewById(R.id.btnDeleteReminder);

        etTitle.setText(reminder.title);
        etTitle.setEnabled(false);
        tvReminderTime.setText(reminder.time);
        switchReminder.setChecked(reminder.isEnabled);

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.isEnabled = isChecked;
            int hour = getHour(reminder.time);
            int minute = getMinute(reminder.time);
            if (isChecked) {
                if (canScheduleExactAlarms()) {
                    scheduleDailyNotification(requireContext(), hour, minute, reminder.requestCode, reminder.title, "Reminder: " + reminder.title);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    exactAlarmPermissionLauncher.launch(intent);
                }
            } else {
                cancelReminder(reminder.requestCode);
            }

            saveReminders();
        });

        btnDelete.setOnClickListener(v -> {
            reminderContainer.removeView(reminderView);
            cancelReminder(reminder.requestCode);

            // Remove by matching requestCode instead of object reference
            for (int i = 0; i < reminderList.size(); i++) {
                if (reminderList.get(i).requestCode == reminder.requestCode) {
                    reminderList.remove(i);
                    break;
                }
            }

            saveReminders();
        });

        reminderContainer.addView(reminderView);
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }


}
