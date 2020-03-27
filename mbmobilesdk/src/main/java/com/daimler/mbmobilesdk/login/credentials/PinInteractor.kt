package com.daimler.mbmobilesdk.login.credentials

import com.daimler.mbingresskit.common.LoginUser
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal interface PinInteractor {

    fun sendPin(user: String, callback: Callback)

    interface Callback {
        fun onPinSent(user: LoginUser)
        fun onRegistrationRequired(isMail: Boolean)
        fun onError(error: ResponseError<out RequestError>?)
        fun onFinish()
    }
}