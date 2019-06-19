package com.deepster.mafiaparty.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deepster.mafiaparty.model.entities.Game
import com.deepster.mafiaparty.model.entities.Role
import com.deepster.mafiaparty.model.entities.User

class GameViewModel : ViewModel() {

    lateinit var currentUser: MutableLiveData<User>
    lateinit var game: MutableLiveData<Game>
    lateinit var role: MutableLiveData<Role>


}
