package com.deepster.mafiaparty.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Role : Parcelable {
    OWNER, PLAYER, MAFIA, CITIZEN, COP, DOCTOR
}


@Parcelize
data class User(val email: String = "", val username: String = "", val uid: String = "  ") : Parcelable