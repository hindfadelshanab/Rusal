package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.adapter.MessageAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityIndividualismChatBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IndividualismChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityIndividualismChatBinding
    lateinit var db: FirebaseFirestore
    private var preferenceManager: PreferenceManager? = null
    lateinit var messageAdpter: MessageAdpter
    lateinit var chatMessageList: ArrayList<Message>
    private var encodedImage: Uri? = null

    private val RESULT_LOAD_IMG = 100
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIndividualismChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(this)
        preferenceManager!!.getString(Constants.KEY_USER_ID)
        chatMessageList = ArrayList<Message>()
        var reciverUser = intent.getParcelableExtra<User>(Constants.KEY_USER)
        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        var userImage = preferenceManager!!.getString(Constants.KEY_IMAGE)
        var userName = preferenceManager!!.getString(Constants.KEY_NAME)
        if (reciverUser != null) {
            getAllMessage(reciverUser!!)
        }


        Picasso.get().load(reciverUser!!.image)
            .into(binding.imageChatUserProfile)
        binding.txtChatUserName.text = reciverUser.name
        binding.layoutSend.setOnClickListener {
            if (binding.inputMessage.text.isEmpty()) {
                Toast.makeText(this, "No Message to send", Toast.LENGTH_LONG).show()
            } else {
                var message = Message()

                message.senderId = userId
                message.senderName = userName
                message.senderImage = userImage
                message.receiverId = reciverUser!!.id
                message.receiverImage = reciverUser!!.image
                message.receiverName = reciverUser!!.name
                message.message = binding.inputMessage.text.toString()
                message.dateTime = getReadableDateTime(Date())
                sendMessage(message, reciverUser!! , encodedImage)
                Log.e("yaaaa", "HHHH ${reciverUser.id.toString()}")
            }
        }
        binding.layoutPhoto.setOnClickListener {

//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
//            val intent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
            showPopupDialog( binding.layoutPhoto)
       }
   
    }

    fun sendMessage(message:Message ,  receiverUser: User , fileUri :Uri?) {
        binding.layoutSend.isEnabled =false
        if (fileUri !=null) {
            var fileName = UUID.randomUUID().toString() + ".jpg";
            var refStorage: StorageReference =
                FirebaseStorage.getInstance().getReference().child("images/" + fileName);

            refStorage.putFile(fileUri).addOnSuccessListener(OnSuccessListener { task ->
                var result = task.storage.downloadUrl
                result.addOnSuccessListener(OnSuccessListener { it
                    val ref: DocumentReference =
                        db.collection("Messages").document()
                    message.messageId = ref.id
                    message.messageImage = it.toString()
                    ref.set(message).addOnSuccessListener { doc ->
                        binding.inputMessage.text.clear()
                        getAllMessage(receiverUser)
                        encodedImage = null
                        binding.imageInchat.setImageResource(R.drawable.ic_baseline_photo_24)
                        binding.layoutSend.isEnabled =true

                    }
                });
            });
        }else{
            val ref: DocumentReference =
                db.collection("Messages").document()
            message.messageId = ref.id
            ref.set(message).addOnSuccessListener { doc ->
                binding.inputMessage.text.clear()
                getAllMessage(receiverUser)
                encodedImage = null
                binding.imageInchat.setImageResource(R.drawable.ic_baseline_photo_24)
                binding.layoutSend.isEnabled =true

            }
        }



    }

    private fun getReadableDateTime(date: Date): String? {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    fun getAllMessage(receiverUser: User) {
        var messageList = ArrayList<Message>()
        val count: Int = messageList.size


        db.collection("Messages")
            .orderBy("dateTime")
            .whereEqualTo("senderId", preferenceManager!!.getString(Constants.KEY_USER_ID))
            .whereEqualTo("receiverId", preferenceManager!!.getString(Constants.KEY_USER))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    messageList.addAll(value.toObjects(Message::class.java))
                    messageList.sortBy {
                        it.time
                    }
                    binding.progressbarMessage.visibility = View.GONE
                    binding.messageRc.visibility = View.VISIBLE
                    messageAdpter =
                        MessageAdpter(
                            messageList,
                            preferenceManager!!.getString(Constants.KEY_USER_ID)
                        )
                    binding.messageRc.setAdapter(messageAdpter)
                    binding.messageRc.setLayoutManager(LinearLayoutManager(this))

                }
            }

        db.collection("Messages")
            .whereEqualTo("senderId", receiverUser.id)
            .whereEqualTo("receiverId", preferenceManager!!.getString(Constants.KEY_USER_ID))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    messageList.addAll(value.toObjects(Message::class.java))
                    binding.progressbarMessage.visibility = View.GONE
                    binding.messageRc.visibility = View.VISIBLE
                    messageAdpter =
                        MessageAdpter(
                            messageList,
                            preferenceManager!!.getString(Constants.KEY_USER_ID)
                        )
                    binding.messageRc.setAdapter(messageAdpter)
                    binding.messageRc.setLayoutManager(LinearLayoutManager(this))
                    Collections.sort(
                        messageList,
                        Comparator<Message> { obj1: Message, obj2: Message ->
                            obj1.dateTime!!.compareTo(
                                obj2.dateTime!!
                            )
                        })

                    if (count == 0) {
                        messageAdpter.notifyDataSetChanged()
                    } else {
                        messageAdpter.notifyItemRangeInserted(messageList.size, messageList.size)
                        binding.messageRc.smoothScrollToPosition(messageList.size - 1)
                    }
                }
            }
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
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
//        }
//    }



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
        ActivityResultContracts.StartActivityForResult()
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
                    encodedImage = getImageUri(this, photo!!)

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
}