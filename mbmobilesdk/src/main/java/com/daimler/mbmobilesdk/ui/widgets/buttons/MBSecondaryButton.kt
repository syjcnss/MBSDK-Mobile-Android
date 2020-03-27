package com.daimler.mbmobilesdk.ui.widgets.buttons

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.daimler.mbmobilesdk.R

/**
 * Secondary button widget with rounded corners.
 *
 * @see R.style.MBSecondaryButtonStyle
 */
internal open class MBSecondaryButton : Button {

    constructor(context: Context) : super(context, null, 0, R.style.MBSecondaryButtonStyle)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs, 0, R.style.MBSecondaryButtonStyle)
}