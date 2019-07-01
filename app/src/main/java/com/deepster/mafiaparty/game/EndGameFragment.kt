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
import com.deepster.mafiaparty.shared.GameStatus
import com.deepster.mafiaparty.shared.Role
import com.deepster.mafiaparty.shared.UserItemView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_end_game.*


class EndGameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        viewModel.game.observe(this, Observer { game ->
            val adapterWinning = GroupAdapter<ViewHolder>()
            val adapterLosing = GroupAdapter<ViewHolder>()
            recycler_winning.adapter = adapterWinning
            recycler_losing.adapter = adapterLosing
            recycler_winning.layoutManager = LinearLayoutManager(context)
            recycler_losing.layoutManager = LinearLayoutManager(context)


            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (game.status) {
                GameStatus.TOWN -> {
                    adapterWinning.addAll(
                        game.players.filter { player ->
                            player.value != Role.MAFIA
                        }.map { player -> UserItemView(player.key) }
                    )

                    adapterLosing.addAll(
                        game.players.filter { player ->
                            player.value == Role.MAFIA
                        }.map { player -> UserItemView(player.key) }
                    )
                }
                GameStatus.MAFIA -> {
                    adapterWinning.addAll(
                        game.players.filter { player ->
                            player.value == Role.MAFIA
                        }.map { player -> UserItemView(player.key) }
                    )

                    adapterLosing.addAll(
                        game.players.filter { player ->
                            player.value != Role.MAFIA
                        }.map { player -> UserItemView(player.key) }
                    )
                }
            }

        })


    }

}
