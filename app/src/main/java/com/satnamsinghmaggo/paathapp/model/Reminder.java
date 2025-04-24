package com.satnamsinghmaggo.paathapp.model;

public class Reminder {
    public String title;
    public String time;
    public boolean isEnabled;
    public int requestCode;


    public Reminder(String title, String time, boolean isEnabled, int requestCode) {
        this.title = title;
        this.time = time;
        this.isEnabled = isEnabled;
        this.requestCode = requestCode;

    }
}
