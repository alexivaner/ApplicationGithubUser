package com.example.applicationgithubuser.adapter

import android.os.Parcel
import android.os.Parcelable

data class UserGithub(
    var avatar: String? = "",
    var username: String? ="",
    var userid:String?="",
    var url:String?=""


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeString(username)
        parcel.writeString(userid)
        parcel.writeString(url)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserGithub> {
        override fun createFromParcel(parcel: Parcel): UserGithub {
            return UserGithub(parcel)
        }

        override fun newArray(size: Int): Array<UserGithub?> {
            return arrayOfNulls(size)
        }
    }
}