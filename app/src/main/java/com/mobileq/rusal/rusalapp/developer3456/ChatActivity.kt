package com.mobileq.rusal.rusalapp.developer3456

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobileq.rusal.rusalapp.developer3456.adapter.MessageAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityChatBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var messageAdpter: MessageAdpter
    lateinit var db: FirebaseFirestore
    var currentStudent: User = User()
    private var encodedImage: Uri? = null
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100

    private val RESULT_LOAD_IMG = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(this)
        db = FirebaseFirestore.getInstance()
        preferenceManager!!.getString(Constants.KEY_USER_ID)
        var isStudent = preferenceManager!!.getBoolean(Constants.KEY_IS_Student)

        //   isStudent = intent.getBooleanExtra("isStudent", false)
        db.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
            .get().addOnSuccessListener { doc ->
                currentStudent = doc.toObject(User::class.java)!!
            }
//        if (isStudent) {
//            db.collection(Constants.KEY_COLLECTION_STUDENT)
//                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
//                .get().addOnSuccessListener { doc ->
//                    currentStudent = doc.toObject(User::class.java)!!
//                }
//        } else {
//            db.collection(Constants.KEY_COLLECTION_TEACHER)
//                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
//                .get().addOnSuccessListener { doc ->
//                    currentStudent = doc.toObject(User::class.java)!!
//                }
//        }
        var studentClub = intent.getStringExtra("userClub")

        binding.textClubName.setText(studentClub)

        getAllMessage(studentClub.toString())


//
//        binding.layoutPhoto.setOnClickListener {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
//            val intent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }

        binding.layoutPhoto.setOnClickListener { showPopupDialog(binding.layoutPhoto) }


        binding.layoutSend.setOnClickListener {
//            var message = Message()
//            var usersList: ArrayList<User> = intent.getParcelableArrayListExtra<User>("users")!!
//            message.message = binding.inputMessage.text.toString()
//            message.senderId = preferenceManager!!.getString(Constants.KEY_USER_ID)
//            message.senderName = currentStudent.name
//            message.senderImage = currentStudent.image
//            message.users = usersList
//            message.
//            message.fromClub = studentClub
//            message.dateTime = getReadableDateTime(Date())

            sendMessage(studentClub.toString())


        }
        setContentView(binding.root)
    }


    private fun sendMessage(userClub: String) {
        val messageText = binding.inputMessage.text.toString()
        if (messageText.isEmpty() && encodedImage == null) {
            Toast.makeText(this, "Enter message", Toast.LENGTH_LONG).show()
        } else {
            if (encodedImage != null) {
                uploadImage {
                    sendMessage(club = userClub, messageText = messageText, imageUrl = it)
                }
            } else {
                sendMessage(club = userClub, messageText = messageText)
            }
        }
    }

    private fun uploadImage(onUploadImageFinished: (String) -> Unit) {
        encodedImage?.let { imageUri ->

            val fileName = UUID.randomUUID().toString() + ".jpg";
            val refStorage: StorageReference =
                FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(imageUri).addOnSuccessListener { task ->
                task.storage.downloadUrl.addOnSuccessListener {
                    onUploadImageFinished(it.toString())
                }
            }
        } ?: onUploadImageFinished("")


    }

    private fun sendMessage(club: String, messageText: String, imageUrl: String = "") {
        val messageRef: DocumentReference = db.collection("Messages").document()
        val messageDto = Message()
        val usersList: ArrayList<User> = intent.getParcelableArrayListExtra<User>("users")!!
        messageDto.apply {
            messageId = messageRef.id
            senderId = preferenceManager!!.getString(Constants.KEY_USER_ID)
            senderName = currentStudent.name
            senderImage = currentStudent.image
            message = messageText
            val d = Date()
            dateTime = getReadableDateTime(d)
            time = d.time
            users = usersList
            fromClub = club

            messageImage = imageUrl
        }
        messageRef.set(messageDto).addOnCompleteListener {
            binding.inputMessage.text.clear()
            encodedImage = null
            binding.imageInchat.setImageResource(R.drawable.ic_baseline_photo_24)
            getAllMessage(club)
        }
    }

    private fun getReadableDateTime(date: Date): String? {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun getAllMessage(userClub: String) {
        Log.d("TAG", "getAllMessage: userClub $userClub")
        changeLoadingStatus(true)
        val messageList = ArrayList<Message>()
        val count: Int = messageList.size
        db.collection("Messages")
            .whereEqualTo("fromClub", userClub)
            .get()
            .addOnCompleteListener {
                Log.d("TAG", "getAllMessage: ${it.result.documents.size}")
            }
            .addOnSuccessListener { documents ->
                val messages = documents.toObjects(Message::class.java)
                messages.sortBy {
                    it.time
                }
                changeLoadingStatus(false)
                changeEmptyStatus(messages.isNotEmpty())

                initMessageAdapter(messages)
//
//                Collections.sort(messageList, Comparator<Message> { obj1: Message, obj2: Message ->
//                    obj2.dateTime!!.compareTo(
//                        obj1.dateTime!!
//                    )
//                })
//                if (count == 0) {
//                    messageAdpter.notifyDataSetChanged()
//                } else {
//                    messageAdpter.notifyItemRangeInserted(messageList.size, messageList.size)
//                    binding.chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
//                }
            }
    }

    private fun initMessageAdapter(messages: List<Message>) {
        messageAdpter =
            MessageAdpter(messages, preferenceManager!!.getString(Constants.KEY_USER_ID))
        binding.chatRecyclerView.adapter = messageAdpter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun changeEmptyStatus(status: Boolean) {
        binding.chatRecyclerView.visibility = if (status) View.VISIBLE else View.GONE
    }

    private fun changeLoadingStatus(status: Boolean) {
        binding.progressBar.visibility = if (status) View.VISIBLE else View.GONE

    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPopupDialog(imageView: FrameLayout) {
        val popupMenu = PopupMenu(this, imageView)
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popupMenu.menuInflater.inflate(R.menu.photo_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_gallery) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                launchSomeActivity.launch(intent)
            } else if (menuItem.itemId == R.id.action_camer) {
                dispatchTakePictureIntent()
            }
            true
        }
        popupMenu.show()
    }

    var launchSomeActivity = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data!!.data != null) {
                val imageUri = data.data
                val photo: Bitmap? = null
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageInchat.setImageBitmap(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                encodedImage = imageUri
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent,CAMERA_REQUEST)
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun dispatchTakePictureIntent() {
        if (checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),MY_CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                var photo: Bitmap? = null
                if (data != null) {
                    photo = data.extras!!["data"] as Bitmap?
                    encodedImage = getImageUri(this@ChatActivity, photo!!)

                    Log.e("imagee", encodedImage.toString() + "lll66l")
                    Log.e("imagee", data.extras!!["data"].toString() + "lll6l")
                }
                binding.imageInchat.setImageBitmap(photo)
            }
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            try {
//                val imageUri = data!!.data
//                val imageStream = contentResolver.openInputStream(imageUri!!)
//                val selectedImage = BitmapFactory.decodeStream(imageStream)
//                binding.imageInchat.setImageBitmap(selectedImage)
//                encodedImage = imageUri
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//                Toast.makeText(this@ChatActivity, "Something went wrong", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Toast.makeText(this@ChatActivity, "You haven't picked Image", Toast.LENGTH_LONG).show()
//        }
//    }
}