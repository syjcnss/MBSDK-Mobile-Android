package com.daimler.mbmobilesdk.ui.utils.extensions

import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import android.widget.TextView

/**
 * Sets the font family/ font for the given id as [Typeface].
 *
 * @param resId the id to the font/ font family
 * @param keepStyle true to keep the typeface style of the previous set typeface
 */
internal fun TextView.setTypefaceFromRes(@FontRes resId: Int, keepStyle: Boolean = true) {
    val tf = ResourcesCompat.getFont(context, resId)
    val style = if (keepStyle) typeface?.style ?: Typeface.NORMAL else Typeface.NORMAL
    setTypeface(tf, style)
}