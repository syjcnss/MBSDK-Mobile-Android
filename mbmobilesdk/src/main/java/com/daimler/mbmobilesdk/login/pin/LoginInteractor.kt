package com.daimler.mbmobilesdk.login.pin

import com.daimler.mbingresskit.common.User
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal interface LoginInteractor {

    fun loginWithCredentials(
        user: String,
        pin: String,
        callback: Callback
    )

    interface Callback {
        fun onLoginSuccess(user: User?)
        fun onTokenExchangeFailed()
        fun onWrongCredentials()
        fun onAuthorizationFailed()
        fun onUnknownError(error: ResponseError<out RequestError>?)
        fun onFinish() // always called
    }
}