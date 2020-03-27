package com.daimler.mbmobilesdk.login.credentials

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLoginModel(
    val user: String,
    val isMail: Boolean
) : Parcelable