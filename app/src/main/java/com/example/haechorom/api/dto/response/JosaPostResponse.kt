package com.example.haechorom.api.dto.response

import android.os.Parcel
import android.os.Parcelable

data class JosaPostResponse(
    val id: Long,
    val createAt: String,
    val updateAt: String,
    val deletedAt: String,
    val serialNumber: String,
    val josaName: String,
    val josaDate: String,
    val lat: Double,
    val lng: Double,
    val coastName: String,
    val coastLength: Int,
    val collectBag: Int,
    val trashType: String,
    val josaStatus: String,
    val deleted: Boolean
)  : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(createAt)
        parcel.writeString(updateAt)
        parcel.writeString(deletedAt)
        parcel.writeString(serialNumber)
        parcel.writeString(josaName)
        parcel.writeString(josaDate)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(coastName)
        parcel.writeInt(coastLength)
        parcel.writeInt(collectBag)
        parcel.writeString(trashType)
        parcel.writeString(josaStatus)
        parcel.writeByte(if (deleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JosaPostResponse> {
        override fun createFromParcel(parcel: Parcel): JosaPostResponse {
            return JosaPostResponse(parcel)
        }

        override fun newArray(size: Int): Array<JosaPostResponse?> {
            return arrayOfNulls(size)
        }
    }
}
