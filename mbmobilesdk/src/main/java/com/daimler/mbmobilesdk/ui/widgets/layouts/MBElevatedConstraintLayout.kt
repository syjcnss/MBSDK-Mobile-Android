package com.daimler.mbmobilesdk.ui.widgets.layouts

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.ui.utils.extensions.setDefaultAlphaOutlineProvider

/**
 * [ConstraintLayout] with elevation.
 *
 * @see R.style.MBElevationLayoutStyle
 */
internal open class MBElevatedConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, R.style.MBElevationLayoutStyle) {

    init {
        setDefaultAlphaOutlineProvider()
    }
}