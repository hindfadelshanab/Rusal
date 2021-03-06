package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import Post
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mobileq.rusal.rusalapp.developer3456.BuildConfig
import com.mobileq.rusal.rusalapp.developer3456.PostDetailsActivity
import com.mobileq.rusal.rusalapp.developer3456.ProfileActivity
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentHomeBinding
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), PostListener {

    lateinit var binding: FragmentHomeBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var postAdpter: PostAdpter
    lateinit var db: FirebaseFirestore
    lateinit var teacher: User
    private var encodedImage: Uri? = null
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var post: Post
    private var latestTmpUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(activity)
        db = FirebaseFirestore.getInstance()

        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        binding.imageFromGallery.setOnClickListener { view ->
            selectImageFromGallery()
        }
        binding.imageFromCamera.setOnClickListener { view ->
            takeImage()
        }


        binding.imageSendPost.setOnClickListener(View.OnClickListener {
            if (binding.txtWritePost.text.isEmpty()) {
                binding.txtWritePost.setError("Enter your Post")
            } else {
                addPost(userId)
                getAllPost(userId)
            }
        })

        getAllPost(userId)
        Picasso.get()
            .load(preferenceManager!!.getString(Constants.KEY_IMAGE))
            .into(binding.imageUserSendPost)

        binding.imageUserSendPost.setOnClickListener {

            var intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("myProfile", true)
            startActivity(intent)

        }
        binding.txtNameUserSendPost.setText(preferenceManager!!.getString(Constants.KEY_NAME))

        return binding.root
    }

    fun getAllPost(userId: String) {
        var student: User = User()
        var data = ArrayList<Post>()
        data.clear()
        binding.progressBarPost.visibility = View.VISIBLE
        db.collection(Constants.KEY_COLLECTION_STUDENT)
            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
            .get().addOnSuccessListener { doc ->
                student = doc.toObject(User::class.java)!!
                db.collection("Post").get()
                    .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                        for (documentSnapshotpost in queryDocumentSnapshots) {
                            post = documentSnapshotpost.toObject(Post::class.java)
                           // if (post.clubName.equals(student.club)) {
                                data.add(post)
                           // }

                        }
                        binding.progressBarPost.visibility = View.GONE
                        binding.postRecS.visibility = View.VISIBLE
                        postAdpter = PostAdpter(data, activity, userId, this, false)
                        binding.postRecS.setAdapter(postAdpter)
                        binding.postRecS.setLayoutManager(LinearLayoutManager(activity))
                        Log.e("hin", data.size.toString() + "")

                    }).addOnFailureListener(OnFailureListener { e ->
                        Log.e("hind", e.message!!)
                        binding.progressBarPost.visibility = View.VISIBLE
                        binding.postRecS.visibility = View.GONE
                    })
            }


    }


    private fun addPost(userId: String) {

        Log.e("file uri", " fff ${latestTmpUri}")
        binding.imageSendPost.isEnabled = false

        if (latestTmpUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
            refStorage.putFile(latestTmpUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        var imageUrl = it.toString()


                        db.collection(Constants.KEY_COLLECTION_STUDENT)
                            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
                            .get().addOnSuccessListener { doc ->

                                var user = doc.toObject(User::class.java)!!

                                var post: Post = Post()
                                post.postDec = binding.txtWritePost.text.toString()
                                post.postImage = imageUrl
                                post.teacherName = user.name
                                post.clubName = user.club
                                post.numberOfNum = 0
                                post.numberOfComment = 0
                                post.teacherId = user.id
                                post.isLike = false
                                post.teacherImage = user.image
                                post.likeBy = ArrayList()
                                val ref: DocumentReference = db.collection("Post").document()
                                post.postId = ref.id
                                ref.set(post).addOnSuccessListener {
                                    binding.txtWritePost.text.clear()
                                    latestTmpUri = null
                                    binding.imageForPost.setImageBitmap(null)
                                    binding.imageForPost.visibility = View.GONE
                                    binding.imageSendPost.isEnabled = true

                                    getAllPost(userId)
                                }

                            }


                    }
                })

                .addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })


        } else {
            db.collection(Constants.KEY_COLLECTION_STUDENT)
                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
                .get().addOnSuccessListener { doc ->

                    var user = doc.toObject(User::class.java)!!

                    var post: Post = Post()
                    post.postDec = binding.txtWritePost.text.toString()
                    post.postImage = ""
                    post.teacherName = user.name
                    post.clubName = user.club
                    post.numberOfNum = 0
                    post.isLike = false
                    post.teacherImage = user.image
                    post.likeBy = ArrayList()
                    post.teacherImage = user.image
                    post.teacherId = user.id

                    val ref: DocumentReference = db.collection("Post").document()
                    post.postId = ref.id
                    ref.set(post).addOnSuccessListener {
                        binding.txtWritePost.text.clear()
                        encodedImage = null
                        binding.imageForPost.setImageBitmap(null)
                        binding.imageForPost.visibility = View.GONE

                        binding.imageSendPost.isEnabled = true

                        getAllPost(userId)
                    }

                }
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTempLatestTmpUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->

                    //    uploadImage(uri)
                    binding.imageForPost.visibility = View.VISIBLE
                    binding.imageForPost.setImageURI(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imageForPost.visibility = View.VISIBLE
                binding.imageForPost.setImageURI(it)
                latestTmpUri = it
            }
            Log.d("TAG", ": selectImageFromGalleryResult uri $uri")
            Log.d("TAG", ": selectImageFromGalleryResult latestTmpUri $latestTmpUri")
        }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTempLatestTmpUri(): Uri {
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

    private fun dispatchTakePictureIntent() {


        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            //encodedImage = getImageUri(requireActivity(), imageBitmap)
            encodedImage = data.data!!
            if (encodedImage != null) {
                binding.imageForPost.visibility = View.VISIBLE
                binding.imageForPost.setImageBitmap(imageBitmap)
            }

        }
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {
                val imageUri = result.data!!.data
                try {
                    val inputStream = activity?.contentResolver?.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    //binding.imageProfile.setImageBitmap(bitmap)
                    //       binding.textAddImage.visibility = View.GONE
                    encodedImage = result.data!!.data!!
                    if (encodedImage != null) {
                        binding.imageForPost.visibility = View.VISIBLE
                        binding.imageForPost.setImageBitmap(bitmap)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onPostClicked(post: Post) {
        val intent = Intent(activity, PostDetailsActivity::class.java)
        intent.putExtra("Post", post)
        intent.putExtra("comment", post.numberOfComment)
        //   preferenceManager!!.putString(Constants.KEY_USER , post!!.id)

        startActivity(intent)
    }


}