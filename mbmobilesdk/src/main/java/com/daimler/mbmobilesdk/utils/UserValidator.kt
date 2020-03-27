package com.daimler.mbmobilesdk.utils

import java.util.regex.Pattern

internal sealed class UserValidator(private val validator: Validator) {

    fun isValid(value: String?) = validator(value)

    object Mail : UserValidator({ validateWithPattern(PATTERN_MAIL, it?.trim()) })
    object Phone : UserValidator({ validateWithPattern(PATTERN_PHONE, it?.trim()) })

    companion object {

        internal const val USER_PIN_DIGITS = 4
        private val PATTERN_MAIL = "[A-Z0-9a-z._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}".toPattern()
        private val PATTERN_PHONE = "\\+*[0-9]{7,}".toPattern()

        private fun validateWithPattern(pattern: Pattern, value: String?): Boolean =
            !value.isNullOrBlank() && pattern.matcher(value).matches()
    }
}

typealias Validator = (String?) -> Boolean