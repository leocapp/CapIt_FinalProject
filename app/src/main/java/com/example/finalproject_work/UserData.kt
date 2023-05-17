package com.example.finalproject_work

import java.io.Serializable

data class UserData (
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var bio: String? = null,
    var profilePic: String? = null,
    var image: String? = null
):Serializable