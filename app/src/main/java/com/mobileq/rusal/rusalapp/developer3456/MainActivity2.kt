package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.mobileq.rusal.rusalapp.developer3456.Frgmant.MakeChatActivity
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityMain2Binding
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import com.squareup.picasso.Picasso

class MainActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    private var preferenceManager: PreferenceManager? = null
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain2.toolbar)
        preferenceManager = PreferenceManager(this)
        db = FirebaseFirestore.getInstance()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        getTeacherInfo(preferenceManager!!.getString(Constants.KEY_USER_ID))
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home2 ,R.id.nav_student_talent  ,R.id.nav_public,
                R.id.nav_contact , R.id.nav_support , R.id.nav_who_are
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
        if (item.itemId==R.id.nav_chat){
            goToChat()
        }else if(item.itemId ==R.id.nav_logout){
            preferenceManager!!.clear()
            startActivity(Intent(this , SignInActivity::class.java))
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun goToChat() {
        db.collection(Constants.KEY_COLLECTION_TEACHER).document(preferenceManager!!.getString(Constants.KEY_USER_ID)).get()
            .addOnSuccessListener { doc ->
                var student = doc.toObject(User::class.java)!!

                var intent = Intent(this, MakeChatActivity::class.java)
                intent.putExtra("isStudent", false)
                intent.putExtra("userClub",student.club)
                startActivity(intent)
            }



    }

    fun getTeacherInfo(teacherId: String) {

        db.collection(Constants.KEY_COLLECTION_TEACHER).document(teacherId).get()
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
}