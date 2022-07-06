package com.mobileq.rusal.rusalapp.developer3456

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityProfileBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.ChangeNameDilaogBinding
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {


    lateinit var binding: ActivityProfileBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this);
        database = FirebaseFirestore.getInstance()
        var userId = intent.getStringExtra("userId");
        var isMyProfile = intent.getBooleanExtra("myProfile" , false);
        getUserInfo(userId.toString())
        if (isMyProfile ||userId.equals(preferenceManager.getString(Constants.KEY_USER_ID))){
            binding.cardChangeName.visibility=View.VISIBLE
            binding.txtSignOut.visibility=View.VISIBLE
        }
        binding.cardChangeName.setOnClickListener{
            showChangeNameDialog(userId.toString())
        }
        binding.txtSignOut.setOnClickListener{
            logOut()
        }

    }

    private fun getUserInfo(userId: String) {
        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnFailureListener {
                Log.d("TAG", "getUserInfo: ")
//                database.collection(Constants.KEY_COLLECTION_TEACHER)
//                    .document(userId)
//                    .get().addOnSuccessListener(OnSuccessListener { doc ->
//                        if (doc != null) {
//                            var user = doc.toObject(User::class.java)
//                            user?.let { refreshUi(it) }
//                        }
//                    })
            }
            .addOnSuccessListener(OnSuccessListener { doc ->
                var user = doc.toObject(User::class.java)

                user?.let { refreshUi(it) }

            })
//        database.collection(Constants.KEY_COLLECTION_TEACHER)
//            .document(userId)
//            .get().addOnSuccessListener(OnSuccessListener { doc ->
//                if (doc != null) {
//                    var user = doc.toObject(User::class.java)
//                    user?.let { refreshUi(it) }
//                }
//            })

    }
    private fun updateUserInfo(userId: String , newName :String) {
        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnFailureListener {
//                database.collection(Constants.KEY_COLLECTION_TEACHER)
//                    .document(userId)
//                    .get().addOnSuccessListener(OnSuccessListener { doc ->
//                        if (doc != null) {
//                            var user = doc.toObject(User::class.java)
//                            database.collection(Constants.KEY_COLLECTION_STUDENT)
//                                .document(userId)
//                                .update("name",newName)
//                                .addOnSuccessListener{
//                                    binding.txtUserNameProfile.setText(newName)
//                                }
//                            user?.let { refreshUi(it) }
//                        }
//                    })
            }
            .addOnSuccessListener(OnSuccessListener { doc ->
                var user = doc.toObject(User::class.java)
                database.collection(Constants.KEY_COLLECTION_STUDENT)
                    .document(userId)
                    .update("name",newName)
                    .addOnSuccessListener{
                        binding.txtUserNameProfile.setText(newName)
                    }
                user?.let { refreshUi(it) }

            })

    }

    private fun refreshUi(user: User) {
        Picasso.get().load(user!!.image)
            .into(binding.profileImage)

        binding.txtUserNameProfile.setText(user.name)
        binding.txtUserEmailProfile.setText(user.email)
        binding.txtUserClubProfile.setText(user.club)
        binding.txtUserClubProfile.setOnClickListener {
            var intent = Intent(this , ClubActivity::class.java)
            intent.putExtra("club" , user.club)
            startActivity(intent)
        }

    }
    private fun showChangeNameDialog(userId: String) {

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val changeNameDilaogBinding: ChangeNameDilaogBinding =
            ChangeNameDilaogBinding.inflate(layoutInflater)

        dialog.setContentView(changeNameDilaogBinding.root)
        dialog.window!!.setLayout(6 * width / 7, 1 * height / 4)

        changeNameDilaogBinding.btnDone.setOnClickListener {

            val newName = changeNameDilaogBinding.inputNewValue.text.toString()
            if (!newName.isEmpty()) {
                updateUserInfo(userId , newName)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Failed Empty!", Toast.LENGTH_LONG).show()
            }

        }
        changeNameDilaogBinding.btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logOut(){
        preferenceManager!!.clear()
        startActivity(Intent(this , SignInActivity::class.java))
    }


}