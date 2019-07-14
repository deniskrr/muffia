package com.deepster.mafiaparty.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.deepster.mafiaparty.shared.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    companion object Credentials {
        var email: String = ""
        var password: String = ""
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableLiveData<String?>()

    val onUserLoggedIn: LiveData<Boolean> = Transformations.map(_currentUser) { user ->
        null != user
    }

    init {
        auth.addAuthStateListener { state ->
            _currentUser.value = state.uid
        }
    }

    fun registerUser(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authSignUpTask ->
                if (authSignUpTask.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val user = User(email = email, uid = uid)

                    db.collection("users").document(uid).set(user).addOnCompleteListener { dbSignUpTask ->
                        if (dbSignUpTask.isSuccessful) {
                            _currentUser.value = email
                        }
                    }
                }
            }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            }
        }
    }


}