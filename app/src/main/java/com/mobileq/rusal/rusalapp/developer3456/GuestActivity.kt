package com.mobileq.rusal.rusalapp.developer3456

import Post
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityGuestBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.home_feature.PostModelForAdapter
import com.mobileq.rusal.rusalapp.developer3456.home_feature.comment.CommentsDialog
import com.mobileq.rusal.rusalapp.developer3456.image_preview_feature.ImagePreviewDialog
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener
import com.mobileq.rusal.rusalapp.developer3456.model.Comment
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import developer.citypalestine8936ps.new_home_feature.NewPostAdapter

class GuestActivity : AppCompatActivity()  {

    lateinit var binding:ActivityGuestBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var database: FirebaseFirestore
    private lateinit var postCollection: CollectionReference
    private lateinit var usersCollection: CollectionReference


    private val newPostAdapter by lazy {
        NewPostAdapter(this, mutableListOf(), "",null  , true)
    }
  //  private var loggedUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setTitle(R.string.app_name)
        initData()

     //   getAllPost()
    }

    private fun  initData(){

        database = FirebaseFirestore.getInstance()

        //studentCollection = database.collection(Constants.KEY_COLLECTION_STUDENT)
        usersCollection = database.collection(Constants.KEY_COLLECTION_USERS)
        postCollection = database.collection(Constants.KEY_COLLECTION_PUBLIC_POST)

      //  loggedUserId= PreferenceManager(this).getString(Constants.KEY_USER_ID)



        postCollection
            .orderBy("time" , Query.Direction.DESCENDING)

            .addSnapshotListener { snapshots, _ ->
                snapshots?.documentChanges?.let {
//                    it.sortBy {
//                        it.document.toObject(NewPost::class.java).time
//
//                    }

                    Log.d("TAG", "initData: ${ it.size} sizzz")
                    for (doc in it) {
                        val newPost = doc.document.toObject(NewPost::class.java)
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> onPostAdded(newPost)
                            DocumentChange.Type.MODIFIED -> onPostModified(newPost)
                            DocumentChange.Type.REMOVED -> onPostRemoved(newPost)
                        }
                    }
                }

            }
        initPostsAdapter()

    }

    private fun initPostsAdapter() {
        binding.postRecS.adapter = newPostAdapter
    }


    private fun onPostAdded(newPost: NewPost) {

        usersCollection.document(newPost.authorDocId).get().addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val data = it.result.toObject(User::class.java)
            data?.let { author ->
                postCollection.document(newPost.docId)
                    .collection(Constants.KEY_COLLECTION_COMMENT)
                    .get().addOnCompleteListener{
                        if (!it.isSuccessful){

                            return@addOnCompleteListener
                        }
                        val comments =  it.result.toObjects(Comment::class.java)
                        newPostAdapter.addPost(
                            PostModelForAdapter(
                            author = author,
                            post = newPost,
                            comment = comments
                        )
                        )
                        Log.d("TAG", "initData: ${ newPost} siaaaab guest ")

                    }

            }
        }

    }


    private fun onPostRemoved(newPost: NewPost) {
        newPostAdapter.removePost(newPost)

    }

    private fun onPostModified(newPost: NewPost) {

        newPostAdapter.updatePost(newPost)
    }



//
//    override fun onClickLike(post: NewPost) {
//
//        val isLike = post.likes.contains(loggedUserId)
//
//        if (isLike) {
//            post.likes.remove(loggedUserId)
//        } else {
//            post.likes.add(loggedUserId)
//        }
//
//        postCollection.document(post.docId).set(post)
//    }
//
//    override fun onClickComment(post: NewPost) {
//        CommentsDialog.display(
//            fragmentManager = supportFragmentManager,
//            postId = post.docId
//        )
//
//    }
//
//    override fun onClickImage(post: NewPost) {
//        ImagePreviewDialog.display(
//            fragmentManager = supportFragmentManager,
//            imageUrl = post.imageUrl
//        )    }
//
//    override fun onClickAuthor(author: User) {
//
//    }

//    fun getAllPost() {
//
//        var data = ArrayList<Post>()
//        binding.progressBarPost.visibility = View.VISIBLE
//        data.clear()
//        db.collection("PublicPost")
//
//            .get().addOnSuccessListener { queryDocumentSnapshots ->
//
//
//                for (documentSnapshot in queryDocumentSnapshots) {
//                    val post: Post = documentSnapshot.toObject(Post::class.java)
//                    data.add(post)
//                }
//                postAdpter = PostAdpter(data, this, "" , this , true)
//                binding.postRecS.setAdapter(postAdpter)
//                postAdpter.notifyDataSetChanged()
//                binding.progressBarPost.visibility = View.GONE
//                binding.postRecS.visibility = View.VISIBLE
//
//                Log.e("hin", data.size.toString() + "")
//                binding.postRecS.setLayoutManager(LinearLayoutManager(this))
//            }
//            .addOnFailureListener {
//                binding.progressBarPost.visibility = View.VISIBLE
//                binding.postRecS.visibility = View.GONE
//            }
//    }
//
//    override fun onPostClicked(post: Post) {
//
//    }

}