package com.daimler.mbmobilesdk.ui.components.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class MBPinDialogViewModelFactory(
    private val title: String?,
    private val msg: String?,
    private val pinLength: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MBPinDialogViewModel(title, msg, pinLength) as T
}