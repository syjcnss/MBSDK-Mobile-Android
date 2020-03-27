package com.daimler.mbmobilesdk.ui.components.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class MBGenericDialogViewModelFactory(
    private val title: String?,
    private val msg: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MBGenericDialogViewModel(title, msg) as T
}