package com.deepster.mafiaparty.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class GameStatus : Parcelable {
    PLAYING, TOWN, MAFIA
}

@Parcelize
data class Game(
    val roomID: String = "",
    val status: GameStatus = GameStatus.PLAYING,
    val copCount: Int = 1,
    val citizenCount: Int = 3,
    val doctorCount: Int = 1,
    val mafiaCount: Int = 2,
    var period: Int = 0,
    val players: MutableMap<String, Role> = mutableMapOf(),
    val alivePlayers: MutableList<String> = mutableListOf(),
    val deadPlayers: MutableList<String> = mutableListOf(),
    val investigatedPlayers: MutableMap<String, Role> = mutableMapOf(),
    var votes: MutableList<MutableMap<String, String>> = mutableListOf(mutableMapOf())
) :
    Parcelable