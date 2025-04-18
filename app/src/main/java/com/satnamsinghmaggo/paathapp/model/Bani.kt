package com.satnamsinghmaggo.paathapp.model

import android.os.Parcel
import android.os.Parcelable

data class Bani(val name: String, val time: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bani> {
        override fun createFromParcel(parcel: Parcel): Bani {
            return Bani(parcel)
        }

        override fun newArray(size: Int): Array<Bani?> {
            return arrayOfNulls(size)
        }
    }
} 