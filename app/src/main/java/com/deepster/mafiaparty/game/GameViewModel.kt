package com.deepster.mafiaparty.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deepster.mafiaparty.model.entities.Game
import com.deepster.mafiaparty.model.entities.Role
import com.deepster.mafiaparty.model.entities.User

class GameViewModel : ViewModel() {

    var currentUser: MutableLiveData<User> = MutableLiveData()
    var game: MutableLiveData<Game> = MutableLiveData()
    var role: MutableLiveData<Role> = MutableLiveData()


}
