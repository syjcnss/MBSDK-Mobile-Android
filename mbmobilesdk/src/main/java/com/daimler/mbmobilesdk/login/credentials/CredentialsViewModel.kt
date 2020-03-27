package com.daimler.mbmobilesdk.login.credentials

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.daimler.mbingresskit.common.LoginUser
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.business.StringProvider
import com.daimler.mbmobilesdk.business.UserInputErrorMessageProvider
import com.daimler.mbmobilesdk.utils.UserValidator
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveEvent
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveUnitEvent

internal class CredentialsViewModel(
    private val stringProvider: StringProvider,
    userId: String?,
    private val errorProvider: UserInputErrorMessageProvider,
    private val pinInteractor: PinInteractor
) : ViewModel() {

    val progressVisible = MutableLiveData(false)

    val currentUser = MutableLiveData(userId)

    val hasCredentials = Transformations.map(currentUser) { !it.isNullOrBlank() }

    internal val pinRequestStarted = MutableLiveEvent<String>()
    internal val navigateToPinVerification = MutableLiveEvent<UserLoginModel>()
    internal val pinRequestError = MutableLiveEvent<String>()
    internal val errorEvent = MutableLiveEvent<String>()
    internal val showMmeIdInfoEvent = MutableLiveUnitEvent()
    internal val userNotRegisteredEvent = MutableLiveEvent<UserLoginModel>()

    private val loading: Boolean
        get() = progressVisible.value == true

    fun onNextClicked() {
        if (!loading) {
            login(currentUser.value?.replace("\\s".toRegex(), ""))
        }
    }

    fun onMmeIdClicked() {
        showMmeIdInfoEvent.sendEvent()
    }

    private fun login(user: String?) {
        user?.takeIf {
            isInputValid(it)
        }?.let {
            pinRequestStarted(it)
        } ?: notifyInvalidInput()
    }

    fun continueLogin(user: String) {
        startPinRequest(user)
    }

    private fun notifyInvalidInput() {
        pinRequestError.sendEvent(stringProvider.getString(R.string.login_invalid_input))
    }

    private fun isInputValid(user: String?) =
        UserValidator.Mail.isValid(user) || UserValidator.Phone.isValid(user)

    private fun pinRequestStarted(user: String) {
        onLoading()
        pinRequestStarted.sendEvent(user)
    }

    private fun startPinRequest(user: String) {
        MBLoggerKit.d("Request pin for $user")
        pinInteractor.sendPin(user, object : PinInteractor.Callback {
            override fun onPinSent(user: LoginUser) {
                navigateToPinVerification.sendEvent(
                    UserLoginModel(
                        user.userName,
                        user.isMail
                    )
                )
            }

            override fun onRegistrationRequired(isMail: Boolean) {
                userNotRegisteredEvent.sendEvent(
                    UserLoginModel(
                        user,
                        isMail
                    )
                )
            }

            override fun onError(error: ResponseError<out RequestError>?) {
                pinRequestError.sendEvent(errorProvider.userInputErrorMessage(error))
            }

            override fun onFinish() {
                onLoadingFinished()
            }
        })
    }

    private fun onLoading() {
        progressVisible.postValue(true)
    }

    private fun onLoadingFinished() {
        progressVisible.postValue(false)
    }
}