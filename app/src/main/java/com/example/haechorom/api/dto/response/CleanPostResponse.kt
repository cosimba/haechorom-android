package com.example.haechorom.api.dto.response

import android.os.Parcel
import android.os.Parcelable

data class CleanPostResponse(
    val id: Long,
    val cleanName: String,
    val serialNumber: String,
    val josaId: Long,
    val cleanDate: String,
    val coastName: String,
    val lat: Double,
    val lng: Double,
    val coastLength: Int,
    val collectBag: Int,
    val collectVal: Int,
    val trashType: String,
    val cleanStatus: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(cleanName)
        parcel.writeString(serialNumber)
        parcel.writeLong(josaId)
        parcel.writeString(cleanDate)
        parcel.writeString(coastName)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeInt(coastLength)
        parcel.writeInt(collectBag)
        parcel.writeInt(collectVal)
        parcel.writeString(trashType)
        parcel.writeString(cleanStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CleanPostResponse> {
        override fun createFromParcel(parcel: Parcel): CleanPostResponse {
            return CleanPostResponse(parcel)
        }

        override fun newArray(size: Int): Array<CleanPostResponse?> {
            return arrayOfNulls(size)
        }
    }
}
