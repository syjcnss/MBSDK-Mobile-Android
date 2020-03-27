package com.daimler.mbmobilesdk.ui.widgets.textviews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.ui.utils.extensions.setTypefaceFromRes

internal class MBHeadline4SerifTextView : TextView {

    constructor(context: Context) : super(context, null, 0, R.style.MBTextAppearance_H4)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs, 0, R.style.MBTextAppearance_H4)

    init {
        setTypefaceFromRes(R.font.mb_font_serif)
    }
}
