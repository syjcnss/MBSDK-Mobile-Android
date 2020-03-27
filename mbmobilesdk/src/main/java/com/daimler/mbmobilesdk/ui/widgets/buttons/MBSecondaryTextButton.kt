package com.daimler.mbmobilesdk.ui.widgets.buttons

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.daimler.mbmobilesdk.R

/**
 * Text button widget with no background.
 *
 * @see R.style.MBTextButtonStyle
 */
internal open class MBSecondaryTextButton : Button {

    constructor(context: Context) : super(context, null, 0, R.style.MBSecondaryTextButtonStyle)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs, 0, R.style.MBSecondaryTextButtonStyle)
}