package com.mobileq.rusal.rusalapp.developer3456.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
class User (


    var id: String? = null,
    var name: String? = null,
    var image: String? = null,
    var email: String? = null,
    var club: String? = null,
    var accountType: String? = null,


    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(email)
        parcel.writeString(club)
        parcel.writeString(accountType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}