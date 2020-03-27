package com.daimler.mbmobilesdk.ui.widgets.edittexts

import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.daimler.mbmobilesdk.R
import com.google.android.material.textfield.TextInputEditText

internal class StatefulEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    var onErrorStateChangeListener: ((isError: Boolean) -> Unit)? = null

    var isError = false
        set(value) {
            if (field != value) {
                field = value
                refreshDrawableState()
                onErrorStateChangeListener?.invoke(field)
            }
        }

    var dismissErrorOnChange = false

    var keyInterceptor: KeyInterceptor? = null

    var pasteInterceptor: PasteInterceptor? = null

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.StatefulEditText, defStyleAttr, 0).apply {
            try {
                isError = getBoolean(R.styleable.StatefulEditText_isError, false)
                dismissErrorOnChange = getBoolean(R.styleable.StatefulEditText_dismissErrorOnChange, false)
            } finally {
                recycle()
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (dismissErrorOnChange) {
                    isError = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        if (isPasswordInputType(inputType)) {
            transformationMethod = MBPasswordTransformation()
        }
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        return (variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val state = super.onCreateDrawableState(extraSpace + 1)
        if (isError) {
            mergeDrawableStates(state, STATE_ERROR)
        }
        return state
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        val connection: InputConnection? = super.onCreateInputConnection(outAttrs)
        return connection?.let { DeletionInputConnection(it, true) }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        return if (id == android.R.id.paste) {
            pasteInterceptor?.let {
                it.interceptPaste(textFromClipboard())
                true
            } ?: false
        } else {
            super.onTextContextMenuItem(id)
        }
    }

    private fun textFromClipboard(): CharSequence {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        return clip?.let {
            val text = clip.getItemAt(0).coerceToText(context)
            (text as? Spanned)?.toString() ?: text
        } ?: ""
    }

    inner class DeletionInputConnection(target: InputConnection?, mutable: Boolean) : InputConnectionWrapper(target, mutable) {
        override fun sendKeyEvent(event: KeyEvent?): Boolean {
            event?.let {
                if (keyInterceptor?.interceptKeyEvent(this@StatefulEditText, event) == true) {
                    return false
                }
            }
            return super.sendKeyEvent(event)
        }
    }

    interface KeyInterceptor {
        fun interceptKeyEvent(editText: StatefulEditText, event: KeyEvent): Boolean
    }

    interface PasteInterceptor {
        fun interceptPaste(paste: CharSequence)
    }

    companion object {
        private val STATE_ERROR = intArrayOf(R.attr.state_error)
    }
}
