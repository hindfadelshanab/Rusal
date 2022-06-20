package com.mobileq.rusal.rusalapp.developer3456.adapter


import Post
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.number.NumberFormatter.with
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class PostAdpter(private val mList: List<Post>, var context: FragmentActivity? , var userId:String
, var  postListener: PostListener , var isPublic: Boolean) : RecyclerView.Adapter<PostAdpter.ViewHolder>() {

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

        if (isPublic){
            holder.linearComment.visibility=View.GONE
            holder.linearLike.visibility=View.GONE
        }else{
            holder.linearComment.visibility=View.VISIBLE
            holder.linearLike.visibility=View.VISIBLE
        }
        if (itemsViewModel.postImage=="" ){
            holder.imageView.visibility=View.GONE
            holder.progressBar.visibility=View.GONE
        }
        else if (itemsViewModel.postImage !=null &&itemsViewModel.postImage!="") {

            Picasso
                .get()
                .load(itemsViewModel.postImage)
                .error(R.drawable.ic_launcher_background)
                .fit()
            //    .resize(300 , 180)

                .into(holder.imageView , object :Callback{
                    override fun onSuccess() {
                        holder.imageView.visibility =View.VISIBLE
                        holder.progressBar.visibility =View.GONE

                    }

                    override fun onError(e: Exception?) {

                    }
                })

       }
        holder.itemView.setOnClickListener { view -> postListener.onPostClicked(itemsViewModel)

        }
        holder.linearComment.setOnClickListener { view -> postListener.onPostClicked(itemsViewModel)

        }

        holder.textViewDec.text = itemsViewModel.postDec
        holder.textViewNumberOfLick.text = "${itemsViewModel.numberOfNum.toString()} اعجاب"
        Picasso
            .get()
            .load(itemsViewModel.teacherImage)
    .placeholder(R.drawable.ic__70076_account_avatar_client_male_person_icon)

            .error(R.drawable.ic__70076_account_avatar_client_male_person_icon)
            .into(holder.imageViewUserProfile)

        holder.textViewUserName.setText(itemsViewModel.teacherName)
        holder.textViewClubName.setText("(${itemsViewModel.clubName})")



        if (userId == ""){
            holder.textViewNumberOfLick.visibility =View.GONE
            holder.likeImage.visibility =View.GONE
        }
            if (itemsViewModel.likeBy!!.contains(userId)){
                holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
            }else{
                holder.likeImage.setImageResource(R.drawable.ic_heart)
            }

        db.collection("Post").document(itemsViewModel.postId.toString()).collection("Comment").get().addOnSuccessListener { docs ->

            Log.e("number of comment" , "size : ${docs.size()}");
            holder.textViewNumberOfComment.text = "${docs.size()} تعليق"
//            if (postListener !=null){
//
//            }
        }
        holder.imageShare.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,itemsViewModel.postDec)
            intent.type="text/plain"
            context?.startActivity(Intent.createChooser(intent,"Share To:"))
        }





        holder.likeImage.setOnClickListener{

             //usersLikePostList.add(userId)
           // var newArray =

            if(!itemsViewModel.likeBy!!.contains(userId)) {
                itemsViewModel.likeBy!!.add(userId);
                holder.likeImage.setImageResource(R.drawable.ic__fill_heart)
            }


            var post =Post()
            post.postDec =itemsViewModel.postDec
            post.postId =itemsViewModel.postId
            post.postImage =itemsViewModel.postImage
            post.clubName =itemsViewModel.clubName
            post.teacherName =itemsViewModel.teacherName
            post.likeBy =   itemsViewModel.likeBy
            post.isLike =true
            post.numberOfComment = itemsViewModel.numberOfComment
            post.numberOfNum = itemsViewModel.likeBy!!.size


            db.collection("Post").document(itemsViewModel.postId.toString()).set(post).addOnSuccessListener {
                //  Toast.makeText(context ,"post update",Toast.LENGTH_LONG).show()


                holder.textViewNumberOfLick.text="${post.likeBy!!.size.toString()}اعجاب "



            }
       //     updatePost(itemsViewModel.postId.toString() ,post )

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
        val textViewNumberOfComment: TextView = itemView.findViewById(R.id.txt_numberOfComment)
        val imageShare: LinearLayout = itemView.findViewById(R.id.layoyt_share)
        val  linearComment :LinearLayout = itemView.findViewById(R.id.linearComment);
        val  linearLike :LinearLayout = itemView.findViewById(R.id.linearLike);

    }

    fun updatePost(postId:String , newPost:Post){



    }
}
