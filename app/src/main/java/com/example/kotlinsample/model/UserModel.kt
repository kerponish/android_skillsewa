package com.example.kotlinsample.model

data class UserModel(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val dob: String = "",
    val gender: String = "",
    val profilePicture: String = "" // URL or path to profile picture
)
