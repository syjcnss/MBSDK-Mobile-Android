package com.daimler.mbmobilesdk.login.credentials

import com.daimler.mbingresskit.login.UserService
import com.daimler.mbloggerkit.MBLoggerKit
import java.util.*

internal class PinInteractorImpl(
    private val userService: UserService
) : PinInteractor {

    override fun sendPin(user: String, callback: PinInteractor.Callback) {
        val locale = Locale.getDefault()
        userService.sendPin(user, locale.country.toUpperCase(locale))
            .onComplete {
                if (isLogin(it)) {
                    callback.onPinSent(it)
                } else {
                    callback.onRegistrationRequired(it.isMail)
                }
            }.onFailure {
                MBLoggerKit.e("Sendpin request failed.")
                callback.onError(it)
            }.onAlways { _, _, _ ->
                callback.onFinish()
            }
    }

    private fun isLogin(user: com.daimler.mbingresskit.common.LoginUser): Boolean {
        return user.userName.isNotEmpty()
    }
}