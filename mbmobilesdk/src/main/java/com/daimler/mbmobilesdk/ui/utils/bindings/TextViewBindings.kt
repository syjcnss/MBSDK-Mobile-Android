package com.daimler.mbmobilesdk.ui.utils.bindings

import androidx.databinding.BindingAdapter
import android.graphics.Typeface
import androidx.annotation.StringRes
import android.widget.TextView
import com.daimler.mbloggerkit.MBLoggerKit

/**
 * Sets the text from a given resource id.
 */
@BindingAdapter("textRes")
internal fun setTextFromRes(view: TextView, @StringRes resId: Int) = view.setText(resId)

@BindingAdapter("textStyle")
internal fun setTextStyle(view: TextView, style: String) {
    val styleInt: Int =
            when (style) {
                "bold" -> Typeface.BOLD
                "italic" -> Typeface.ITALIC
                "bold|italic" -> Typeface.BOLD_ITALIC
                "normal" -> Typeface.NORMAL
                else -> {
                    MBLoggerKit.e("unsupported style $style")
                    Typeface.NORMAL
                }
            }
    val tf = view.typeface
    view.typeface = Typeface.create(tf, styleInt)
}