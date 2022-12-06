package com.example.ambuxproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ambuxproject.repository.Authentication
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private var repository : Authentication
    private var userData: MutableLiveData<FirebaseUser>
    private var loggedStatus : MutableLiveData<Boolean>
    private var currentUserId : String?

     fun getUserData(): MutableLiveData<FirebaseUser>{
        return userData
    }

    fun getLoggedStatus(): MutableLiveData<Boolean>{
        return loggedStatus
    }

    fun getCurrentUserId(): String? {
        return currentUserId
    }

    init{

        repository = Authentication(application)
        userData = repository.getFirebaseUserMutableLiveData()
        loggedStatus = repository.getFirebaseUserLoggedInState()
        currentUserId = repository.getCurrentUserId()
    }

    fun registerCustomer(email : String , password : String){
        repository.registerCustomer(email, password)
    }

    fun registerDriver(email: String , password: String){
        repository.registerDriver(email,password)
    }

    fun signIn(email: String,password: String){
        repository.loginUser(email, password)
    }

    fun signOut(){
         repository.signOut()
    }



}