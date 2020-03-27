package com.daimler.mbmobilesdk.implementation

import com.daimler.mbingresskit.common.UserInputErrors
import com.daimler.mbmobilesdk.business.ErrorMessageProvider
import com.daimler.mbmobilesdk.business.UserInputErrorMessageProvider
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal class UserInputErrorMessageProviderImpl(
    private val errorMessageProvider: ErrorMessageProvider
) : UserInputErrorMessageProvider {

    override fun userInputErrorMessage(error: ResponseError<out RequestError>?): String =
        defaultErrorMessage<UserInputErrors>(error) { errors ->
            errors.errors.find { !it.description.isNullOrBlank() }?.description ?: generalError()
        }

    override fun defaultErrorMessage(error: ResponseError<out RequestError>?): String =
        errorMessageProvider.defaultErrorMessage(error)

    override fun <T : RequestError> defaultErrorMessage(error: ResponseError<out RequestError>?, onCustomError: (T) -> String): String =
        errorMessageProvider.defaultErrorMessage(error, onCustomError)

    override fun defaultErrorMessage(error: Throwable?): String =
        errorMessageProvider.defaultErrorMessage(error)

    override fun generalError(): String =
        errorMessageProvider.generalError()

    override fun generalErrorNetwork(): String =
        errorMessageProvider.generalErrorNetwork()
}