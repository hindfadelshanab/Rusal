package com.mobileq.rusal.rusalapp.developer3456.adapter


import Post
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class PostAdpter(private val mList: List<Post>, var context: FragmentActivity? , var userId:String , var  postListener: PostListener) : RecyclerView.Adapter<PostAdpter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }


    var usersLikePostList:ArrayList<String> = ArrayList()

    var db: FirebaseFirestore =FirebaseFirestore.getInstance()

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        if (itemsViewModel.postImage==""){
            holder.imageView.visibility=View.GONE
            holder.progressBar.visibility=View.GONE
        }else if (itemsViewModel.postImage !=null) {
            Picasso.get()
                .load(itemsViewModel.postImage)
                .into(holder.imageView , object :Callback{
                    override fun onSuccess() {
                        holder.imageView.visibility =View.VISIBLE
                        holder.progressBar.visibility =View.GONE

                    }

                    override fun onError(e: Exception?) {

                    }
                })

        }
        holder.itemView.setOnClickListener { view -> postListener.onPostClicked(itemsViewModel) }

        holder.textViewDec.text = itemsViewModel.postDec
        holder.textViewNumberOfLick.text = itemsViewModel.numberOfNum.toString()
        Picasso.get()
            .load(itemsViewModel.teacherImage)
            .into(holder.imageViewUserProfile)
        holder.textViewUserName.setText(itemsViewModel.teacherName)
        holder.textViewClubName.setText("(${itemsViewModel.clubName})")
        if (userId == ""){
            holder.textViewNumberOfLick.visibility =View.GONE
            holder.likeImage.visibility =View.GONE
        }

//
//        if (usersLikePostList.contains(userId) && itemsViewModel.isLike == true ) {
//            holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
//
//        } else {
//            holder.likeImage.setImageResource(R.drawable.ic_heart)
//        }


            if (itemsViewModel.likeBy!!.contains(userId)){
                holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
            }else{
                holder.likeImage.setImageResource(R.drawable.ic_heart)
            }



        holder.likeImage.setOnClickListener{

             //usersLikePostList.add(userId)
           // var newArray =

            if(!itemsViewModel.likeBy!!.contains(userId)) {
                itemsViewModel.likeBy!!.add(userId);
                holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
            }
//                if (!itemsViewModel.likeBy!!.contains(userId)) {
//                    itemsViewModel.likeBy!!.add(userId);
//                    holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
//                    holder.textViewNumberOfLick.text = itemsViewModel.numberOfNum.toString()
//
//                }



            var post =Post()
            post.postDec =itemsViewModel.postDec
            post.postId =itemsViewModel.postId
            post.postImage =itemsViewModel.postImage
            post.clubName =itemsViewModel.clubName
            post.teacherName =itemsViewModel.teacherName
            post.likeBy =   itemsViewModel.likeBy
            post.isLike =true
            post.numberOfNum = itemsViewModel.likeBy!!.size
            updatePost(itemsViewModel.postId.toString() ,post )
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_postImage)
        val imageViewUserProfile: ImageView = itemView.findViewById(R.id.imagePostUserProfile)
        val textViewDec: TextView = itemView.findViewById(R.id.txt_PostDescription)
        val textViewUserName: TextView = itemView.findViewById(R.id.txt_post_user_name)
        val textViewNumberOfLick: TextView = itemView.findViewById(R.id.txt_numberOfLike)
        val likeImage: ImageView =itemView.findViewById<ImageView>(R.id.img_like)
        var progressBar :ProgressBar = itemView.findViewById(R.id.progressBar)
        val textViewClubName: TextView = itemView.findViewById(R.id.txt_post_club_name)

    }

    fun updatePost(postId:String , newPost:Post){



        db.collection("Post").document(postId).set(newPost).addOnSuccessListener {
          //  Toast.makeText(context ,"post update",Toast.LENGTH_LONG).show()


        }

    }
}
