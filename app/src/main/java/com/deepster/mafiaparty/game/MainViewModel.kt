package com.deepster.mafiaparty.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.deepster.mafiaparty.shared.Game
import com.deepster.mafiaparty.shared.Role
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

    fun createLobby() {
        // Generate a random 4 uppercase letter word as room id
        val roomID = (1..4)
            .map { i -> kotlin.random.Random.nextInt(65, 91).toChar() } // upper letter ascii
            .joinToString("")

        val newGame =
            Game(
                roomID = roomID,
                players = mutableMapOf(_currentUser.value!!.username to Role.OWNER)
            ) // Create game object
        db.collection("games").document(roomID).set(newGame).addOnSuccessListener {

        }
    }

    fun joinLobby(roomID: String) {
        db.collection("games").document(roomID).get().addOnSuccessListener { game ->
            // Get the joined game
            if (game.exists()) {
                val joinedGame =
                    game.toObject(Game::class.java) // Convert the Firebase doc to Game class

                val username = _currentUser.value!!.username

                // If the player is new to the lobby  - add him
                if (!joinedGame!!.players.containsKey(username)) {
                    joinedGame.players[username] =
                        Role.PLAYER // Add the player joining the game
                    db.collection("games").document(roomID).set(joinedGame)
                }
            }
        }
    }
}
