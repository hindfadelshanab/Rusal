package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentContackUsBinding
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.net.URLEncoder


class ContackUsFragment : Fragment() {

    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore


    lateinit var binding: FragmentContackUsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContackUsBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(activity)
        db = FirebaseFirestore.getInstance()
        preferenceManager!!.getString(Constants.KEY_USER_ID)
        binding.btnSendMessage.setOnClickListener(View.OnClickListener {
            if (isValid()) {
                postMessage(preferenceManager!!.getString(Constants.KEY_USER_ID))
            }
        })


        binding.layoutWhatsapp.setOnClickListener({

            openWhatsApp(binding.txtWhatsappNo.text.toString(), binding.txtInputMessage.text.toString())
        })
        binding.layoutPhone.setOnClickListener({
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${binding.txtPhoneNo}")
            startActivity(intent)
        })
        return binding.root
    }

    private fun postMessage(userId: String?) {
        val message = HashMap<String, Any>()
        message[Constants.KEY_EMAIL] = binding.txtInputEmail.text.toString()
        message["Message"] = binding.txtInputMessage.text.toString()
        db.collection("Contacts").add(message).addOnSuccessListener { doc ->
            Toast.makeText(activity, "Message sent", Toast.LENGTH_LONG).show()
        }
    }

    fun isValid(): Boolean {

        if (binding.txtInputEmail.text.isEmpty()) {
            binding.txtInputEmail.setError("Pleas Enter Email")
            return false
        } else if (binding.txtInputMessage.text.isEmpty()) {
            binding.txtInputMessage.setError("Pleas Enter Message")
            return false
        } else {
            return true
        }

    }

    private fun openWhatsApp(numero: String, mensaje: String) {
        try {
            val packageManager = requireActivity().packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=" + numero + "&text=" + URLEncoder.encode(
                    mensaje,
                    "UTF-8"
                )
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
            } else {     Toast.makeText(
                activity,
                "ERROR WHATSAPP",
                Toast.LENGTH_SHORT
            ).show()
            }
        } catch (e: Exception) {
            Log.e("ERROR WHATSAPP", e.toString())
            Toast.makeText(
                activity,
                "${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
