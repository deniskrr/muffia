package com.deepster.mafiaparty.game


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.entities.Game
import com.deepster.mafiaparty.model.entities.Period
import com.deepster.mafiaparty.model.itemview.UserItemView
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_lobby.*

class LobbyFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var db: FirebaseFirestore

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

        val adapter = GroupAdapter<ViewHolder>()
        recycler_players.adapter = adapter
        recycler_players.layoutManager = LinearLayoutManager(context)
        db = FirebaseFirestore.getInstance()

        val roomID = viewModel.roomID.value!!

        db.collection("games").document(roomID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject(Game::class.java)!!
                val currentUser = viewModel.currentUser.value!!
                viewModel.role.value = game.players[currentUser.username]

                button_start_game.isEnabled = game.players.size == 7 // If enough players joined, enable start button

                if (game.period == Period.NIGHT_ONE) { // If roles were distributed
                    val role = game.players[currentUser.username]!!
                    viewModel.role.value = role
                    val gameAction = LobbyFragmentDirections.actionLobbyFragmentToGameFragment()
                    findNavController().navigate(gameAction)
                }
                adapter.clear()
                adapter.addAll(game.players.keys.map { playerName ->
                    UserItemView(playerName)
                })
            }
        }
    }
}
