package com.daimler.mbmobilesdk.ui.utils.bindings

import androidx.databinding.BindingAdapter
import com.daimler.mbmobilesdk.ui.widgets.edittexts.MBPinTextInput

@BindingAdapter("inputLength")
internal fun setInputLength(pinTextInput: MBPinTextInput, inputLength: Int) {
    pinTextInput.inputLength = inputLength
}