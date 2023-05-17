package com.example.finalproject_work

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class profileView : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)


        val user = intent.getSerializableExtra("user") as UserData

        val nameTextView = findViewById<TextView>(R.id.username_clicked_profile_view)
        nameTextView.text = "${user.firstName} ${user.lastName}"

        val bioTextView = findViewById<TextView>(R.id.bio_text_view_clicked)
        bioTextView.text = user.bio

        val profileImageView = findViewById<ImageView>(R.id.profile_view_profile_pic)
        Glide.with(this)
            .load(user.profilePic)
            .circleCrop()
            .into(profileImageView)


        val images = ArrayList<String?>()
        val image = user.image
        images.add(image)
        images.add(user.profilePic)
        val imagesAdapter = ImagesAdapter(images)


        val recyclerView = findViewById<RecyclerView>(R.id.clicked_profile_recycler_view)
        recyclerView.adapter = imagesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@profileView, LinearLayoutManager.HORIZONTAL, false)

        /*val postImageView = findViewById<ImageView>(R.id.post_image_view)
        Glide.with(this)
            .load(user.image)
            .into(postImageView)*/


        /*val username = intent.getStringExtra("username")
        val postedPic = intent.getStringExtra("postedPic")
        val usernameField = findViewById<TextView>(R.id.username_clicked_profile_view)
        val postedPicField = findViewById<ImageView>(R.id.post_image_view_profile)
        usernameField.text = username
        if (postedPic != null){
            postedPicField.setImageResource(postedPic.toInt())
        }*/


    }
}