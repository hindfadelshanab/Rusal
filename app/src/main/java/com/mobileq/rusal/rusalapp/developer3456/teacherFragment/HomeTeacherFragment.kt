package com.mobileq.rusal.rusalapp.developer3456.teacherFragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobileq.rusal.rusalapp.developer3456.BuildConfig
import com.mobileq.rusal.rusalapp.developer3456.ProfileActivity
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentHomeTeacherBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.PostModelForAdapter
import com.mobileq.rusal.rusalapp.developer3456.home_feature.comment.CommentsDialog
import com.mobileq.rusal.rusalapp.developer3456.image_preview_feature.ImagePreviewDialog
import com.mobileq.rusal.rusalapp.developer3456.model.Comment
import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.new_home_feature.NewPostListener
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import developer.citypalestine8936ps.new_home_feature.NewPostAdapter
import java.io.File


class HomeTeacherFragment : Fragment(), NewPostListener , View.OnClickListener {



    private lateinit var binding: FragmentHomeTeacherBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var postStorageRef: StorageReference

    private lateinit var userCollection: CollectionReference
    private lateinit var postCollection: CollectionReference
    private lateinit var commentCollection: CollectionReference

    private val newPostAdapter by lazy {
        NewPostAdapter(requireContext(), mutableListOf(), loggedUserId, this , false)
    }
    private var loggedUserId: String = ""
    private var clubName: String = ""
    private var postImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false)

        return binding.apply {
            ibPickCamera.setOnClickListener(this@HomeTeacherFragment)
            ibPost.setOnClickListener(this@HomeTeacherFragment)
            ibPickGallery.setOnClickListener(this@HomeTeacherFragment)
            ibRemoveImage.setOnClickListener(this@HomeTeacherFragment)
            ivPostAuthorImage.setOnClickListener(this@HomeTeacherFragment)


        }.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newPostAdapter.clearPost()
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
                snapshots?.documentChanges?.let {

                    for (doc in it) {
                        val newPost = doc.document.toObject(NewPost::class.java)

                        Log.d("TAG", "initData: ${newPost.content}")
                        Log.d("TAG", "initData:ggggggggg")
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
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                val data = it.result.toObject(User::class.java)
                data?.let { author ->
                    postCollection.document(newPost.docId)
                        .collection(Constants.KEY_COLLECTION_COMMENT)
                        .get().addOnCompleteListener {
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
                            Log.d("TAG", "onPostAdded: ${comments.size}")

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
    }

    private fun initLoggedUserData(loggedUser: User?) {
        loggedUserId.let {
            binding.tvPostAuthorName.text = loggedUser!!.name
            clubName =loggedUser.club.toString()
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
            binding.ivPostAuthorImage ->goToMyProfile()
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


//
//        db = FirebaseFirestore.getInstance()
//        preferenceManager = PreferenceManager(activity)
//        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
//        getAllPost(userId)
//
//        //preferenceManager!!.getString(Constants.KEY_USER_ID)
//        binding.imageFromGallery.setOnClickListener { view ->
////            val intent =
////                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////            pickImage.launch(intent)
//            selectImageFromGallery()
//        }
//        binding.imageFromCamera.setOnClickListener { view ->
//            // dispatchTakePictureIntent()
//            takeImage()
//        }
//
//        binding.imageSendPost.setOnClickListener(View.OnClickListener {
//            if (binding.txtWritePost.text.isEmpty()) {
//                binding.txtWritePost.setError("Enter your Post")
//            } else {
//                addPost( userId)
//                getAllPost(userId)
//            }
//        })
//        getTecaherInfo(userId)
//        binding.imageUserSendPost.setOnClickListener {
//
//            var intent = Intent(activity, ProfileActivity::class.java)
//            intent.putExtra("userId", userId)
//            intent.putExtra("myProfile", true)
//            startActivity(intent)
//
//        }
//
//
//
//
//        binding.txtNameUserSendPost.setText(preferenceManager!!.getString(Constants.KEY_NAME))
//
//        Log.e("imaggggepost", preferenceManager!!.getString(Constants.KEY_IMAGE))
//
//        return binding.root


//    fun getTecaherInfo(userId: String) {
//        db.collection(Constants.KEY_COLLECTION_TEACHER).document(userId).get()
//            .addOnSuccessListener { doc ->
//
//                var teacher = doc.toObject(User::class.java)
////                Picasso.get()
////                    .load(teacher!!.image)
////                    .into(binding.imageUserSendPost)
//                Log.e("imaggggepost2", teacher!!.image.toString())
//                if (teacher.image != null) {
//                    Picasso.get()
//                        .load(teacher.image)
//                        .resize(30, 30)
//                        .centerCrop()
//                        .into(binding.imageUserSendPost)
//                }
//                binding.txtNameUserSendPost.setText(teacher.name)
//            }
//
//    }
//
//    private fun takeImage() {
//        lifecycleScope.launchWhenStarted {
//            getTempLatestTmpUri().let { uri ->
//                latestTmpUri = uri
//                takeImageResult.launch(uri)
//            }
//        }
//    }
//
//    private val takeImageResult =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
//            if (isSuccess) {
//                latestTmpUri?.let { uri ->
//
//                    //    uploadImage(uri)
//                    binding.imageForPost.visibility = View.VISIBLE
//                    binding.imageForPost.setImageURI(uri)
//                }
//            }
//        }
//
//    private val selectImageFromGalleryResult =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                binding.imageForPost.visibility = View.VISIBLE
//                binding.imageForPost.setImageURI(it)
//                latestTmpUri = it
//            }
//            Log.d("TAG", ": selectImageFromGalleryResult uri $uri")
//            Log.d("TAG", ": selectImageFromGalleryResult latestTmpUri $latestTmpUri")
//        }
//
//    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
//
//    private fun getTempLatestTmpUri(): Uri {
//        val tmpFile =
//            File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
//                createNewFile()
//                deleteOnExit()
//            }
//
//        return FileProvider.getUriForFile(
//            requireContext(),
//            "${BuildConfig.APPLICATION_ID}.provider",
//            tmpFile
//        )
//    }
//
//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
////
////            encodedImage = getImageUri(requireActivity(), imageBitmap)
////            if (encodedImage != null) {
////                binding.imageForPost.visibility = View.VISIBLE
////                binding.imageForPost.setImageBitmap(imageBitmap)
////            }
//
//        }
//    }
//
//    fun getAllPost(userId: String) {
//
//        var data = ArrayList<Post>()
//        binding.progressBarPost.visibility = View.VISIBLE
//        data.clear()
//        db.collection("Post")
//
//            .get().addOnSuccessListener { queryDocumentSnapshots ->
//
//
//                for (documentSnapshot in queryDocumentSnapshots) {
//                    val post: Post = documentSnapshot.toObject(Post::class.java)
//                    data.add(post)
//                }
//                postAdpter = PostAdpter(data, activity, userId, this, false)
//                binding.postTecRc.setAdapter(postAdpter)
//                postAdpter.notifyDataSetChanged()
//                binding.progressBarPost.visibility = View.GONE
//                binding.postTecRc.visibility = View.VISIBLE
//
//                Log.e("hin", data.size.toString() + "")
//                binding.postTecRc.setLayoutManager(LinearLayoutManager(activity))
//            }
//            .addOnFailureListener {
//                binding.progressBarPost.visibility = View.VISIBLE
//                binding.postTecRc.visibility = View.GONE
//            }
//    }
//
//
//    fun addPost( userId: String) {
//
//        binding.imageSendPost.isEnabled = false
//
//        if (latestTmpUri != null) {
//            val fileName = UUID.randomUUID().toString() + ".jpg"
//            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
//            refStorage.putFile(latestTmpUri!!)
//                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
//                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
//                        var imageUrl = it.toString()
//
//
//                        db.collection(Constants.KEY_COLLECTION_TEACHER)
//                            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
//                            .get().addOnSuccessListener { doc ->
//
//                                var user = doc.toObject(User::class.java)!!
//
//                                var post: Post = Post()
//                                post.postDec = binding.txtWritePost.text.toString()
//                                post.postImage = imageUrl
//                                post.teacherName = user.name
//                                post.clubName = user.club
//                                post.numberOfComment = 0
//                                post.numberOfNum = 0
//                                post.teacherId = user.id
//
//                                post.isLike = false
//                                post.teacherImage = user.image
//                                post.likeBy = ArrayList()
//                                val ref: DocumentReference = db.collection("Post").document()
//                                post.postId = ref.id
//                                ref.set(post).addOnSuccessListener {
//                                    binding.txtWritePost.text.clear()
//                                    latestTmpUri = null
//                                    binding.imageForPost.setImageBitmap(null)
//                                    binding.imageForPost.visibility = View.GONE
//                                    binding.imageSendPost.isEnabled = true
//
//                                    getAllPost(userId)
//                                }
//
//                            }
//
//
//                    }
//                })
//
//                .addOnFailureListener(OnFailureListener { e ->
//                    print(e.message)
//                })
//
//
//        } else {
//            db.collection(Constants.KEY_COLLECTION_TEACHER)
//                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
//                .get().addOnSuccessListener { doc ->
//
//                    var user = doc.toObject(User::class.java)!!
//
//                    var post: Post = Post()
//                    post.postDec = binding.txtWritePost.text.toString()
//                    post.postImage = ""
//                    post.teacherName = user.name
//                    post.clubName = user.club
//                    post.numberOfNum = 0
//                    post.isLike = false
//                    post.teacherId = user.id
//                    post.teacherImage = user.image
//                    post.likeBy = ArrayList()
//                    post.teacherImage = user.image
//
//                    val ref: DocumentReference = db.collection("Post").document()
//                    post.postId = ref.id
//                    ref.set(post).addOnSuccessListener {
//                        binding.txtWritePost.text.clear()
//                        latestTmpUri = null
//                        binding.imageForPost.setImageBitmap(null)
//                        binding.imageForPost.visibility = View.GONE
//
//                        binding.imageSendPost.isEnabled = true
//
//                        getAllPost(userId)
//                    }
//
//                }
//        }
//    }
//
//
//    private val pickImage = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            if (result.data != null) {
//                val imageUri = result.data!!.data
//                try {
//                    val inputStream = activity?.contentResolver?.openInputStream(imageUri!!)
//                    val bitmap = BitmapFactory.decodeStream(inputStream)
//
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//
//    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(
//            inContext.getContentResolver(),
//            inImage,
//            "Title",
//            null
//        )
//        return Uri.parse(path)
//    }
//
//    override fun onPostClicked(post: Post) {
//        val intent = Intent(activity, PostDetailsActivity::class.java)
//        intent.putExtra("Post", post)
//        //   preferenceManager!!.putString(Constants.KEY_USER , post!!.id)
//
//        startActivity(intent)
//    }
//
//    override fun onClick(p0: View?) {
//        TODO("Not yet implemented")
//    }
}