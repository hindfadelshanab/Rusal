package com.mobileq.rusal.rusalapp.developer3456.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.mobileq.rusal.rusalapp.developer3456.R


import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.IntroSlide

class IntroSliderAdapter(private val introSlides: List<IntroSlide>) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSlideViewHolder>() {

    inner class IntroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {

      //  private val textTitle = view.findViewById<TextView>(R.id.imageView1)
        private val textDescription = view.findViewById<TextView>(R.id.text_slid)
        private val textDescription2 = view.findViewById<TextView>(R.id.text_slid2)
        private val imageIcon = view.findViewById<ImageView>(R.id.imageView1)

        fun bind(introSlide: IntroSlide) {

            textDescription.text = introSlide.description
            textDescription2.text = introSlide.description2
            imageIcon.setImageResource(introSlide.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.slide_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }
}