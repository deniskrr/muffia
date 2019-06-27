package com.deepster.mafiaparty.game


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.shared.Game
import com.deepster.mafiaparty.shared.Role
import com.deepster.mafiaparty.shared.UserItemView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_lobby.*

class LobbyFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var functions: FirebaseFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        val currentUser = viewModel.currentUser.value!!

        val adapter = GroupAdapter<ViewHolder>()
        recycler_players.adapter = adapter
        recycler_players.layoutManager = LinearLayoutManager(context)
        db = FirebaseFirestore.getInstance()
        functions = FirebaseFunctions.getInstance()

        button_start_game.setOnClickListener { button ->
            button.isEnabled = false // Disable the start button while the cloud function is running
            val game = viewModel.game.value
            functions.getHttpsCallable("startGame")
                .call(game!!.roomID).addOnCompleteListener {
                    button.isEnabled = true
                }
        }

        viewModel.game.observe(this, Observer { game ->
            // Set the player's role
            viewModel.role.value = game.alivePlayers[currentUser.username]

            text_room_id.text = game.roomID

            // Enable start button if enough alivePlayers joined
            if (viewModel.role.value == Role.OWNER) {
                button_start_game.isEnabled = game.alivePlayers.size == 7
            } else {
                //todo Show something else for normals alivePlayers
            }

            if (game.period == 1) {
                val gameAction = LobbyFragmentDirections.actionLobbyFragmentToGameFragment()
                findNavController().navigate(gameAction)
            }

            // Update UI player list
            adapter.clear()
            adapter.addAll(game.alivePlayers.keys.map { player ->
                UserItemView(player)
            })
        })

        val roomID = viewModel.game.value!!.roomID

        // Update game object
        db.collection("games").document(roomID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject(Game::class.java)!!
                viewModel.game.value = game
            }
        }
    }
}
