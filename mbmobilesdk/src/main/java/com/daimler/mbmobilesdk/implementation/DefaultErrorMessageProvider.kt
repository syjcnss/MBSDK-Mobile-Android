package com.daimler.mbmobilesdk.implementation

import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.business.ErrorMessageProvider
import com.daimler.mbmobilesdk.business.StringProvider
import com.daimler.mbnetworkkit.networking.HttpError
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal open class DefaultErrorMessageProvider(
    private val stringProvider: StringProvider
) : ErrorMessageProvider {

    override fun defaultErrorMessage(error: ResponseError<out RequestError>?): String =
        defaultErrorMessage<RequestError>(error) { generalError() }

    override fun <T : RequestError> defaultErrorMessage(
        error: ResponseError<out RequestError>?,
        onCustomError: (T) -> String
    ): String =
        error?.let {
            when {
                it.networkError != null -> generalErrorNetwork()
                it.requestError == null -> generalError()
                it.requestError is HttpError -> {
                    val httpError = it.requestError as? HttpError
                    httpError?.description?.message ?: generalError()
                }
                else -> it.getRequestError<T>()?.let(onCustomError) ?: generalError()
            }
        } ?: generalError()

    override fun defaultErrorMessage(error: Throwable?): String = generalError()

    override fun generalError(): String = stringProvider.getString(R.string.general_error_msg)

    override fun generalErrorNetwork(): String = stringProvider.getString(R.string.general_error_network_msg)

    @Suppress("UNCHECKED_CAST")
    private fun <T : RequestError> ResponseError<*>.getRequestError(): T? =
        requestError as? T
}