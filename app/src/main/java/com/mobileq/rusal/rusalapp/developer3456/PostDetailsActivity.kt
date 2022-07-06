package com.mobileq.rusal.rusalapp.developer3456

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.adapter.CommentAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityPostDetailsBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso

class PostDetailsActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityPostDetailsBinding
    lateinit var db: FirebaseFirestore
    lateinit var commentAdpter: CommentAdpter

    lateinit var postCollection: CollectionReference
    lateinit var studentCollection: CollectionReference
    lateinit var teacherCollection: CollectionReference
    lateinit var commentCollection: CollectionReference
    var loggedUserId: String = ""
    var postId: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        getSupportActionBar()?.setTitle("تفاصيل المنشور");


         postId = intent.getStringExtra("Post").toString()




//        if (post!!.teacherImage !== null && post!!.teacherImage !== "") {
//            Picasso.get().load(post!!.teacherImage)
//          //      .into(binding.imagePostUserProfile)
//
//        }

//        binding.txtNumberOfLike.setText(post!!.numberOfNum.toString())
//        // binding.txtPostUserName.setText(post!!.teacherName.toString())
//        //binding.txtPostClubName.setText(post!!.clubName.toString())
//        binding.txtPostDescription.setText(post!!.postDec.toString())

//        db.collection("Post").document(post.postId.toString()).collection("Comment").get()
//            .addOnSuccessListener { docs ->
//
//                Log.e("number of comment", "size : ${docs.size()}");
//                binding.txtNumberOfComment.text = docs.size().toString()
//
//
//            }

//
//        if (preferenceManager!!.getBoolean(Constants.KEY_IS_Teacher)) {
//            db.collection(Constants.KEY_COLLECTION_TEACHER).document(userId).get()
//                .addOnSuccessListener { doc ->
//                    user = doc.toObject(User::class.java)!!
//                }
//
//        } else if (preferenceManager!!.getBoolean(Constants.KEY_IS_Student)) {
//            db.collection(Constants.KEY_COLLECTION_STUDENT).document(userId).get()
//                .addOnSuccessListener { doc ->
//                    user = doc.toObject(User::class.java)!!
//                }
//
//        }
//        // getAllComment(post , userId)
//

        binding.imagSendComment.setOnClickListener(this)
//            .setOnClickListener {
//            if (binding.inputComment.text.isEmpty()) {
//                binding.inputComment.setError("لا يوجد تعليق")
//                // binding.imagSendComment.is
//
//            } else {
//                //      binding.imagSendComment.visibility = View.VISIBLE
//                var comment = Comment()
//                comment.commentText = binding.inputComment.text.toString()
//                comment.userSendId = user.id
//                comment.userSendImage = user.image
//                comment.userSendName = user.name
//
//                addComment(post, comment, userId)
//                getAllComment(post, userId)
//            }
//        }


    }

    private fun initData() {

        db = FirebaseFirestore.getInstance()
        postCollection = db.collection(Constants.KEY_COLLECTION_POST)
        studentCollection = db.collection(Constants.KEY_COLLECTION_STUDENT)
        teacherCollection = db.collection(Constants.KEY_COLLECTION_TEACHER)

        loggedUserId = PreferenceManager(this).getString(Constants.KEY_USER_ID)
        postId.let {
            db.collection(Constants.KEY_COLLECTION_POST)
                .document(it)
                .get().addOnSuccessListener {
                    var post = it.toObject(NewPost::class.java)!!
                    initPostData(post)

                }
            commentCollection = postCollection.document(postId).collection(Constants.KEY_COLLECTION_COMMENT)

        }

    }
        private fun initPostData(post: NewPost) {
            if (post.imageUrl == "") {
                binding.imgPostImage.visibility = View.GONE
            } else {
                Picasso.get().load(post.imageUrl)
                    .into(binding.imgPostImage)
            }
        }


//
//        studentCollection.document(loggedUserId)
//            .get().addOnSuccessListener {
//                var user = it.toObject(User::class.java)
//                user?.let {
//                }
//            }
//        teacherCollection.document(loggedUserId)
//            .get().addOnSuccessListener {
//                var user = it.toObject(User::class.java)
//                user?.let {
//
//
//                }
//            }


    private fun sendComment() {

        val doc = commentCollection.document()

        val commentText = binding.inputComment.text.toString()
        if (commentText.isEmpty()) {
            Toast.makeText(this, "No Comment to send", Toast.LENGTH_SHORT).show()
            return
        }

    //    val comment = Comment
//        commentCollection.document(doc.id).set()



    }


    override fun onClick(p0: View?) {
        when(p0){
            binding.imagSendComment -> sendComment()
        }
    }
}


//    fun  addComment(post: Post , comment: Comment , userid: String){
//        binding.imagSendComment.isEnabled=false
//
//        val ref: DocumentReference = db.collection(Constants.KEY_COLLECTION_COMMENT).document()
//        comment.commentId = ref.id
////        ref.set(post).addOnSuccessListener {
////            Toast.makeText(this , "Comment send" , Toast.LENGTH_LONG).show()
////
////        }
//
//        db.collection("Post").document(post.postId.toString()).collection("Comment").document()
//            .set(comment)
//            .addOnSuccessListener {
//                Toast.makeText(this , "Comment Send" , Toast.LENGTH_LONG).show()
//                binding.inputComment.text.clear()
//                binding.imagSendComment.isEnabled=true
//
//            }
//
//
//
//
//
//    }

//    fun getAllComment(post: Post, userid: String) {
//        var data = ArrayList<Comment>()
//        db.collection("Post").document(post.postId.toString()).collection("Comment")
//            .get().addOnSuccessListener { docs ->
//                for (d in docs) {
//                    data.add(d.toObject(Comment::class.java))
//
//                }
//
//                binding.commentRc.visibility = View.VISIBLE
//                commentAdpter = CommentAdpter(data)
//                binding.commentRc.setAdapter(commentAdpter)
//                binding.commentRc.setLayoutManager(LinearLayoutManager(this))
//            }
//
//
//    }
