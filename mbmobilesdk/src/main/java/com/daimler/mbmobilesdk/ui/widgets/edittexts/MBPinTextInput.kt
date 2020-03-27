package com.daimler.mbmobilesdk.ui.widgets.edittexts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.daimler.mbmobilesdk.R

internal class MBPinTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /**
     * Specifies, how many digit fields should be displayed.
     */
    var inputLength: Int = 0
        set(value) {
            field = value
            editTexts = Array(inputLength) {
                createEditText(it).apply { addView(this) }
            }
            editTexts.last().addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()) lastDigitEnteredListener?.onEntered()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })
        }

    /**
     * Listener, which is triggered when last digit was entered
     */
    var lastDigitEnteredListener: LastDigitEnteredListener? = null

    /**
     * Listener, which is triggered when a digit was changed
     */
    var onTextChangeListener: ((charWiseTextInput: MBPinTextInput) -> Unit)? = null

    var errorEnabled = false
        set(value) {
            field = value
            editTexts.forEach { it.isError = value }
        }

    /**
     * Listener, which is triggered, when a complete pin was entered
     */
    var pinListener: PinListener? = null

    /**
     * Listener, which is triggered, when all digits are empty and delete button is pressed (e.g. for changing focus to previous input field)
     */
    var deletePressedWhileEmptyListener: (() -> Unit)? = null

    private var editTexts = arrayOf<StatefulEditText>()
    private val globalInputType: Int
    private val divider: Drawable

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.MBPinTextInput, 0, 0).apply {
            try {
                globalInputType = getInt(R.styleable.MBPinTextInput_android_inputType, EditorInfo.TYPE_NULL)
                if (hasValue(R.styleable.MBPinTextInput_inputLength)) {
                    inputLength = getInt(R.styleable.MBPinTextInput_inputLength, 0)
                }
                divider = ShapeDrawable().apply {
                    intrinsicWidth = getDimensionPixelSize(R.styleable.MBPinTextInput_spacing, 0)
                    setTint(Color.TRANSPARENT)
                }
            } finally {
                recycle()
            }
        }

        orientation = HORIZONTAL
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = divider
    }

    private fun createEditText(key: Int): StatefulEditText {
        return (LayoutInflater.from(context).inflate(R.layout.edit_text_mbsdk_pin_text_input, this, false) as StatefulEditText).apply {
            inputType = globalInputType
            keyInterceptor = KeyInterceptor()
            pasteInterceptor = PasteInterceptor()
            addTextChangedListener(TextChangeListener(this))
            customSelectionActionModeCallback = SelectionActionCallback() // Disable copy & paste
            hint = EMPTY_STATE_CHAR
            tag = "${VIEW_ID_SUFFIX}_$key"
        }
    }

    fun deleteAndFocusLast() {
        editTexts.last().apply {
            text?.clear()
            requestFocus()
        }
    }

    private fun deletePrevious(activeEditText: StatefulEditText) {
        val newIndex = editTexts.indexOf(activeEditText) - 1
        if (newIndex >= 0 && newIndex < editTexts.size) {
            editTexts[newIndex].apply {
                text?.clear()
                requestFocus()
            }
        } else if (newIndex == -1) {
            deletePressedWhileEmptyListener?.invoke()
        }
    }

    private fun moveToNext(activeEditText: StatefulEditText) {
        val newIndex = editTexts.indexOf(activeEditText) + 1
        if (newIndex >= 0 && newIndex < editTexts.size) {
            editTexts[newIndex].apply {
                requestFocus()
                // selectAll()
            }
        }
    }

    /**
     * Returns currently entered digits
     */
    fun getText(): String {
        return StringBuilder().apply {
            editTexts.forEach {
                append(it.text)
            }
        }.toString()
    }

    /**
     * Resets all digit fields
     */
    fun clear() {
        editTexts.forEach { it.setText("") }
        editTexts.first().requestFocus()
    }

    interface PinListener {
        fun onPinChanged(s: String)
    }

    interface LastDigitEnteredListener {
        fun onEntered()
    }

    inner class KeyInterceptor : StatefulEditText.KeyInterceptor {
        override fun interceptKeyEvent(editText: StatefulEditText, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL && editText.length() == 0) {
                deletePrevious(editText)
                return true
            }
            return false
        }
    }

    inner class PasteInterceptor : StatefulEditText.PasteInterceptor {
        override fun interceptPaste(paste: CharSequence) {
            for ((index, editText) in editTexts.withIndex()) {
                if (index < paste.length) {
                    editText.setText(paste[index].toString())
                    if (index == paste.lastIndex) {
                        editText.setSelection(1)
                    }
                } else {
                    editText.setText("")
                }
            }
        }
    }

    inner class TextChangeListener(private val editText: StatefulEditText) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (it.length == 1) {
                    moveToNext(editText)
                }
                pinListener?.run { onPinChanged(getText()) }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChangeListener?.invoke(this@MBPinTextInput)
        }
    }

    inner class SelectionActionCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
        override fun onDestroyActionMode(mode: ActionMode?) {}
    }

    companion object {
        private const val EMPTY_STATE_CHAR = "-"
        private const val VIEW_ID_SUFFIX = "MBPinTextInput"
    }
}
