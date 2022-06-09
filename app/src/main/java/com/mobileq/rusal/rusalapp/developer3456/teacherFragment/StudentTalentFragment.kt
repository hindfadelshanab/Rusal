package com.mobileq.rusal.rusalapp.developer3456.teacherFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdpter
import com.mobileq.rusal.rusalapp.developer3456.adapter.TalentAdpter
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentStudentTalentBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentTalentBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Talent
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager

class StudentTalentFragment : Fragment() {


    lateinit var binding: FragmentStudentTalentBinding
    private var preferenceManager: PreferenceManager? = null

    lateinit var db: FirebaseFirestore
    lateinit var talentAdapter:TalentAdpter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStudentTalentBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(activity)

        db = FirebaseFirestore.getInstance()
        getAllTslent()
        return binding.root
    }

    fun getAllTslent() {
         var teacher :User =User()
        db.collection(Constants.KEY_COLLECTION_TEACHER)
            .document(preferenceManager!!.getString(Constants.KEY_USER_ID))
            .get().addOnSuccessListener { doc ->
                teacher = doc.toObject(User::class.java)!!
            }
        var data = ArrayList<Talent>()
        db.collection(Constants.KEY_COLLECTION_TALENT).get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                for (documentSnapshotpost in queryDocumentSnapshots) {
                    var talent = documentSnapshotpost.toObject(Talent::class.java)
                    if(talent.studentClub.equals(teacher.club)) {
                        data.add(talent)
                    }

                }
                talentAdapter = TalentAdpter( data )
                binding.talentRc.setAdapter(talentAdapter)
                Log.e("hin", data.size.toString() + "")
                binding.talentRc.setLayoutManager(LinearLayoutManager(activity))


            })
    }


}