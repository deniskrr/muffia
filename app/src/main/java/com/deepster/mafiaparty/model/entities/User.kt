package com.deepster.mafiaparty.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val email: String = "", val username: String = "", val uid: String = "  ") : Parcelable