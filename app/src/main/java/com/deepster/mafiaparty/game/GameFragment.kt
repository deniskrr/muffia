package com.deepster.mafiaparty.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.shared.Game
import com.deepster.mafiaparty.shared.Role
import com.deepster.mafiaparty.shared.UserItemView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_game.*

@Suppress("NON_EXHAUSTIVE_WHEN")
class GameFragment : Fragment() {

    private lateinit var functions: FirebaseFunctions
    private lateinit var viewModel: GameViewModel
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        db = FirebaseFirestore.getInstance()
        functions = FirebaseFunctions.getInstance()

        val currentUser = viewModel.currentUser.value!!

        val adapter = GroupAdapter<ViewHolder>()
        adapter.setOnItemClickListener { item, _ ->
            if (item is UserItemView) {
                button_vote.text = item.user
            }
        }
        recycler_players.adapter = adapter
        recycler_players.layoutManager = LinearLayoutManager(context)

        button_vote.setOnClickListener {
            val game = viewModel.game.value!!
            val role = viewModel.role.value!!
            val voteString = "${button_vote.text},${role}"
            game.votes[game.period - 1][currentUser.username] = voteString
            db.collection("games").document(game.roomID).set(game).addOnSuccessListener {
                functions.getHttpsCallable("newPeriod")
                    .call(game.roomID)
            }
        }

        viewModel.game.observe(this, Observer { game ->
            viewModel.role.value = game.alivePlayers[currentUser.username]

            //todo Add resource strings
            val periodString = (if (game.period % 2 == 1) "Night " else "Day ") + (game.period / 2)
            text_period.text = periodString
            if (game.period % 2 == 1) { // Night time
                //todo Make background dark
                when (viewModel.role.value) {
                    Role.MAFIA -> {
                        text_role_help.text = resources.getString(R.string.mafia_help)
                        recycler_players.visibility = View.VISIBLE
                        button_vote.visibility = View.VISIBLE
                    }
                    Role.COP -> {
                        text_role_help.text = resources.getString(R.string.cop_help)
                        recycler_players.visibility = View.VISIBLE
                        button_vote.visibility = View.VISIBLE
                    }
                    Role.DOCTOR -> {
                        text_role_help.text = resources.getString(R.string.doctor_help)
                        recycler_players.visibility = View.VISIBLE
                        button_vote.visibility = View.VISIBLE
                    }
                    Role.CITIZEN -> {
                        text_role_help.text = resources.getString(R.string.citizen_help)
                        recycler_players.visibility = View.GONE
                        button_vote.visibility = View.GONE
                    }
                }
            } else { // Day time
                //todo Make background light

            }
            adapter.clear()
            adapter.addAll(game.alivePlayers.keys.map { player -> UserItemView(player) })
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
