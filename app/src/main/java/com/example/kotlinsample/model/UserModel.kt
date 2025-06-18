package com.example.kotlinsample.model

data class UserModel(
    val firstName: String = "",
    val secondName: String = "",
    val email: String = "",
    val password: String = "",
    val dob: String = "",
    val gender: String = "" // You're passing an empty string for now
)
