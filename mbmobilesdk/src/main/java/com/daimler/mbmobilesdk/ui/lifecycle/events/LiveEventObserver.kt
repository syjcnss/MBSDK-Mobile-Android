package com.daimler.mbmobilesdk.ui.lifecycle.events

import androidx.lifecycle.Observer

/**
 * Convenient observer class to use with [LiveEvent] that only triggers events
 * when the content was not already handled.
 */
internal class LiveEventObserver<T>(private val action: (T) -> Unit) : Observer<LiveEvent<T>> {

    override fun onChanged(event: LiveEvent<T>?) {
        event?.getContentIfNotHandled()?.let { action(it) }
    }
}