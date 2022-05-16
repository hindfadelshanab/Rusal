package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivitySignInBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this , MainActivity::class.java))

        })
        binding.textSignIn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this , SignInActivity::class.java))

        })
    }
}