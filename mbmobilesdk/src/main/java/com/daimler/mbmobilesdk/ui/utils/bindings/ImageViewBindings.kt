package com.daimler.mbmobilesdk.ui.utils.bindings

import androidx.databinding.BindingAdapter
import androidx.annotation.DrawableRes
import android.widget.ImageView

/**
 * Sets the drawable of the given resource to the given ImageView.
 */
@BindingAdapter("drawableRes")
internal fun setDrawableFromRes(imageView: ImageView, @DrawableRes drawableRes: Int) {
    imageView.setImageResource(drawableRes)
}