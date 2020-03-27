package com.daimler.mbmobilesdk.login

import com.daimler.mbmobilesdk.login.credentials.UserLoginModel

internal interface MBBaseLoginCallback {

    fun hideToolbar()

    fun setToolbarTitle(title: String)

    fun onLoginStarted()

    fun onLoginSuccess(dataLoginModel: UserLoginModel)

    fun onLoginError(error: String)

    fun onUserNotRegistered(dataLoginModel: UserLoginModel)

    fun onShowPinVerification(dataLoginModel: UserLoginModel)
}