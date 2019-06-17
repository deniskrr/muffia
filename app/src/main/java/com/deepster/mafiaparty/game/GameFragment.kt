package com.deepster.mafiaparty.game


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.entities.Game
import com.deepster.mafiaparty.model.entities.Period
import com.deepster.mafiaparty.model.entities.Role
import com.deepster.mafiaparty.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private lateinit var roomID: String
    private lateinit var currentUser: User
    private lateinit var role: Role

    private lateinit var db: FirebaseFirestore
    private val args: GameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = args.currentUser
        role = args.role
        roomID = args.roomID

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        db.collection("games").document(roomID).addSnapshotListener { snapshot, e ->

            if (snapshot != null) {
                val game = snapshot.toObject(Game::class.java)!!

                text_period.text = when (game.period) {
                    Period.NOT_STARTED -> resources.getString(R.string.not_started)
                    Period.DAY_ONE -> resources.getString(R.string.day_one)
                    Period.DAY_TWO -> resources.getString(R.string.day_two)
                    Period.DAY_THREE -> resources.getString(R.string.day_three)
                    Period.NIGHT_ONE -> resources.getString(R.string.night_one)
                    Period.NIGHT_TWO -> resources.getString(R.string.night_two)
                    Period.NIGHT_THREE -> resources.getString(R.string.night_there)
                }
            }
        }
    }
}
