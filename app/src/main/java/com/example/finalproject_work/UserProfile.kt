package com.example.finalproject_work

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserProfile : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize and assign variable
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.profile

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
                R.id.dashboard -> {
                    startActivity(Intent(applicationContext, Dashboard::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> return@OnNavigationItemSelectedListener true
            }
            false
        })

        val editBioBtn = findViewById<Button>(R.id.edit_bio_btn)
        val editBioEditText = findViewById<EditText>(R.id.bio_edit_text)
        val enterBtn = findViewById<Button>(R.id.enter_bio_btn)
        val bioTextView = findViewById<TextView>(R.id.bio_text_view_profile_view)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val username = findViewById<TextView>(R.id.username_text_view_profile_view)
        val profilePic = findViewById<ImageView>(R.id.profile_picture)
        //val profilePicUrl = currentUser.profilePic
        if (currentUser != null) {
            username.text = currentUser.displayName
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(currentUser.uid)
                .addSnapshotListener { document, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (document != null && document.exists()) {
                        val profilePicUrl = document.getString("profilePic")
                        Glide.with(this)
                            .load(profilePicUrl)
                            .circleCrop()
                            .into(profilePic)
                        bioTextView.text = document.getString("bio")
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }

        editBioBtn.setOnClickListener{
            editBioEditText.visibility = View.VISIBLE
            enterBtn.visibility = View.VISIBLE
        }
        enterBtn.setOnClickListener{
            val db = FirebaseFirestore.getInstance()
            val newBio = editBioEditText.text.toString()
            if (currentUser != null) {
                val userRef = db.collection("users").document(currentUser.uid)
                userRef.update("bio", newBio)
                    .addOnSuccessListener {
                        // Update successful
                        Log.d(TAG, "User bio updated successfully")
                    }
                    .addOnFailureListener { exception ->
                        // Update failed
                        Log.d(TAG, "Error updating user bio: $exception")
                    }
            }
            editBioEditText.visibility = View.INVISIBLE
            enterBtn.visibility = View.INVISIBLE

        }

        /*val images = ArrayList<String?>()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = currentUser?.let { db.collection("users").document(it.uid) }
        collectionRef?.get()?.addOnSuccessListener { document ->
            val image = document.getString("image")
            val profilePic = document.getString("profilePic")
            images.add(image)
            images.add(profilePic)
            val imagesAdapter = ImagesAdapter(images)
            val recyclerView = findViewById<RecyclerView>(R.id._profile_recycler_view)
            recyclerView.adapter = imagesAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@UserProfile, LinearLayoutManager.HORIZONTAL, false)
        }?.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting documents: ", exception)
        }*/



        /*val images = ArrayList<String?>()
        val image = currentUser.image
        images.add(user.profilePic)
        val imagesAdapter = ImagesAdapter(images)


        val recyclerView = findViewById<RecyclerView>(R.id.clicked_profile_recycler_view)
        recyclerView.adapter = imagesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@UserProfile, LinearLayoutManager.HORIZONTAL, false)*/

        //val sharedPref = getSharedPreferences("picture", MODE_PRIVATE)
        //findViewById<ImageView>(R.id.capturedPhoto).setImageBitmap(decodeBase64(sharedPref.getString("profileImage", null)))

    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte: ByteArray = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }

}