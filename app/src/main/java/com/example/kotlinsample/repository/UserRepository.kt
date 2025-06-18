package com.example.kotlinsample.repository

import com.example.kotlinsample.model.Product
import com.example.kotlinsample.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {


    fun login (email: String, password: String, callback: (Boolean, String) -> Unit)
    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)

    fun forgetPassword(email: String,
                       callback: (Boolean, String) -> Unit)



    fun editProfile(userId: String,data: MutableMap<String, Any>,callback: (Boolean, String) -> Unit)



    fun getProfile(userId: String, callback: (Map<String, Any>) -> Unit)



    fun addUserToDatabase( userId: String, model : UserModel,
        callback: (Boolean, String) -> Unit
    )


    fun  getUserFromDatabase(userId: String,
                             callback: (Boolean, String, UserModel?) -> Unit)



    fun logout(
        callback: (Boolean, String) -> Unit
    )



    fun deleteAcc(userId: String,
                  callback: (Boolean, String) -> Unit)


    fun getAllUsers( callback: (Boolean, String, List<UserModel>) -> Unit)


}