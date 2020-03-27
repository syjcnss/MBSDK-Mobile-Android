package com.daimler.mbmobilesdk.utils.extensions

import android.app.Application
import androidx.fragment.app.Fragment
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver

internal fun Fragment.showSimpleTextDialog(msg: String) {
    activity?.showOkayDialog(message = msg)
}

internal fun Fragment.simpleTextObserver() =
    LiveEventObserver<String> {
        if (canShowDialog()) {
            showSimpleTextDialog(it)
        } else {
            MBLoggerKit.e("Did not show dialog since fragment is not visible.")
            MBLoggerKit.e("Message was: $it")
        }
    }

internal fun Fragment.canShowDialog() = isVisible && isAdded

internal val Fragment.application: Application
    get() = activity?.application ?: throw IllegalArgumentException("Activity is null!")
