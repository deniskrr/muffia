package com.deepster.mafiaparty.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Game(
    val roomID: String = "",
    val alivePlayers: MutableMap<String, Role> = mutableMapOf(),
    var period: Int = 0,
    var votes: MutableList<MutableMap<String, String>> = mutableListOf(mutableMapOf()),
    val deadPlayers: MutableList<String> = mutableListOf(),
    val investigatedPlayers: MutableMap<String, Role> = mutableMapOf()
) :
    Parcelable