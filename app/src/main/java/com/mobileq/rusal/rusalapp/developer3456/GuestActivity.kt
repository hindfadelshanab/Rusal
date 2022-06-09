package com.mobileq.rusal.rusalapp.developer3456

import Post
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityGuestBinding
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener

class GuestActivity : AppCompatActivity() , PostListener {

    lateinit var binding:ActivityGuestBinding
   lateinit var  postAdpter:PostAdpter

   lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuestBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        setContentView(binding.root)

        getAllPost()
    }


    fun getAllPost() {

        var data = ArrayList<Post>()
        binding.progressBarPost.visibility = View.VISIBLE
        data.clear()
        db.collection("PublicPost")

            .get().addOnSuccessListener { queryDocumentSnapshots ->


                for (documentSnapshot in queryDocumentSnapshots) {
                    val post: Post = documentSnapshot.toObject(Post::class.java)
                    data.add(post)
                }
                postAdpter = PostAdpter(data, this, "" , this)
                binding.postRecS.setAdapter(postAdpter)
                postAdpter.notifyDataSetChanged()
                binding.progressBarPost.visibility = View.GONE
                binding.postRecS.visibility = View.VISIBLE

                Log.e("hin", data.size.toString() + "")
                binding.postRecS.setLayoutManager(LinearLayoutManager(this))
            }
            .addOnFailureListener {
                binding.progressBarPost.visibility = View.VISIBLE
                binding.postRecS.visibility = View.GONE
            }
    }

    override fun onPostClicked(post: Post) {

    }

}