package com.deepster.mafiaparty.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Game(
    val roomID: String = "",
    val copCount: Int = 1,
    val citizenCount: Int = 3,
    val doctorCount: Int = 1,
    val mafiaCount: Int = 2,
    var period: Int = 0,
    val alivePlayers: MutableMap<String, Role> = mutableMapOf(),
    val deadPlayers: MutableList<String> = mutableListOf(),
    val investigatedPlayers: MutableMap<String, Role> = mutableMapOf(),
    var votes: MutableList<MutableMap<String, String>> = mutableListOf(mutableMapOf())
) :
    Parcelable