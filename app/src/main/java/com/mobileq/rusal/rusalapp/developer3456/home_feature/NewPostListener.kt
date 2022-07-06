package com.mobileq.rusal.rusalapp.developer3456.new_home_feature

import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.model.User

interface NewPostListener {
    fun onClickLike(post: NewPost)
    fun onClickComment(post: NewPost)
    fun onClickImage(post: NewPost)
    fun onClickAuthor(author:User)
}