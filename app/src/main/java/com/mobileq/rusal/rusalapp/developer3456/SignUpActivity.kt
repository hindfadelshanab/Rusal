package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivitySignUpBinding
import com.mobileq.rusal.rusalapp.developer3456.model.User
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*


class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private var encodedImage: Uri? = null
    private var preferenceManager: PreferenceManager? = null
    val TAG = "SignUp"

    var accountType: String = ""
    var club: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)
        setListener()
        getSupportActionBar()?.setTitle("تسجيل");

    }

    private fun setListener() {

        binding.buttonSignUp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        })

        binding.radio.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            var radioButton = findViewById<View>(checkedId) as RadioButton
            //  Toast.makeText(baseContext, radioButton.getText(), Toast.LENGTH_SHORT).show()
            accountType = radioButton.text.toString()

        }
        )
//        val selectedId: Int = binding.radio.getCheckedRadioButtonId()
//
//        var radioButton = findViewById<View>(selectedId) as RadioButton
        binding.textSignIn.setOnClickListener { view -> onBackPressed() }
        binding.buttonSignUp.setOnClickListener { view ->
            if (isValidSignUpDetails() == true) {
                signUp(encodedImage!!)
            }
        }
        binding.layoutImage.setOnClickListener { view ->
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun signUp(fileUri: Uri) {
        Log.e("image" , "imageee :${fileUri}" )
        loading(true)
        preferenceManager!!.putString(Constants.KEY_Chat_ID ,"")

        val database = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        if (fileUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
            refStorage.putFile(fileUri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                          var imageUrl = it.toString()

                            mAuth.createUserWithEmailAndPassword(
                                binding.inputEmail.text.toString(),
                                binding.inputPassword.text.toString()
                            ).addOnCompleteListener(this,
                                OnCompleteListener<AuthResult?> { task ->
                                    if (task.isSuccessful) {
                                        val currentUser: FirebaseUser? = mAuth.getCurrentUser()
                                        val userId = currentUser!!.uid.toString()
                                        var user: User = User()
                                        user.name = binding.inputName.text.toString()
                                        user.email = binding.inputEmail.text.toString()
                                        user.accountType = accountType
                                        user.club = binding.clubSpinner.selectedItem.toString()
                                        user.image = imageUrl!!
                                        user.id = currentUser!!.uid.toString()
                                        Log.e("image" , "image url :${imageUrl}" )


                                        if (accountType == "طالب") {
                                            database.collection(Constants.KEY_COLLECTION_STUDENT).document(userId)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    loading(false)
                                                    preferenceManager!!.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                                                    preferenceManager!!.putString(
                                                        Constants.KEY_USER_ID,
                                                        userId
                                                    )
                                                    preferenceManager!!.putString(
                                                        Constants.KEY_NAME,
                                                        binding.inputName.text.toString()
                                                    )
                                                    preferenceManager!!.putBoolean(
                                                        Constants.KEY_IS_Student,
                                                       true
                                                    )
                                                    preferenceManager!!.putBoolean(
                                                        Constants.KEY_IS_Teacher,
                                                       false
                                                    )
                                                    preferenceManager!!.putString(Constants.KEY_IMAGE, imageUrl)
                                                    Log.e(TAG, "Sucees Student");

                                                    Log.e("hind", "Sucees");
                                                    Log.e("hind", userId);
                                                    val intent =
                                                        Intent(applicationContext, MainActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    startActivity(intent)
                                                }

                                                .addOnFailureListener { e: Exception ->
                                                    loading(false)
                                                    showToast(e.message)

                                                }

                                        } else if (accountType == "معلم") {

                                            database.collection(Constants.KEY_COLLECTION_TEACHER).document(userId)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    loading(false)
                                                    preferenceManager!!.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                                                    preferenceManager!!.putString(
                                                        Constants.KEY_USER_ID,
                                                        userId
                                                    )
                                                    preferenceManager!!.putString(
                                                        Constants.KEY_NAME,
                                                        binding.inputName.text.toString()
                                                    )
                                                    preferenceManager!!.putBoolean(
                                                        Constants.KEY_IS_Student,
                                                        false
                                                    )
                                                    preferenceManager!!.putBoolean(
                                                        Constants.KEY_IS_Teacher,
                                                        true
                                                    )
                                                    preferenceManager!!.putString(Constants.KEY_IMAGE, imageUrl)
                                                    Log.e(TAG, "Sucees teacher");

                                                    Log.e("hind", "Sucees");
                                                    Log.e("hind", userId);
                                                    val intent =
                                                        Intent(applicationContext, MainActivity2::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener { e: Exception ->
                                                    loading(false)
                                                    showToast(e.message)

                                                }
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            this, "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })

                            Log.e("image" , "image :${it.toString()}" )
                        }
                    })

                .addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }

    }

    private fun encodImage(bitmap: Bitmap): String {
        val preViewWidth = 150
        val preViewHeight = bitmap.height * preViewWidth / bitmap.width
        val preViewBitmap = Bitmap.createScaledBitmap(bitmap, preViewWidth, preViewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        preViewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val imageUri = result.data!!.data
                try {
                    val inputStream =
                        contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = result.data!!.data
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean? {

        return if (encodedImage == null) {
            showToast("Select profile image")

            false
        } else if (binding.inputName.text.toString().trim().isEmpty()) {

            binding.inputName.setError("Enter name")
            false
        } else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter email")
            binding.inputEmail.setError("Enter email")

            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Enter valid email")
            binding.inputEmail.setError("Enter valid email")

            false
        } else if (binding.clubSpinner.selectedItemPosition == 0) {
            showToast("Pleas select Club")
            // binding.inputEmail.setError("Pleas select Club")
            false
        } else if (accountType == "") {
            showToast("Pleas select Account type")
            //binding.inputEmail.setError("Pleas select Account type")
            false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            showToast("Enter password")
            binding.inputPassword.setError("Enter password")

            false
        } else {
            true
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.buttonSignUp.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}