package com.deepster.mafiaparty.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Game(
    val roomID: String = "",
    val players: MutableMap<String, Role> = mutableMapOf(),
    var period: Int = 0,
    var votes: MutableList<MutableMap<String, String>> = mutableListOf(mutableMapOf())
) :
    Parcelable