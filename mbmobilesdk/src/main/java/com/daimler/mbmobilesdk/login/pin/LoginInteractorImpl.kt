package com.daimler.mbmobilesdk.login.pin

import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbingresskit.common.UserCredentials
import com.daimler.mbingresskit.login.LoginFailure
import com.daimler.mbingresskit.login.UserService
import com.daimler.mbmobilesdk.utils.extensions.getRequestError
import com.daimler.mbnetworkkit.common.TokenProvider
import com.daimler.mbnetworkkit.common.TokenProviderCallback
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal class LoginInteractorImpl(
    private val ingressKit: MBIngressKit,
    private val tokenProvider: TokenProvider,
    private val userService: UserService
) : LoginInteractor {

    override fun loginWithCredentials(
        user: String,
        pin: String,
        callback: LoginInteractor.Callback
    ) {
        ingressKit.loginWithCredentials(UserCredentials(user, pin))
            .onComplete {
                fetchUserAndNotifySuccess(callback)
            }.onFailure { loginError ->
                loginError?.getRequestError<LoginFailure>()?.let {
                    when (it) {
                        LoginFailure.UNABLE_TO_EXCHANGE_TOKEN -> callback.onTokenExchangeFailed()
                        LoginFailure.WRONG_CREDENTIALS -> callback.onWrongCredentials()
                        LoginFailure.AUTHORIZATION_FAILED -> callback.onAuthorizationFailed()
                        else -> callback.onUnknownError(loginError)
                    }
                } ?: callback.onUnknownError(loginError)
            }.onAlways { _, _, _ ->
                callback.onFinish()
            }
    }

    private fun fetchUserAndNotifySuccess(callback: LoginInteractor.Callback) {
        tokenProvider.requestToken(object : TokenProviderCallback {

            override fun onTokenReceived(token: String) {
                userService.loadUser(token)
                    .onComplete {
                        callback.onLoginSuccess(it)
                    }.onFailure {
                        callback.onLoginSuccess(null)
                    }
            }

            override fun onRequestFailed(error: ResponseError<out RequestError>?) {
                callback.onLoginSuccess(null)
            }
        })
    }
}
