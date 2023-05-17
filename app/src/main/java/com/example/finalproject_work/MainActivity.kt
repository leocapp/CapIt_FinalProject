package com.example.finalproject_work

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var fireBaseDb: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList : ArrayList<UserData>
    private lateinit var usersAdapter: UsersAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Get a Cloud Firestore instance
        fireBaseDb = FirebaseFirestore.getInstance()


        // #### Authentication using FirebaseAuth #####

        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // If currentUser is null, open the RegisterActivity
        if (currentUser == null) {
            startLoginActivity()
        }

        // Initialize and assign variable
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.home

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> return@OnNavigationItemSelectedListener true
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

                R.id.profile -> {
                    startActivity(Intent(applicationContext, UserProfile::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        // Store the the recyclerView widget in a variable
        recyclerView = findViewById(R.id.recycler_view_home)
        Log.d(TAG, "recyclerView initialized successfully")

        // use a linear layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = arrayListOf<UserData>()
        usersAdapter = UsersAdapter(userList) // Initialize usersAdapter
        recyclerView.adapter = usersAdapter // Set the adapter to recyclerView
        Log.d(TAG, "onCreate: ")
        //getPictureData()
        getUserData()


        /*usersAdapter.setOnItemClickListener(object : UsersAdapter.OnItemClickListener {
            override fun onItemClick(user: UserData) {
                val intent = Intent(this@MainActivity, profileView::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
        })*/

        val sharedPref = getSharedPreferences("theme", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val themeSwitch = findViewById<Switch>(R.id.theme_switch)

        // Retrieve the value of "mode" from shared preferences
        val mode = sharedPref.getBoolean("mode", false)

        // Set the state of the switch based on the value of "mode"
        themeSwitch.isChecked = mode

        // Set the night mode of the app based on the value of "mode"
        if (mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        themeSwitch.setOnClickListener {
            // Update the value of "mode" in shared preferences
            editor.putBoolean("mode", themeSwitch.isChecked)
            editor.apply()

            // Set the night mode of the app based on the new value of "mode"
            if (themeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    // An helper function to start our RegisterActivity
    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Make sure to call finish(), otherwise the user would be able to go back to the MainActivity
        finish()
    }

    // This override function is used to create menu option where you can see on the top right corner
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    // This override function is used to handle if menu_option (logout) is selected.
    // If so, the user will be signed out.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // User chose the "logout" item, logout the user then
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()

                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // After logout, start the RegisterActivity again
                            startLoginActivity()
                        }
                        else {
                            Log.e(TAG, "Task is not successful:${task.exception}")
                        }
                    }
                true
            }
            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }
    }


    fun getUserData() {
        fireBaseDb.collection("users")
            .orderBy("id")
            .get()
            .addOnSuccessListener { documents ->
                val users = mutableListOf<UserData>()
                for (document in documents) {
                    val userData = document.toObject(UserData::class.java)
                    if (userData.image != null) { // Check if the "image" field is not null
                        users.add(userData)
                    }
                }
                userList.addAll(users)
                usersAdapter.notifyDataSetChanged()
                recyclerView.adapter = UsersAdapter(userList)
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents")
            }
    }
}

