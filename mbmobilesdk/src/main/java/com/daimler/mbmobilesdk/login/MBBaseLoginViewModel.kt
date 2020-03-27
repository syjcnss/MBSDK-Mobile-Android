package com.daimler.mbmobilesdk.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveUnitEvent

open class MBBaseLoginViewModel : ViewModel() {

    val toolbarVisible = MutableLiveData(true)
    val toolbarTitle = MutableLiveData<String>()

    val backClickedEvent = MutableLiveUnitEvent()

    fun onBackClicked() {
        backClickedEvent.sendEvent()
    }
}