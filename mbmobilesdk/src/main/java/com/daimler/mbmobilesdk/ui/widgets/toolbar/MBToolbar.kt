package com.daimler.mbmobilesdk.ui.widgets.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.appcompat.widget.Toolbar
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.ui.utils.extensions.findColor
import kotlinx.android.synthetic.main.toolbar_mbsdk_default.view.*

/**
 * Basic toolbar that can either show a back button, a close button or just a title.
 */
internal class MBToolbar : Toolbar {

    @ButtonMode private var buttonMode = BUTTON_NONE
    private var listener: OnButtonClickedListener? = null

    constructor(context: Context) : super(context) {
        initialize(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.toolbar_mbsdk_default, this, true)
        setContentInsetsRelative(0, 0)
        contentInsetStartWithNavigation = 0
        iv_btn_start.setColorFilter(findColor(R.color.ris_white))
        iv_btn_start.setOnClickListener { listener?.onButtonClicked() }
    }

    fun setToolbarTitle(title: String?) {
        tv_title.text = title
    }

    fun setHasCloseButton(hasCloseButton: Boolean) =
            updateButtonState(BUTTON_CLOSE, hasCloseButton)

    fun setHasBackButton(hasBackButton: Boolean) =
            updateButtonState(BUTTON_BACK, hasBackButton)

    fun setButtonListener(listener: OnButtonClickedListener?) {
        this.listener = listener
    }

    private fun updateButtonState(@ButtonMode requestedMode: Int, shallShow: Boolean) {
        if (shallShow) {
            getButtonDrawable(requestedMode)?.let {
                iv_btn_start.setImageResource(it)
                buttonMode = requestedMode
            }
        } else if (buttonMode == requestedMode) {
            iv_btn_start.setImageDrawable(null)
            buttonMode = BUTTON_NONE
        }
    }

    @DrawableRes private fun getButtonDrawable(@ButtonMode requestedMode: Int): Int? =
            when (requestedMode) {
                BUTTON_BACK -> R.drawable.ic_arrow_back
                BUTTON_CLOSE -> R.drawable.ic_close
                else -> null
            }

    interface OnButtonClickedListener {
        fun onButtonClicked()
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(BUTTON_NONE, BUTTON_BACK, BUTTON_CLOSE)
    private annotation class ButtonMode

    private companion object {
        const val BUTTON_NONE = 0
        const val BUTTON_BACK = 1
        const val BUTTON_CLOSE = 2
    }
}