package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentHomeBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentTalentBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Talent
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso


class TalentFragment : Fragment() {


    lateinit var binding: FragmentTalentBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore
    lateinit var student: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTalentBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(activity)
        db = FirebaseFirestore.getInstance()

        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        getUserInfo(userId)

        binding.btnSendTalent.setOnClickListener(View.OnClickListener {
            addPost(userId, it)
        })

        return binding.root
    }


    fun getUserInfo(userId: String) {
        db.collection(Constants.KEY_COLLECTION_USERS).document(userId).get()
            .addOnSuccessListener { doc ->
                student = doc.toObject(User::class.java)!!

                binding.txtNameStudentTalent.text = student.name

                Picasso.get()
                    .load(student.image)
                    .into(binding.imageStudentTalent)

            }

    }

    fun addPost(userId: String, view: View) {
        binding.txtInputTalent.text.toString()
        var talent: Talent = Talent()
        talent.studentName = student.name.toString()
        talent.studentClub = student.club.toString()
        talent.studentId = userId
        talent.studentImage = student.image.toString()
        talent.studentTalent = binding.txtInputTalent.text.toString()

        db.collection(Constants.KEY_COLLECTION_TALENT).add(talent).addOnSuccessListener { doc ->
            Snackbar.make(view, "talent Send Success", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }


}