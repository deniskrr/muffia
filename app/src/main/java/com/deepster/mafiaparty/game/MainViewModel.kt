package com.deepster.mafiaparty.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.deepster.mafiaparty.shared.Game
import com.deepster.mafiaparty.shared.Role
import com.deepster.mafiaparty.shared.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(application: Application) : AndroidViewModel(application) {

    enum class JoinMode {
        GAME, LOBBY
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _game = MutableLiveData<Game>()
    private val _currentUid = MutableLiveData<String?>()

    private val _currentUser = MutableLiveData<User>()

    val onNavigateToGame: LiveData<JoinMode> = Transformations.map(_game) { game ->
        if (game.period > 0) {
            JoinMode.GAME
        } else {
            JoinMode.LOBBY
        }
    }

    val roomID: LiveData<String> = Transformations.map(_game) { game ->
        game.roomID
    }

    val onUserLoggedOut: LiveData<Boolean> = Transformations.map(_currentUid) { uid ->
        null == uid
    }

    init {
        auth.addAuthStateListener { state ->
            val uid = _currentUid.value
            if (null != uid) {
                db.collection("users").document(uid).addSnapshotListener { _, _ -> }.remove()
            }
            _currentUid.value = state.uid
            val newUid = _currentUid.value
            if (null != newUid)
                db.collection("users").document(newUid).addSnapshotListener { userSnapshot, _ ->
                    if (userSnapshot != null)
                        _currentUser.value = userSnapshot.toObject(User::class.java)
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
            _game.value = newGame
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
                    db.collection("games").document(roomID).set(joinedGame).addOnSuccessListener {
                        _game.value = joinedGame
                    }
                }
            }
        }
    }
}
