package com.mobileq.rusal.rusalapp.developer3456.teacherFragment

import Post
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mobileq.rusal.rusalapp.developer3456.PostDetailsActivity
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentHomeTeacherBinding
import com.mobileq.rusal.rusalapp.developer3456.listeners.PostListener
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList


class HomeTeacherFragment : Fragment() , PostListener {

    lateinit var binding: FragmentHomeTeacherBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore
    private var encodedImage: Uri? = null
    lateinit var postAdpter: PostAdpter
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(activity)
        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        getAllPost(userId)

        //preferenceManager!!.getString(Constants.KEY_USER_ID)
        binding.imageFromGallery.setOnClickListener { view ->
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
        binding.imageFromCamera.setOnClickListener { view ->
            dispatchTakePictureIntent()

        }

        binding.imageSendPost.setOnClickListener(View.OnClickListener {
            if (binding.txtWritePost.text.isEmpty()) {
                binding.txtWritePost.setError("Enter your Post")
            } else {
                addPost(encodedImage, userId)
                getAllPost(userId)
            }
        })

        Picasso.get()
            .load(preferenceManager!!.getString(Constants.KEY_IMAGE))
            .into(   binding.imageUserSendPost)
        getTecaherInfo(userId)

        binding.txtNameUserSendPost.setText(preferenceManager!!.getString(Constants.KEY_NAME))

        Log.e("imaggggepost" , preferenceManager!!.getString(Constants.KEY_IMAGE))

        return binding.root
    }

    fun  getTecaherInfo(userId: String){
        db.collection(Constants.KEY_COLLECTION_TEACHER).document(userId).get().addOnSuccessListener { doc ->

            var teacher = doc.toObject(User::class.java)
            Log.e("imaggggepost2" , teacher!!.image.toString())
            if (teacher.image != null) {
                Picasso.get()
                    .load(teacher.image)
                    .resize(30,30)
                    .centerCrop()
                    .into(binding.imageUserSendPost)
            }
        }

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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            encodedImage = getImageUri(requireActivity(), imageBitmap)
            if (encodedImage !=null) {
                binding.imageForPost.visibility =View.VISIBLE
                binding.imageForPost.setImageBitmap(imageBitmap)
            }

        }
    }

    fun getAllPost(userId: String) {

        var data = ArrayList<Post>()
        binding.progressBarPost.visibility = View.VISIBLE
        data.clear()
        db.collection("Post")

            .get().addOnSuccessListener { queryDocumentSnapshots ->


                for (documentSnapshot in queryDocumentSnapshots) {
                    val post: Post = documentSnapshot.toObject(Post::class.java)
                    data.add(post)
                }
                postAdpter = PostAdpter(data, activity, userId , this , false)
                binding.postTecRc.setAdapter(postAdpter)
                postAdpter.notifyDataSetChanged()
                binding.progressBarPost.visibility = View.GONE
                binding.postTecRc.visibility = View.VISIBLE

                Log.e("hin", data.size.toString() + "")
                binding.postTecRc.setLayoutManager(LinearLayoutManager(activity))
            }
            .addOnFailureListener {
                binding.progressBarPost.visibility = View.VISIBLE
                binding.postTecRc.visibility = View.GONE
            }
    }


    fun addPost(fileUri: Uri?, userId: String) {

        binding.imageSendPost.isEnabled=false

        if (fileUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
            refStorage.putFile(fileUri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        var imageUrl = it.toString()


                        db.collection(Constants.KEY_COLLECTION_TEACHER)
                            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
                            .get().addOnSuccessListener { doc ->

                                var user = doc.toObject(User::class.java)!!

                                var post: Post = Post()
                                post.postDec = binding.txtWritePost.text.toString()
                                post.postImage = imageUrl
                                post.teacherName = user.name
                                post.clubName = user.club
                                post.numberOfComment = 0
                                post.numberOfNum = 0
                                post.isLike = false
                            //    post.teacherName = user.name
                                post.teacherImage = user.image
                                post.likeBy = ArrayList()
                                val ref: DocumentReference = db.collection("Post").document()
                                post.postId = ref.id
                                ref.set(post).addOnSuccessListener {
                                    binding.txtWritePost.text.clear()
                                    encodedImage = null
                                    binding.imageForPost.setImageBitmap(null)
                                    binding.imageForPost.visibility =View.GONE
                                    binding.imageSendPost.isEnabled=true

                                    getAllPost(userId)
                                }

                            }


                    }
                })

                .addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })


        } else {
            db.collection(Constants.KEY_COLLECTION_TEACHER)
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
                    post.teacherImage =user.image
                    post.likeBy = ArrayList()
                    post.teacherImage = user.image

                    val ref: DocumentReference = db.collection("Post").document()
                    post.postId = ref.id
                    ref.set(post).addOnSuccessListener {
                        binding.txtWritePost.text.clear()
                        encodedImage = null
                        binding.imageForPost.setImageBitmap(null)
                        binding.imageForPost.visibility =View.GONE

                        binding.imageSendPost.isEnabled=true

                        getAllPost(userId)
                    }

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
                    if (encodedImage !=null) {
                        binding.imageForPost.visibility =View.VISIBLE
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
        val intent = Intent(activity  , PostDetailsActivity::class.java)
        intent.putExtra("Post", post)
        //   preferenceManager!!.putString(Constants.KEY_USER , post!!.id)

        startActivity(intent)
    }
}