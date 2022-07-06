package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.*
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityClubBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.PostModelForAdapter
import com.mobileq.rusal.rusalapp.developer3456.model.Comment
import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.home_feature.comment.CommentsDialog
import com.mobileq.rusal.rusalapp.developer3456.image_preview_feature.ImagePreviewDialog
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.new_home_feature.NewPostListener
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import developer.citypalestine8936ps.new_home_feature.NewPostAdapter

class ClubActivity : AppCompatActivity() , NewPostListener {
    lateinit var binding: ActivityClubBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var database: FirebaseFirestore
    private lateinit var postCollection: CollectionReference
    private lateinit var commentCollection: CollectionReference
    private lateinit var usersCollection: CollectionReference
//    private lateinit var studentCollection: CollectionReference
//    private lateinit var teacherCollection: CollectionReference


    private val newPostAdapter by lazy {
        NewPostAdapter(this, mutableListOf(), loggedUserId, this , false)
    }
    private var loggedUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }


    private fun  initData(){

        database = FirebaseFirestore.getInstance()

        //studentCollection = database.collection(Constants.KEY_COLLECTION_STUDENT)
        usersCollection = database.collection(Constants.KEY_COLLECTION_USERS)
        postCollection = database.collection(Constants.KEY_COLLECTION_POST)

        loggedUserId= PreferenceManager(this).getString(Constants.KEY_USER_ID)
        val  clubName = intent.getStringExtra("club")
        binding.txtClubName.setText(clubName)



        postCollection
            .whereEqualTo("club",clubName)
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
        binding.clubPostRc.adapter = newPostAdapter
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
                        newPostAdapter.addPost(PostModelForAdapter(
                            author = author,
                            post = newPost,
                            comment = comments
                        ))
                        Log.d("TAG", "initData: ${ newPost} siaaaa")

                    }

            }
        }
//        teacherCollection.whereEqualTo("id" ,newPost.authorDocId).limit(1).get().addOnCompleteListener {
//            if (!it.isSuccessful) {
//                return@addOnCompleteListener
//            }
//            val data = it.result.toObjects(User::class.java)
//            data.firstOrNull()?.let { author ->
//                postCollection.document(newPost.docId)
//                    .collection(Constants.KEY_COLLECTION_COMMENT)
//                    .get().addOnCompleteListener{
//                        if (!it.isSuccessful){
//
//                            return@addOnCompleteListener
//                        }
//                        var comments =  it.result.toObjects(Comment::class.java)
//                        newPostAdapter.addPost(PostModelForAdapter(author = author, post = newPost , comment = comments))
//
//                    }
//
//            }
//        }
    }


    private fun onPostRemoved(newPost: NewPost) {
        newPostAdapter.removePost(newPost)

    }

    private fun onPostModified(newPost: NewPost) {

        newPostAdapter.updatePost(newPost)
    }




    override fun onClickLike(post: NewPost) {

        val isLike = post.likes.contains(loggedUserId)

        if (isLike) {
            post.likes.remove(loggedUserId)
        } else {
            post.likes.add(loggedUserId)
        }

        postCollection.document(post.docId).set(post)
    }

    override fun onClickComment(post: NewPost) {
        CommentsDialog.display(
            fragmentManager = supportFragmentManager,
            postId = post.docId
        )

    }

    override fun onClickImage(post: NewPost) {
        ImagePreviewDialog.display(
            fragmentManager = supportFragmentManager,
            imageUrl = post.imageUrl
        )    }

    override fun onClickAuthor(author: User) {

    }


//    override fun onClickAuthor(author: User) {
//        var intent = Intent(this  , ProfileActivity::class.java)
//        intent.putExtra("userId" ,author.id)
//        startActivity(intent)
//    }


}