package com.example.finalproject_work

import android.annotation.SuppressLint
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Make sure this matches the name of your layout file

        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val signActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    // The user has successfully signed in or he/she is a new user

                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d(TAG, "onActivityResult: $user")

                    //Checking for User (New/Old)
                    if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp){
                        //This is a New User
                        Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()
                        val db = FirebaseFirestore.getInstance()
                        val userRef = user?.let { db.collection("users").document(it.uid) }

                        if (userRef != null) {
                            val newUser = //creating a new user
                                    UserData(user.uid, //set id
                                    user.displayName?.split(" ")?.getOrNull(0),//first name
                                    user.displayName?.split(" ")?.getOrNull(1),//last name
                                    user.email, //email
                                    null, //setting pictures to null
                                    null,
                                    null)

                            userRef.set(newUser).addOnSuccessListener {
                                Log.d(TAG, "New user added to Firestore: ${user.uid}")
                            }.addOnFailureListener { e ->
                                Log.e(TAG, "Error adding new user to Firestore: ${user.uid}", e)
                            }
                        }
                    } else {
                        //This is a returning user
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                    }

                    // Since the user signed in, the user can go back to main activity
                    startActivity(Intent(this, MainActivity::class.java))
                    // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity
                    finish()

                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    val response = IdpResponse.fromResultIntent(result.data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: the user has cancelled the sign in request")
                    } else {
                        Log.e(TAG, "onActivityResult: ${response.error?.errorCode}")
                    }
                }

            }

            // Login Button
            findViewById<Button>(R.id.login_button).setOnClickListener {
                // Choose authentication providers -- make sure enable them on your firebase account first
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    //AuthUI.IdpConfig.GoogleBuilder().build()
                    //AuthUI.IdpConfig.PhoneBuilder().build(),
                    //AuthUI.IdpConfig.FacebookBuilder().build(),
                    //AuthUI.IdpConfig.TwitterBuilder().build()
                )

                // Create  sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                    //.setLogo(R.drawable.baseline_cake_24)
                    .setAlwaysShowSignInMethodScreen(true) // use this if you have only one provider and really want the see the signin page
                    .setIsSmartLockEnabled(false)
                    .build()

                // Launch sign-in Activity with the sign-in intent above
                signActivityLauncher.launch(signInIntent)
            }

        }








    }
}
