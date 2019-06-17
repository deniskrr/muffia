package com.deepster.mafiaparty.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize




enum class Period {
    NOT_STARTED, DAY_ONE, DAY_TWO, DAY_THREE, NIGHT_ONE, NIGHT_TWO, NIGHT_THREE;


}

@Parcelize
data class Game(
    val roomID: String = "",
    val players: MutableMap<String, Role> = mutableMapOf(),
    var period: Period = Period.NOT_STARTED
) :
    Parcelable