package com.mobileq.rusal.rusalapp.developer3456.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import java.util.*
import kotlin.collections.ArrayList

class Chat (

    var converstionId: String?=null,
    var converstionName:String? = null,
    var users:ArrayList<User> ? =null,

):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        TODO("users")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(converstionId)
        parcel.writeString(converstionName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }

}