package com.example.ambuxproject.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Authentication(application: Application) {
    private var application : Application
    private var firebaseMutableLiveData: MutableLiveData<FirebaseUser>
    private var auth: FirebaseAuth
    private var firebaseUserLoggedInState: MutableLiveData<Boolean>


    fun getFirebaseUserMutableLiveData (): MutableLiveData<FirebaseUser>{
        return firebaseMutableLiveData
    }

    fun getFirebaseUserLoggedInState(): MutableLiveData<Boolean>{
        return firebaseUserLoggedInState
    }

    init {
        this.application = application
        firebaseMutableLiveData = MutableLiveData<FirebaseUser>()
        firebaseUserLoggedInState = MutableLiveData<Boolean>()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            firebaseMutableLiveData.postValue(auth.currentUser)
        }
    }

    fun registerUser(email: String , password : String){
          auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
              if(it.isSuccessful){
               firebaseMutableLiveData.postValue(auth.currentUser)
              }else{
                  Toast.makeText(application,it.exception?.message,Toast.LENGTH_SHORT).show()
              }
          }
    }

    fun loginUser(email : String , password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
            firebaseMutableLiveData.postValue(auth.currentUser)
            }else{
                Toast.makeText(application,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signOut(){
        auth.signOut()
        firebaseUserLoggedInState.postValue(true)
    }

}