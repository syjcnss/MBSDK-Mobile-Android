package com.daimler.mbmobilesdk.login.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daimler.mbingresskit.common.LoginUser
import com.daimler.mbingresskit.common.User
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.business.ErrorMessageProvider
import com.daimler.mbmobilesdk.business.StringProvider
import com.daimler.mbmobilesdk.login.credentials.UserLoginModel
import com.daimler.mbmobilesdk.login.credentials.PinInteractor
import com.daimler.mbmobilesdk.utils.extensions.re
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveEvent
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveUnitEvent
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal class PinVerificationViewModel(
    private val stringProvider: StringProvider,
    private val userLoginModel: UserLoginModel,
    private val errorProvider: ErrorMessageProvider,
    private val pinInteractor: PinInteractor,
    private val loginInteractor: LoginInteractor
) : ViewModel() {

    val isMail = userLoginModel.isMail
    val validationProgressVisible = MutableLiveData(false)
    val tanProgressVisible = MutableLiveData(false)
    val processing = MutableLiveData(false)
    val onPinError = MutableLiveEvent<String>()
    val onPinVerified = MutableLiveEvent<PinVerificationEvent>()
    val onPinRequested = MutableLiveUnitEvent()
    val onPinRequestError = MutableLiveEvent<String>()
    val userName = userLoginModel.user

    private val pin = MutableLiveData<String>()

    val pinInputReady: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(pin) {
            isPinValid(it) && processing.value == false
        }
        addSource(processing) {
            isPinValid(pin.value) && it.not()
        }
    }

    fun onPinInputChanged(newPin: String) {
        // do note post here or onContinueClicked will be called before the new value is available
        pin.value = newPin
    }

    fun onContinueClicked() {
        pin.value?.takeIf {
            isPinValid(it)
        }?.let {
            MBLoggerKit.d("Valid pin: $it.")
            startLogin(it)
        } ?: MBLoggerKit.e("Invalid pin: ${pin.value}")
    }

    fun onRetryClicked() {
        MBLoggerKit.d("Request pin for $userLoginModel")
        onSendNewTan()
        pinInteractor.sendPin(userLoginModel.user, object : PinInteractor.Callback {
            override fun onPinSent(user: LoginUser) {
                notifyPinRequestRetried()
            }

            override fun onRegistrationRequired(isMail: Boolean) {
                MBLoggerKit.e("Received registration required on pin retry.")
            }

            override fun onError(error: ResponseError<out RequestError>?) {
                MBLoggerKit.re("Error while trying to request a pin.", error)
                handlePinRequestRetryError(error)
            }

            override fun onFinish() {
                onSendNewTanFinished()
            }
        })
    }

    /**
     * Should only be called if pin was already check for validity.
     */
    private fun startLogin(pin: String) {
        onLogin()
        loginInteractor.loginWithCredentials(
            userLoginModel.user,
            pin,
            object : LoginInteractor.Callback {
                override fun onLoginSuccess(user: User?) {
                    MBLoggerKit.d("Login for $userLoginModel success")
                    handlePinVerified()
                }

                override fun onTokenExchangeFailed() {
                    handleLoginError(stringProvider.getString(R.string.general_error_msg))
                }

                override fun onWrongCredentials() {
                    handleLoginError(stringProvider.getString(R.string.login_error_wrong_tan))
                }

                override fun onAuthorizationFailed() {
                    handleLoginError(stringProvider.getString(R.string.login_error_authentication_failed))
                }

                override fun onUnknownError(error: ResponseError<out RequestError>?) {
                    handleLoginError(errorProvider.defaultErrorMessage(error))
                }

                override fun onFinish() {
                    onLoginFinished()
                }
            })
    }

    private fun handlePinVerified() {
        notifyPinVerified()
    }

    private fun handleLoginError(error: String) {
        MBLoggerKit.e("Login for $userLoginModel failed: $error")
        notifyPinError(error)
    }

    private fun handlePinRequestRetryError(error: ResponseError<out RequestError>?) {
        notifyPinRequestRetryError(errorProvider.defaultErrorMessage(error))
    }

    private fun isPinValid(pin: String?): Boolean =
        pin?.length == PIN_DIGITS

    private fun notifyPinVerified() {
        onPinVerified.sendEvent(PinVerificationEvent(userLoginModel))
    }

    private fun notifyPinError(error: String) {
        onPinError.sendEvent(error)
    }

    private fun notifyPinRequestRetried() {
        onPinRequested.sendEvent()
    }

    private fun notifyPinRequestRetryError(error: String) {
        onPinRequestError.sendEvent(error)
    }

    private fun onLogin() {
        validationProgressVisible.postValue(true)
        onLoading()
    }

    private fun onLoginFinished() {
        validationProgressVisible.postValue(false)
        onLoadingFinished()
    }

    private fun onSendNewTan() {
        tanProgressVisible.postValue(true)
        onLoading()
    }

    private fun onSendNewTanFinished() {
        tanProgressVisible.postValue(false)
        onLoadingFinished()
    }

    private fun onLoading() {
        processing.postValue(true)
    }

    private fun onLoadingFinished() {
        processing.postValue(false)
    }

    internal data class PinVerificationEvent(val userLoginModel: UserLoginModel)

    companion object {
        const val PIN_DIGITS = 6
    }
}