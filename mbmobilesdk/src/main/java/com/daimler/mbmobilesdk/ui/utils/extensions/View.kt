package com.daimler.mbmobilesdk.ui.utils.extensions

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.view.View
import com.daimler.mbmobilesdk.ui.utils.AlphaElevationOutlineProvider

/**
 * Convenient function for [ContextCompat.getColor].
 */
internal fun View.findColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

internal fun View.setDefaultAlphaOutlineProvider() {
    outlineProvider = AlphaElevationOutlineProvider()
}