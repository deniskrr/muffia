package com.deepster.mafiaparty.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.entities.Game
import com.deepster.mafiaparty.model.entities.Role
import com.deepster.mafiaparty.model.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {


    private lateinit var currentUser: User
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            // Navigate to Login if the user is not logged
            if (auth.currentUser == null) {
                val loginAction = MainFragmentDirections.actionMainFragmentToLoginFragment()
                findNavController().navigate(loginAction)
            }
        }


        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener { data ->
            if (data.exists()) {
                currentUser = data.toObject(User::class.java)!!
            }
        }


        button_create_lobby.setOnClickListener {
            // Generate a random 4 uppercase letter word as room id
            val roomID = (1..4)
                .map { i -> kotlin.random.Random.nextInt(65, 91).toChar() } // upper letter ascii
                .joinToString("")

            val newGame =
                Game(
                    roomID,
                    mutableMapOf(currentUser.username to Role.UNSELECTED)
                ) // Create game object
            db.collection("games").document(roomID).set(newGame) // Write the game to db
            val newGameAction = MainFragmentDirections.actionMainFragmentToLobbyFragment(roomID, currentUser)
            findNavController().navigate(newGameAction) // Move to the lobby fragment
        }

        button_join_lobby.setOnClickListener { button ->
            val roomID = textinput_room_id.editText!!.text.toString()

            db.collection("games").document(roomID).get().addOnSuccessListener { game ->
                // Get the joined game
                if (game.exists()) {
                    val joinedGame =
                        game.toObject(Game::class.java) // Convert the Firebase doc to Game class
                    joinedGame!!.players[currentUser.username] =
                        Role.UNSELECTED // Add the player joining the game
                    db.collection("games").document(roomID).set(joinedGame).addOnSuccessListener {
                        val joinAction = MainFragmentDirections.actionMainFragmentToLobbyFragment(roomID, currentUser)
                        button.findNavController().navigate(joinAction)
                    }
                }
            }
        }
    }
}

