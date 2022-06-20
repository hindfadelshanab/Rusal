package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobileq.rusal.rusalapp.developer3456.adapter.MessageAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityChatBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var messageAdpter: MessageAdpter
    lateinit var db: FirebaseFirestore
    var currentStudent: User = User()
    private var encodedImage: Uri? = null

    private val RESULT_LOAD_IMG = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(this)
        db = FirebaseFirestore.getInstance()
        preferenceManager!!.getString(Constants.KEY_USER_ID)
        var isStudent = preferenceManager!!.getBoolean(Constants.KEY_IS_Student)

        //   isStudent = intent.getBooleanExtra("isStudent", false)

        if (isStudent) {
            db.collection(Constants.KEY_COLLECTION_STUDENT)
                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
                .get().addOnSuccessListener { doc ->
                    currentStudent = doc.toObject(User::class.java)!!
                }
        } else {
            db.collection(Constants.KEY_COLLECTION_TEACHER)
                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
                .get().addOnSuccessListener { doc ->
                    currentStudent = doc.toObject(User::class.java)!!
                }
        }
        var studentClub = intent.getStringExtra("userClub")

        binding.textClubName.setText(studentClub)

        getAllMessage(studentClub.toString())






        binding.layoutPhoto.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
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
            if (binding.inputMessage.text.toString().isEmpty()){
                Toast.makeText(this , "Enter message" , Toast.LENGTH_LONG).show()
            }else {
                sendMessage(studentClub.toString(), encodedImage)

            }
        }
        setContentView(binding.root)
    }


    fun sendMessage(userClub:String, fileUri: Uri?) {

    if (fileUri !=null){
        var fileName = UUID.randomUUID().toString() + ".jpg";
        var refStorage  :StorageReference= FirebaseStorage.getInstance().getReference().child("images/"+fileName);

        refStorage.putFile(fileUri).addOnSuccessListener(OnSuccessListener{task ->
            var result =task.storage.downloadUrl
            result.addOnSuccessListener(OnSuccessListener {it
                it.toString()
                val ref: DocumentReference =
                    db.collection("Messages").document()


                var message = Message()
                var usersList: ArrayList<User> = intent.getParcelableArrayListExtra<User>("users")!!
                message.message = binding.inputMessage.text.toString()
                message.messageId = ref.id
                message.senderId = preferenceManager!!.getString(Constants.KEY_USER_ID)
                message.senderName = currentStudent.name
                message.senderImage = currentStudent.image
                message.users = usersList
                message.messageImage = it.toString()
                message.fromClub = userClub
                message.dateTime = getReadableDateTime(Date())
             //   message.messageImage = it.toString()
                ref.set(message).addOnSuccessListener { doc ->
                    binding.inputMessage.text.clear()
                    encodedImage = null
                    binding.imageInchat.setImageResource( R.drawable.ic_baseline_photo_24)
                    getAllMessage(userClub)
                }
            })

        })
    }else{
        val ref: DocumentReference =
            db.collection("Messages").document()
        var message = Message()
        var usersList: ArrayList<User> = intent.getParcelableArrayListExtra<User>("users")!!
        message.message = binding.inputMessage.text.toString()
        message.messageId = ref.id
        message.senderId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        message.senderName = currentStudent.name
        message.senderImage = currentStudent.image
        message.users = usersList
//        message.messageImage =""
        message.fromClub = userClub
        message.dateTime = getReadableDateTime(Date())

       // message.messageId = ref.id
        ref.set(message).addOnSuccessListener { doc ->
            binding.inputMessage.text.clear()
            encodedImage = null
            binding.imageInchat.setImageResource( R.drawable.ic_baseline_photo_24)
            getAllMessage(userClub)
        }
    }





















//            .addOnSuccessListener {  }
//        if (currentStudent.club =="نادي الاعلام"){
//            val ref: DocumentReference = db.collection("PressChat").document()
//            message.messageId = ref.id
//            ref.set(message)
//
//        }else if (currentStudent.club =="نادي الوسائط"){
//
//        }


    }

    private fun getReadableDateTime(date: Date): String? {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    fun getAllMessage(userClub: String) {
        var messageList = ArrayList<Message>()
        val count: Int = messageList.size
        db.collection("Messages").get().addOnSuccessListener { documents ->

            for (doc in documents) {
                var message = doc.toObject<Message>()
                if (message.fromClub.equals(userClub)) {
                    messageList.add(message)
                }
            }
            binding.progressBar.visibility = View.GONE
            binding.chatRecyclerView.visibility = View.VISIBLE
            messageAdpter =
                MessageAdpter(messageList, preferenceManager!!.getString(Constants.KEY_USER_ID))
            binding.chatRecyclerView.setAdapter(messageAdpter)
            binding.chatRecyclerView.setLayoutManager(LinearLayoutManager(this))
            binding.chatRecyclerView.scrollToPosition(messageList.size - 1)

//            Collections.sort(messageList, Comparator<Message> { obj1: Message, obj2: Message ->
//                obj1.dateTime!!.compareTo(
//                    obj2.dateTime!!
//                )
//            })

            Collections.sort(messageList, Comparator<Message> { obj1: Message, obj2: Message ->
                obj2.dateTime!!.compareTo(
                    obj1.dateTime!!
                )
            })
            if (count == 0) {
                messageAdpter.notifyDataSetChanged()
            } else {
                messageAdpter.notifyItemRangeInserted(messageList.size, messageList.size)
                binding.chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
            }

//
//            if (count == 0) {
//                messageAdpter.notifyDataSetChanged()
//            } else {
//                messageAdpter.notifyItemRangeInserted(messageList.size, messageList.size)
//                binding.chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
//            }
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
                Toast.makeText(this@ChatActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this@ChatActivity, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

//    @Override
//    protected fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(reqCode, resultCode, data)
//
//    }
}