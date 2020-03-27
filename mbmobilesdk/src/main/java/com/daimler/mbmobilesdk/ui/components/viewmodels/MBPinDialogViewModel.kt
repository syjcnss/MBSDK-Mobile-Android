package com.daimler.mbmobilesdk.ui.components.viewmodels

import androidx.lifecycle.MutableLiveData

internal class MBPinDialogViewModel(
    title: String?,
    msg: String?,
    val pinLength: Int
) : MBGenericDialogViewModel(title, msg) {

    val pin = MutableLiveData<String>()

    fun onPinInputChanged(s: String) {
        pin.postValue(s)
    }
}