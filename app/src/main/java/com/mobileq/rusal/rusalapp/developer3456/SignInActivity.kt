package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignIn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
        })
        binding.textCreateNewAccount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this , SignUpActivity::class.java))
        })
    }
}