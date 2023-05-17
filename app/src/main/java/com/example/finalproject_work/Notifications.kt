package com.example.finalproject_work

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
//import com.example.finalproject_work.dashboard.Dashboard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects


class Notifications : AppCompatActivity() {

    private lateinit var fireBaseDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Initialize and assign variable
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.notifications

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    startActivity(Intent(applicationContext, Dashboard::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.notifications -> return@OnNavigationItemSelectedListener true
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        /*val currentUser = FirebaseAuth.getInstance().currentUser
        val username = findViewById<TextView>(R.id.username_text_view_profile_view)
        if (currentUser != null) {
            username.text = currentUser.displayName
            Glide.with(this)
                .load(currentUser.photoUrl)
                .circleCrop()
                .into(findViewById<ImageView>(R.id.profile_picture))
        }*/
    }
}