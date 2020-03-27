package com.daimler.mbmobilesdk.ui.lifecycle.events

import androidx.lifecycle.MutableLiveData

/**
 * Convenient class to make the syntax for [LiveEvent] easier.
 *
 * Example usage:
 * ```
 * class MyViewModel : ViewModel() {
 *
 *      val myEventField = MutableLiveEvent<Int>()
 *
 *      fun applyEvent(myNumber: Int) = myEventField.sendEvent(myNumber)
 * }
 * ```
 *
 * @see LiveEvent
 * @see LiveEventObserver
 */
internal class MutableLiveEvent<T> : MutableLiveData<LiveEvent<T>>() {

    fun sendEvent(t: T) = postValue(LiveEvent(t))
}