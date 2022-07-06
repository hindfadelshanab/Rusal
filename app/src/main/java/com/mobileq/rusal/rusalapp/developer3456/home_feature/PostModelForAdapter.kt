package com.mobileq.rusal.rusalapp.developer3456.home_feature

import com.mobileq.rusal.rusalapp.developer3456.model.Comment
import com.mobileq.rusal.rusalapp.developer3456.model.User

data class PostModelForAdapter(
    val author: User,
    val post: NewPost,
    val comment: List<Comment> = listOf()
)
