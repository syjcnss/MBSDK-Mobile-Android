package com.daimler.mbmobilesdk.ui.widgets.edittexts

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import com.daimler.mbmobilesdk.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

internal class MBEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    private val textBottomPadding = context.resources
        .getDimension(R.dimen.mb_edittext_text_bottom_padding).toInt()

    private val parentInputLayout: TextInputLayout? by lazy {
        if (parent.parent is TextInputLayout) parent.parent as TextInputLayout else null
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        post { alignTextVertically() }
    }

    private fun alignTextVertically() {
        gravity =
            if (!parentInputLayout?.hint.isNullOrEmpty() && (isFocused || !text.isNullOrEmpty())) {
                setPadding(paddingLeft, 0, paddingRight, textBottomPadding)
                Gravity.BOTTOM
            } else {
                setPadding(paddingLeft, 0, paddingRight, 0)
                Gravity.CENTER_VERTICAL
            }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        alignTextVertically()
        super.onLayout(changed, left, top, right, bottom)
    }
}