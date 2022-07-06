package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobileq.rusal.rusalapp.developer3456.ChatActivity
import com.mobileq.rusal.rusalapp.developer3456.adapter.UserAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityMakeChatBinding
import com.mobileq.rusal.rusalapp.developer3456.listeners.UserListener
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.util.*

class MakeChatActivity : AppCompatActivity() ,UserListener {
    var isExites :Boolean =false
    lateinit var binding: ActivityMakeChatBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore
     var isStudent :Boolean =false
    lateinit var userAdpter:UserAdpter
    var currentStudent: User =User()
    private var conversationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakeChatBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(this)
        db = FirebaseFirestore.getInstance()
        preferenceManager!!.getString(Constants.KEY_USER_ID)
       isStudent = intent.getBooleanExtra("isStudent" , false)
       var studentClub = intent.getStringExtra("userClub" )

        var data= getAllUsers(studentClub.toString())
        binding.btnStartChat.setOnClickListener{
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("users" , data)
            intent.putExtra("userClub" , studentClub)
            startActivity(intent)
        }

        binding.clubName.text =studentClub
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
//        }else{
//            db.collection(Constants.KEY_COLLECTION_USERS)
//                .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
//                .get().addOnSuccessListener { doc ->
//                    currentStudent = doc.toObject(User::class.java)!!
//                }
//        }

        setContentView(binding.root)
    }



    fun  getAllUsers(userClub:String) :ArrayList<User>{
    //    Toast.makeText(this , userClub ,Toast.LENGTH_LONG).show()

        var data = ArrayList<User>()
        db.collection(Constants.KEY_COLLECTION_USERS).get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                for (documentSnapshotpost in queryDocumentSnapshots) {
                    var teacher = documentSnapshotpost.toObject(User::class.java)
                    if (teacher.club.equals(userClub)) {
                        data.add(teacher)

                    }
                }

                binding.progressBar.visibility=View.GONE
                binding.usersRc.visibility=View.VISIBLE
                userAdpter = UserAdpter(data , this)
                binding.usersRc.setAdapter(userAdpter)
                Log.e("hin", data.size.toString() + "")
                binding.usersRc.setLayoutManager(LinearLayoutManager(this))


            }).addOnFailureListener(OnFailureListener { e ->
                binding.progressBar.visibility=View.VISIBLE

                Log.e("hind", e.message!!)
            })


        return data
    }

    override fun onUserClicked(user: User?) {
        val intent = Intent(this  ,IndividualismChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        preferenceManager!!.putString(Constants.KEY_USER , user!!.id)

        startActivity(intent)
    }
//    fun getAllUser(userId: String) :ArrayList<User> {
//         var currentStudent: User =User()
//        var data = ArrayList<User>()
//
//
//
//        if (isStudent) {
//            db.collection(Constants.KEY_COLLECTION_STUDENT)
//                .document(userId)
//                .get().addOnSuccessListener { doc ->
//                    currentStudent = doc.toObject(User::class.java)!!
//                }.addOnCompleteListener({
//
//                })
//        }else{
//            db.collection(Constants.KEY_COLLECTION_USERS)
//                .document(userId)
//                .get().addOnSuccessListener { doc ->
//                    currentStudent = doc.toObject(User::class.java)!!
//                }
//        }
//
//        db.collection(Constants.KEY_COLLECTION_USERS).get()
//            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                for (documentSnapshotpost in queryDocumentSnapshots) {
//                   var teacher = documentSnapshotpost.toObject(User::class.java)
//                    if (teacher.club.equals(currentStudent.club)) {
//                        data.add(teacher)
//
//                    }
//                }
//
//                binding.progressBar.visibility=View.GONE
//                binding.usersRc.visibility=View.VISIBLE
//                userAdpter = UserAdpter(data)
//                binding.usersRc.setAdapter(userAdpter)
//                Log.e("hin", data.size.toString() + "")
//                binding.usersRc.setLayoutManager(LinearLayoutManager(this))
//
//
//            }).addOnFailureListener(OnFailureListener { e ->
//                binding.progressBar.visibility=View.VISIBLE
//
//                Log.e("hind", e.message!!)
//            })
//
//        db.collection(Constants.KEY_COLLECTION_STUDENT).get()
//            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                for (documentSnapshotpost in queryDocumentSnapshots) {
//                    var student = documentSnapshotpost.toObject(User::class.java)
//                    if (student.club.equals(currentStudent.club)) {
//                        data.add(student)
//
//                    }
//
//                }
//
//                binding.progressBar.visibility = View.GONE
//                binding.usersRc.visibility = View.VISIBLE
//                userAdpter = UserAdpter(data)
//                binding.usersRc.setAdapter(userAdpter)
//                Log.e("hin", data.size.toString() + "")
//                binding.usersRc.setLayoutManager(LinearLayoutManager(this))
//            }).addOnFailureListener(OnFailureListener { e ->
//                Log.e("hind", e.message!!)
//                binding.progressBar.visibility=View.VISIBLE
//            })
//
//        return data
//    }
//    private fun addChat(conversation: Chat) {
//
//            val ref: DocumentReference = db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document()
//                conversation.converstionId = ref.id
//              conversationId =ref.id
//
//         ref.set(conversation).addOnSuccessListener {
//                preferenceManager!!.putString(Constants.KEY_Chat_ID , conversation.converstionId)
//                var intent = Intent(this, ChatActivity::class.java)
//                intent.putExtra("users" , conversation.users)
//                intent.putExtra("chatId", conversation.converstionId)
//                startActivity(intent)
//            }
//
//    }
//
//    private fun makeChat(chat: Chat){
//    }

}