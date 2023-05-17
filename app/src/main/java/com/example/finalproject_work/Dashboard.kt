package com.example.finalproject_work

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject_work.databinding.ActivityDashboardBinding
import com.example.finalproject_work.databinding.ActivityMainBinding
import com.firebase.ui.auth.data.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class Dashboard : AppCompatActivity() {

    val REQUEST_CODE = 2000
    private lateinit var imageView: ImageView
    private lateinit var imageBitmap: Bitmap
    private lateinit var postImageView: ImageView
    private lateinit var bitmapImage: Bitmap
    private lateinit var btnImageCapture: Button
    private lateinit var binding : ActivityDashboardBinding
    private lateinit var currentUser: FirebaseUser
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var storageRef : StorageReference
    private lateinit var firebaseFirestore : FirebaseFirestore
    private var imageUri : Uri? = null
    private var typeOfImage = false

    val blah = "Give me a random simple, funny idea for a picture to take and then post on social media. Keep it relatively short"
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize and assign variable
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.dashboard

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.notifications -> {
                    startActivity(Intent(applicationContext, Notifications::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.dashboard -> return@OnNavigationItemSelectedListener true
                R.id.profile -> {
                    startActivity(Intent(applicationContext, UserProfile::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        binding.fromGalleryImageView.setImageResource(R.drawable.image_placeholder)
        imageView = findViewById(R.id.imageCaptureImageView)
        btnImageCapture = findViewById(R.id.takePhotoButton)

        btnImageCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE)
        }

        initVars()
        registerClickEvents()

    }

    private fun registerClickEvents() {
        binding.uploadFromGalleryBtn.setOnClickListener {
            typeOfImage = false
            uploadImage()
        }
        binding.profilePicBtn.setOnClickListener{
            typeOfImage = true
            uploadImage()
        }
        binding.fromGalleryImageView.setOnClickListener{
            resultLauncher.launch("image/*")
        }
    }

    private fun uploadImage() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val documentId = currentUser!!.uid
        storageRef = storageRef.child("Images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(imageUri!!).addOnCompleteListener{task ->
            if(task.isSuccessful) {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val updateImage = UserData(
                        image = uri.toString()
                    )
                    //make update instead of add
                    firebaseFirestore.collection("users")
                        .whereEqualTo("id", documentId)
                        .get()
                        .addOnSuccessListener {documents ->
                            for(document in documents){
                                if(document != null){
                                    if(typeOfImage){
                                        document.reference.update("profilePic", uri.toString())
                                        Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    }else{
                                        document.reference.update("image", uri.toString())
                                        Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }
                            //Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            binding.fromGalleryImageView.setImageResource(R.drawable.image_placeholder)
                        }
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
        imageUri = it
        binding.fromGalleryImageView.setImageURI(it)
    }

    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
        }
    }






    /*// Get a reference to the Firebase Storage instance
    val storage = FirebaseStorage.getInstance()
// Create a reference to the image file in Firebase Storage
    val imageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
// Get the URI of the captured image
    val imageUri = data?.data
// Upload the image to Firebase Storage
    if (imageUri != null) {
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                Toast.makeText(this@Dashboard, "Image uploaded", Toast.LENGTH_SHORT).show()

                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Do something with the download URL, such as save it to Firestore
                    // or display the image in an ImageView
                }
            }
            .addOnFailureListener { exception ->
                // Image upload failed
                Toast.makeText(
                    this@Dashboard,
                    "Error uploading image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
}*/

    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
        Log.d("Image Log:", imageEncoded)
        return imageEncoded
    }

    fun postPhoto(view: View){

        if (imageView != null) {
            imageView.buildDrawingCache()
            val bitmap = imageView.getDrawingCache()
            val sharedPref = getSharedPreferences("picture", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("profileImage", encodeTobase64(bitmap))
            editor.commit()
        }

    }


    data class RequestBody(
        @SerializedName("model")
        val model: String,
        @SerializedName("prompt")
        val prompt: String,
        @SerializedName("max_tokens")
        val maxTokens: Int,
        @SerializedName("temperature")
        val temperature: Double
    )

    data class ResponseBody(
        @SerializedName("choices")
        val choices: List<Choice>
    )

    data class Choice(
        @SerializedName("text")
        val text: String
    )
    fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = "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"
        val url = "https://api.openai.com/v1/"

        val requestBody =RequestBody(
            model = "text-davinci-003",
            prompt = question,
            maxTokens = 500,
            temperature = 0.5
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val call = service.getResponse(
            "Bearer $apiKey",
            requestBody
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Api Failure", t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val textResult = response.body()?.choices?.get(0)?.text ?: "Empty"
                callback(textResult)
            }
        })
    }

    fun random(view: View) {
        val answer = findViewById<TextView>(R.id.chat_text_view)
        getResponse(blah) { response ->
            runOnUiThread {
                answer.text = response
            }
        }
    }

    interface ApiService {
        @POST("completions")
        fun getResponse(
            @Header("Authorization") authorization: String,
            @Body requestBody: RequestBody
        ): Call<ResponseBody>
    }
}