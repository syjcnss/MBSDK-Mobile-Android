package com.daimler.mbmobilesdk.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class MBLoginViewModelFactory :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MBBaseLoginViewModel() as T
    }
}