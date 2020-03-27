package com.daimler.mbmobilesdk.business

import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal interface ErrorMessageProvider {

    fun defaultErrorMessage(error: ResponseError<out RequestError>?): String

    fun <T : RequestError> defaultErrorMessage(
        error: ResponseError<out RequestError>?,
        onCustomError: (T) -> String
    ): String

    fun defaultErrorMessage(error: Throwable?): String

    fun generalError(): String

    fun generalErrorNetwork(): String
}