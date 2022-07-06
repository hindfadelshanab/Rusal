package com.mobileq.rusal.rusalapp.developer3456.home_feature

data class NewPost (
    val docId: String = "",
    val content: String = "",
    val time: Long = System.currentTimeMillis(),
    val club: String = "",
    val imageUrl: String = "",
    val likes: MutableList<String> = mutableListOf(),
    val authorDocId: String = ""
)