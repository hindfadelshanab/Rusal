package com.mobileq.rusal.rusalapp.developer3456.home_feature

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobileq.rusal.rusalapp.developer3456.BuildConfig
import com.mobileq.rusal.rusalapp.developer3456.ProfileActivity
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentNewHomeBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.comment.CommentsDialog
import com.mobileq.rusal.rusalapp.developer3456.image_preview_feature.ImagePreviewDialog
import com.mobileq.rusal.rusalapp.developer3456.model.Comment
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.new_home_feature.NewPostListener
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import developer.citypalestine8936ps.new_home_feature.NewPostAdapter
import java.io.File


class NewHomeFragment : Fragment(), View.OnClickListener, NewPostListener {

    private lateinit var binding: FragmentNewHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var postStorageRef: StorageReference

    private lateinit var userCollection: CollectionReference
    private lateinit var postCollection: CollectionReference
    private lateinit var commentCollection: CollectionReference

    private val newPostAdapter by lazy {
        NewPostAdapter(requireContext(), mutableListOf(), loggedUserId, this,false)
    }
    private var loggedUserId: String = ""
    private var clubName: String = ""
    private var postImageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewHomeBinding.inflate(inflater)

        return binding.apply {
            ibPickCamera.setOnClickListener(this@NewHomeFragment)
            ibPost.setOnClickListener(this@NewHomeFragment)
            ibPickGallery.setOnClickListener(this@NewHomeFragment)
            ibRemoveImage.setOnClickListener(this@NewHomeFragment)
            ivPostAuthorImage.setOnClickListener(this@NewHomeFragment)


        }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // newPostAdapter.clearPost()
        initData()
    }

    private fun initData() {
        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        userCollection = database.collection(Constants.KEY_COLLECTION_USERS)
        postCollection = database.collection(Constants.KEY_COLLECTION_POST)
        commentCollection = database.collection(Constants.KEY_COLLECTION_COMMENT)


        postStorageRef = storage.reference.child(Constants.KEY_POSTS_STORAGE_REF)

        loggedUserId = PreferenceManager(requireContext()).getString(Constants.KEY_USER_ID)

        userCollection.document(loggedUserId)
            .get().addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(requireContext(), "Error Logged User ID", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressed()
                }
                val loggedUser = it.result.toObject(User::class.java)
                initLoggedUserData(loggedUser)
            }

        postCollection.orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                Log.d("TAG", "ttt initData: Posts == ${snapshots?.documents?.size}")
                snapshots?.documentChanges?.let {

                    for (doc in it) {
                        val newPost = doc.document.toObject(NewPost::class.java)
                        Log.d("TAG", "initData: ${newPost}")
                        Log.d("TAG", "initData: ${postCollection}")
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
        binding.rvPosts.adapter = newPostAdapter
    }


    private fun onPostAdded(newPost: NewPost) {

        try {
            userCollection.document(newPost.authorDocId).get().addOnCompleteListener {
                Log.d("TAG", "onPostAdded: newPost.authorDocId ${newPost.authorDocId} ==> ${it.isSuccessful}")
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                val data = it.result.toObject(User::class.java)
                Log.d("TAG", "onPostAdded: newPost.authorDocId ${newPost.authorDocId} ==> $data")
                data?.let { author ->
                    Log.d("TAG", "onPostAdded: ${newPost} userrrr555")
                    postCollection.document(newPost.docId)
                        .collection(Constants.KEY_COLLECTION_COMMENT)
                        .get()
                        .addOnFailureListener{
                            Log.d("TAG", "onPostAdded: ${it.message} tttt")
                        }
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                return@addOnCompleteListener
                            }
                            val comments = it.result.toObjects(Comment::class.java)
                            newPostAdapter.addPost(
                                PostModelForAdapter(
                                    author = author,
                                    post = newPost,
                                    comment = comments
                                )
                            )
                            Log.d("TAG", "initData: ${newPost} onnnnnnn")

                        }

                }
            }
        }catch (e:Exception){}

    }


    private fun onPostRemoved(newPost: NewPost) {
        newPostAdapter.removePost(newPost)

    }

    private fun onPostModified(newPost: NewPost) {

        newPostAdapter.updatePost(newPost)
        Log.d("TAG", "initData: ${newPost} update")
    }

    private fun initLoggedUserData(loggedUser: User?) {
        loggedUserId.let {
            binding.tvPostAuthorName.text = loggedUser!!.name
            clubName = loggedUser.club.toString()
            if (loggedUser.image!!.isNotEmpty())
                Picasso.get().load(loggedUser.image).into(binding.ivPostAuthorImage)

        }

    }

    override fun onClick(p0: View?) {

        when (p0) {
            binding.ibPost -> onSendPostClick()
            binding.ibPickCamera -> onClickPickCamera()
            binding.ibPickGallery -> onClickPickGallery()
            binding.ibRemoveImage -> onClickRemoveImage()
            binding.ivPostAuthorImage -> onClickRemoveImage()
        }
    }


    private fun onSendPostClick() {
        val postText = binding.etPostContent.text.toString()

        if (postText.isEmpty() && postImageUri == null) {
            Toast.makeText(requireContext(), "Can't post empty", Toast.LENGTH_SHORT).show()
            return
        }

        val doc = postCollection.document()

        if (postImageUri != null) {
            val imageName = "${doc.id}.png"
            postStorageRef.child(imageName).putFile(postImageUri!!).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Something wont wrong, please try again later !!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnCompleteListener
                }

                postStorageRef.child(imageName).downloadUrl.addOnSuccessListener {
                    sendMessage(doc, postText, it.toString())
                }
            }


        } else {
            sendMessage(doc, postText, "")
        }
    }

    private fun sendMessage(doc: DocumentReference, postText: String, imageUrl: String) {


        val post = NewPost(
            docId = doc.id,
            content = postText,
            imageUrl = imageUrl,
            authorDocId = loggedUserId ,
           club =clubName
        )

        doc.set(post).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Post Successfully", Toast.LENGTH_SHORT).show()
                clearPostView()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Something wont wrong, please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun clearPostView() {
        binding.etPostContent.text.clear()
        postImageUri = null
        binding.cvImagePreview.visibility = View.GONE

    }


    private fun onClickPickCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let { uri ->
                    postImageUri = uri
                    takeImageResult.launch(uri)
                }
            }
        }
    }

    private fun onClickPickGallery() {
        selectImageFromGalleryResult.launch("image/*")

    }

    private fun onClickRemoveImage() {
        changeImagePreviewVisibility(false)
        postImageUri = null
    }


    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Handle Permission granted/rejected
            if (isGranted) {
                // Permission is granted
                onClickPickCamera()
            } else {
                // Permission is denied
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                postImageUri?.let { uri ->
                    changeImagePreviewVisibility(true)
                    binding.ivPostImage.setImageURI(uri)
                }
            }
        }


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                changeImagePreviewVisibility(true)
                postImageUri = it
                binding.ivPostImage.setImageURI(it)
            }
        }

    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }


    private fun changeImagePreviewVisibility(status: Boolean) {
        binding.cvImagePreview.visibility = if (status) View.VISIBLE else View.GONE
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
            fragmentManager = childFragmentManager,
            postId = post.docId
        )

    }

    override fun onClickImage(post: NewPost) {
        ImagePreviewDialog.display(
            fragmentManager = childFragmentManager,
            imageUrl = post.imageUrl
        )    }

    override fun onClickAuthor(author: User) {
        var intent = Intent(context  , ProfileActivity::class.java)
        intent.putExtra("userId" ,author.id)
      startActivity(intent)
    }
    private  fun goToMyProfile(){
        var intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra("userId", loggedUserId)
        intent.putExtra("myProfile", true)
        startActivity(intent)
    }

}