package com.daimler.mbmobilesdk.ui.widgets.textviews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.daimler.mbmobilesdk.R

internal class MBSubtitle1TextView : TextView {

    constructor(context: Context) : super(context, null, 0, R.style.MBTextAppearance_Subtitle1)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs, 0, R.style.MBTextAppearance_Subtitle1)
}