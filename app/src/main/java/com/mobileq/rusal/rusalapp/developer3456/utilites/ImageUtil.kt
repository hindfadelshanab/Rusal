package com.mobileq.rusal.rusalapp.developer3456.utilites

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso

fun ImageView.loadImageUrl(context: Context, url: String) = apply {
    if (url.isEmpty()) return@apply
    Picasso
        .get()
        .load(url)
        .placeholder(createCircularProgressDrawable(context))
        .into(this)
}

fun createCircularProgressDrawable(context: Context): CircularProgressDrawable {
    Log.d("TAG", "createCircularProgressDrawable: ")
    val cpd: CircularProgressDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 30f
        arrowEnabled = true
        start()
    }
    return cpd
}

fun ImageView.load(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(createCircularProgressDrawable(context))
        .into(this);
}