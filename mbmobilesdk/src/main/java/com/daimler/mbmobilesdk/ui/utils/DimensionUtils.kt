package com.daimler.mbmobilesdk.ui.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 * Returns the px value for the given dp amount.
 */
fun dpToPx(resources: Resources, dp: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                resources.displayMetrics).toInt()

/**
 * Returns the px value for the given sp amount.
 */
fun spToPx(resources: Resources, sp: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(),
                resources.displayMetrics).toInt()