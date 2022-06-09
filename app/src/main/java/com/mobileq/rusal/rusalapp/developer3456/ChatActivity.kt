package com.mobileq.rusal.rusalapp.developer3456

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mobileq.rusal.rusalapp.developer3456.adapter.MessageAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityChatBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var messageAdpter: MessageAdpter
    lateinit var db: FirebaseFirestore
    var currentStudent: User = User()


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
        binding.layoutSend.setOnClickListener {
            var message = Message()
            var usersList: ArrayList<User> = intent.getParcelableArrayListExtra<User>("users")!!
            message.message = binding.inputMessage.text.toString()
            message.senderId = preferenceManager!!.getString(Constants.KEY_USER_ID)
            message.senderName = currentStudent.name
            message.senderImage = currentStudent.image
            message.users = usersList
            message.fromClub = studentClub
            message.dateTime = getReadableDateTime(Date())
            sendMessage(message ,studentClub.toString())

        }
        setContentView(binding.root)
    }


    fun sendMessage(message: Message , userClub:String) {

        val ref: DocumentReference =
            db.collection("Messages").document()
        message.messageId = ref.id
        ref.set(message).addOnSuccessListener { doc ->
            binding.inputMessage.text.clear()
            getAllMessage(userClub)
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
            Collections.sort(messageList, Comparator<Message> { obj1: Message, obj2: Message ->
                obj1.dateTime!!.compareTo(
                    obj2.dateTime!!
                )
            })

            if (count == 0) {
                messageAdpter.notifyDataSetChanged()
            } else {
                messageAdpter.notifyItemRangeInserted(messageList.size, messageList.size)
                binding.chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
            }
        }
    }
}