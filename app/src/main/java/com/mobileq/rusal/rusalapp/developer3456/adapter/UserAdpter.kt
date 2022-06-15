package com.mobileq.rusal.rusalapp.developer3456.adapter


import Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.listeners.UserListener
import com.mobileq.rusal.rusalapp.developer3456.model.Talent
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.squareup.picasso.Picasso

class UserAdpter(private val mList: List<User> , var userListener: UserListener) : RecyclerView.Adapter<UserAdpter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        holder.txtUserName.setText(itemsViewModel.name)
        holder.txtUserEmail.setText(itemsViewModel.email)

        Picasso.get().load(itemsViewModel.image)
            .into(holder.imageView)
        holder.itemView.setOnClickListener { view -> userListener.onUserClicked(itemsViewModel) }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_user)
        val txtUserName: TextView = itemView.findViewById(R.id.txt_userName)
        val txtUserEmail: TextView = itemView.findViewById(R.id.txt_userEmail)
    }
}
