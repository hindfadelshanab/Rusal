package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
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
import java.io.FileNotFoundException
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
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
        }



    }

    private fun getReadableDateTime(date: Date): String? {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    fun getAllMessage(receiverUser: User) {
        var messageList = ArrayList<Message>()
        val count: Int = messageList.size


        db.collection("Messages")
            .whereEqualTo("senderId", preferenceManager!!.getString(Constants.KEY_USER_ID))
            .whereEqualTo("receiverId", preferenceManager!!.getString(Constants.KEY_USER))
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                binding.imageInchat.setImageBitmap(selectedImage)
                encodedImage = imageUri
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }
}