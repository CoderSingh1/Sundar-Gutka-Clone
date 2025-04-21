package com.satnamsinghmaggo.paathapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bani implements Parcelable {
    private String name;
    private String time;

    public Bani(String name, String time) {
        this.name = name;
        this.time = time;
    }

    protected Bani(Parcel in) {
        name = in.readString();
        time = in.readString();
    }

    public static final Creator<Bani> CREATOR = new Creator<Bani>() {
        @Override
        public Bani createFromParcel(Parcel in) {
            return new Bani(in);
        }

        @Override
        public Bani[] newArray(int size) {
            return new Bani[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
