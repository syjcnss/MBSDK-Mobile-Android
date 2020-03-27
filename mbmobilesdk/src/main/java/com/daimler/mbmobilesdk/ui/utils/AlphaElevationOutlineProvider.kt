package com.daimler.mbmobilesdk.ui.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange

/**
 * [ViewOutlineProvider] that uses the outline of the view's background and sets the alpha value
 * of the outline to the given [alpha] value.
 */
internal class AlphaElevationOutlineProvider(
    @FloatRange(from = 0.0, to = 1.0) private val alpha: Float = DEFAULT_OUTLINE_ALPHA
) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        view.background?.getOutline(outline)
        outline.alpha = alpha
    }

    companion object {
        const val DEFAULT_OUTLINE_ALPHA = 0.4f
    }
}