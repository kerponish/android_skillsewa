package com.example.kotlinsample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinsample.model.UserModel
import com.example.kotlinsample.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(private val repo: UserRepository) : ViewModel() {

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun editProfile(userId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.editProfile(userId, data, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }
     private val _users = MutableLiveData<UserModel?>()
    val users : LiveData<UserModel?> get()= _users

    fun getUserFromDatabase(userId: String, callback: (Boolean, String, UserModel?) -> Unit) {
        repo.getUserFromDatabase(userId){
            success,message,users->
            if(success){
                _users.postValue(users)
            }
            else{
                _users.postValue(null)
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun deleteAcc(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAcc(userId, callback)
    }

    fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        repo.getAllUsers(callback)
    }

}
