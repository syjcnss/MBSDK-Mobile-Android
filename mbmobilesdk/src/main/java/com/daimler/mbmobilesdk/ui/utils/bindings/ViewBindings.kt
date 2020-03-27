package com.daimler.mbmobilesdk.ui.utils.bindings

import androidx.databinding.BindingAdapter
import android.view.View

/**
 * Sets the view's selected state.
 */
@BindingAdapter("itemSelected")
fun setItemStateSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}