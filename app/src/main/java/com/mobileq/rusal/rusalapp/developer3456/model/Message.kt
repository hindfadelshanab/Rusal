package com.mobileq.rusal.rusalapp.developer3456.model

import com.google.firebase.firestore.DocumentId
import java.util.*
import kotlin.collections.ArrayList

class Message {


    var messageId: String? = null
    var senderId: String? = null
    var senderName: String? = null
    var senderImage: String? = null
    var message: String? = null
    var dateTime: String? = null
    var users: ArrayList<User>? =null
    var fromClub :String? =null
    var receiverId: String? = null
    var receiverName: String? = null
    var receiverImage: String? = null
}