package com.satnamsinghmaggo.paathapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bani implements Parcelable {
    private String name;


    public Bani(String name) {
        this.name = name;

    }

    protected Bani(Parcel in) {
        name = in.readString();
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


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
