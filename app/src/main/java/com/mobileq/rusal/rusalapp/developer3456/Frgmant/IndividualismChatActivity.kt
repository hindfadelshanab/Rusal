package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.adapter.MessageAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityIndividualismChatBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IndividualismChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityIndividualismChatBinding
    lateinit var db: FirebaseFirestore
    private var preferenceManager: PreferenceManager? = null
    lateinit var messageAdpter: MessageAdpter
    lateinit var chatMessageList: ArrayList<Message>
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
                sendMessage(message, reciverUser!!)
                Log.e("yaaaa", "HHHH ${reciverUser.id.toString()}")
            }
        }

   
    }

    fun sendMessage(message: Message, receiverUser: User) {

        val ref: DocumentReference =
            db.collection("Messages").document()
        message.messageId = ref.id
        ref.set(message).addOnSuccessListener { doc ->
            binding.inputMessage.text.clear()
            getAllMessage(receiverUser)
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
}