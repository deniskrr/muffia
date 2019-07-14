package com.deepster.mafiaparty.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.deepster.mafiaparty.shared.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUid = MutableLiveData<String?>()
    private val _currentUser = MutableLiveData<User>()

    private val _currentUserDatabaseReference = Transformations.map(_currentUid) { uid ->
        db.collection("users").document(uid)
    }

    val onUserLoggedOut: LiveData<Boolean> = Transformations.map(_currentUid) { uid ->
        null == uid
    }

    init {
        auth.addAuthStateListener { state ->
            // If the previous uid was valid, then remove the database listener associated with it
            if (null != _currentUid.value)
                _currentUserDatabaseReference.value!!.addSnapshotListener { _, _ -> }.remove()
            _currentUid.value = state.uid
            if (null != _currentUid.value) {
                // Add the new database listener
                _currentUserDatabaseReference.value!!.addSnapshotListener { userSnapshot, exception ->
                    _currentUser.value = userSnapshot?.toObject(User::class.java)
                }
            }
        }
    }


}
