package com.mobileq.rusal.rusalapp.developer3456.adapter


import Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.model.Talent
import com.squareup.picasso.Picasso

class TalentAdpter(private val mList: List<Talent>) : RecyclerView.Adapter<TalentAdpter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.talent_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        holder.textViewDec.setText(itemsViewModel.studentTalent)
        holder.txtStudentName.setText(itemsViewModel.studentName)

        Picasso.get().load(itemsViewModel.studentImage)
            .into(holder.imageView)


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.im_StudentTalent)
        val textViewDec: TextView = itemView.findViewById(R.id.txt_StudentDecTalent)
        val txtStudentName: TextView = itemView.findViewById(R.id.txt_StudentNameTalent)
    }
}
