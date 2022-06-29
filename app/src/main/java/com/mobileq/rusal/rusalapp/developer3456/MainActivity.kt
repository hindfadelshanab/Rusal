package com.mobileq.rusal.rusalapp.developer3456

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.Frgmant.MakeChatActivity
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityMainBinding
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)
        db = FirebaseFirestore.getInstance()

        var userId = preferenceManager!!.getString(Constants.KEY_USER_ID)
        getStudentInfo(userId)
        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_talent, R.id.nav_contact, R.id.nav_chat, R.id.nav_support ,
                R.id.nav_who_are
            ), drawerLayout
        )




        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity2, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_chat) {
            goToChat()
        }else if(item.itemId ==R.id.nav_logout){
            preferenceManager!!.clear()
            startActivity(Intent(this , SignInActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun goToChat() {
      //  preferenceManager!!.putString(Constants.KEY_Chat_ID ,"")
        db.collection(Constants.KEY_COLLECTION_STUDENT).document(preferenceManager!!.getString(Constants.KEY_USER_ID)).get()
            .addOnSuccessListener { doc ->
                var student = doc.toObject(User::class.java)!!

                var intent = Intent(this, MakeChatActivity::class.java)
                intent.putExtra("isStudent", true)
                intent.putExtra("userClub",student.club)
                startActivity(intent)
            }



    }

    fun getStudentInfo(studentId: String) {

        db.collection(Constants.KEY_COLLECTION_STUDENT).document(studentId).get()
            .addOnSuccessListener { doc ->
               var student = doc.toObject(User::class.java)!!
                var studentImage =
                    binding.drawerLayout.findViewById<ImageView>(R.id.imageDrawaerStudent)
                var studentName =
                    binding.drawerLayout.findViewById<TextView>(R.id.txtDrawaerStudentName)
                studentName.text = student.name
                var studentEmail=
                    binding.drawerLayout.findViewById<TextView>(R.id.txtDrawaerStudentEmail)
                studentEmail.text = student.email
                Picasso.get()
                    .load(student.image)
                    .into(studentImage)

            }

    }

    fun getAllUser() {

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}