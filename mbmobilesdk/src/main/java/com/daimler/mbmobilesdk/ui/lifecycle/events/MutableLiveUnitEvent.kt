package com.daimler.mbmobilesdk.ui.lifecycle.events

import androidx.lifecycle.MutableLiveData

/**
 * Convenient class for [MutableLiveEvent] with type [Unit].
 */
class MutableLiveUnitEvent : MutableLiveData<LiveEvent<Unit>>() {

    fun sendEvent() = postValue(LiveEvent(Unit))
}