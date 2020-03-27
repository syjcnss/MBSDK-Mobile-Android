package com.daimler.mbmobilesdk.utils.extensions

import java.util.*

fun Locale.format() = formatWithSeparator("-")

fun Locale.formatWithSeparator(separator: String) = "$language$separator$country"