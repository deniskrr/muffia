package com.deepster.mafiaparty.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.Game
import com.deepster.mafiaparty.model.Role
import com.deepster.mafiaparty.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: GameViewModel

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
        if (auth.currentUser == null) {
            val loginAction = MainFragmentDirections.actionMainFragmentToLoginFragment()
            findNavController().navigate(loginAction)
        }

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        button_create_lobby.setOnClickListener {

            // Generate a random 4 uppercase letter word as room id
            val roomID = (1..4)
                .map { i -> kotlin.random.Random.nextInt(65, 91) } // upper letter ascii
                .map { i -> i.toChar() }
                .joinToString("")



            db.collection("users").document(auth.currentUser!!.uid) // Get the owner of the game
                .get().addOnSuccessListener { data ->
                    if (data.exists()) {
                        val currentUser = data.toObject(User::class.java)!!

                        val newGame =
                            Game(roomID, mutableMapOf(currentUser.username to Role.UNSELECTED)) // Create game object

                        db.collection("games").document(roomID).set(newGame) // Write the game to db
                        val newGameAction = MainFragmentDirections.actionMainFragmentToLobbyFragment(roomID)
                        findNavController().navigate(newGameAction) // Move to the lobby fragment
                    }
                }

        }
    }

}
