package com.daimler.mbmobilesdk.business

import com.daimler.mbingresskit.common.UserInputErrors
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError

internal interface UserInputErrorMessageProvider : ErrorMessageProvider {

    /**
     * Tries to parse the [error] as [UserInputErrors] and returns the description of the first
     * error element (if the description is not null and not empty). Otherwise it returns the
     * standardized error message of [defaultErrorMessage].
     */
    fun userInputErrorMessage(error: ResponseError<out RequestError>?): String
}