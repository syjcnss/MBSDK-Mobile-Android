package com.daimler.mbmobilesdk.ui.widgets.loadingindicators

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.daimler.mbmobilesdk.R

internal class MBLoadingSpinner : ProgressBar {

    constructor(context: Context) : super(context, null, 0, R.style.MBLoadingSpinner)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs, 0, R.style.MBLoadingSpinner)
}