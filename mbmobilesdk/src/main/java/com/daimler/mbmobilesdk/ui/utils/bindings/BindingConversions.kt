package com.daimler.mbmobilesdk.ui.utils.bindings

import androidx.databinding.BindingConversion
import android.view.View

@BindingConversion
internal fun convertBooleanToVisibility(isVisible: Boolean) = if (isVisible) View.VISIBLE else View.GONE