package com.mobileq.rusal.rusalapp.developer3456.Frgmant

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobileq.rusal.rusalapp.developer3456.BuildConfig
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentContackUsBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentSupportUsBinding

class SupportUsFragment : Fragment() {


    lateinit var binding:FragmentSupportUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentSupportUsBinding.inflate(inflater , container , false)

        binding.layoutShare.setOnClickListener({
            shareApp()
        })
        binding.layoutRate.setOnClickListener({
            rateApp()
        })
        return binding.root
    }

    fun shareApp(){
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Rusal")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
            Log.e("share" , e.message.toString())

        }
    }

    fun rateApp(){
        try {
            val marketUri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            val marketUri: Uri =
                Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"  )
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        }
    }
}