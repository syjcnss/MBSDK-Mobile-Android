package com.daimler.mbmobilesdk.ui.utils.extensions

import android.content.res.TypedArray
import androidx.annotation.ColorInt

/**
 * Returns the color behind the given index or [default] if there is no color for this index
 * or if this TypedArray is null.
 */
@ColorInt fun TypedArray?.getColor(index: Int, @ColorInt default: Int): Int =
        this?.getColor(index, default) ?: default

/**
 * Returns the dimension pixel size behind the given index or [default] if there is no
 * dimension pixel size for this index or if this TypedArray is null.
 */
fun TypedArray?.getDimensionPixelSize(index: Int, default: Int): Int =
        this?.getDimensionPixelSize(index, default) ?: default

/**
 * Returns the Boolean behind the given index or [default] if there is no Boolean for this index
 * or if this TypedArray is null.
 */
fun TypedArray?.getBoolean(index: Int, default: Boolean): Boolean =
        this?.getBoolean(index, default) ?: default

/**
 * Returns the Integer behind the given index or [default] if there is no Integer for this index
 * or if this TypedArray is null.
 */
fun TypedArray?.getInt(index: Int, default: Int): Int =
        this?.getInt(index, default) ?: default