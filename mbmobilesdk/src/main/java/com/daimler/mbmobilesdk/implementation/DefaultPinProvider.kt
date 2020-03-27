package com.daimler.mbmobilesdk.implementation

import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.daimler.mbcarkit.business.PinProvider
import com.daimler.mbcarkit.business.PinRequest
import com.daimler.mbcarkit.business.model.command.CommandVehicleApiStatus
import com.daimler.mbcarkit.socket.PinCommandVehicleApiStatusCallback
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbingresskit.common.UserPinStatus
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.utils.UserValidator
import com.daimler.mbmobilesdk.utils.WeakRefActivityLifecycleCallbacks
import com.daimler.mbmobilesdk.ui.components.dialogfragments.MBDialogFragment
import com.daimler.mbmobilesdk.ui.components.dialogfragments.MBPinDialogFragment
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogButtonOrientation
import java.util.*
import java.util.concurrent.TimeUnit

internal class DefaultPinProvider(application: Application) : WeakRefActivityLifecycleCallbacks(),
    PinProvider,
    PinCommandVehicleApiStatusCallback {

    private var currentPinRequest: PinRequest? = null

    init {
        initCallbacks(application)
    }

    override fun requestPin(pinRequest: PinRequest) {
        MBIngressKit.cachedUser()?.run {
            withFragmentActivity {
                currentPinRequest = pinRequest
                if (pinStatus != UserPinStatus.NOT_SET) {
                    showPinDialog(this)
                } else {
                    showNoPinDialog(this)
                }
            }
        } ?: MBLoggerKit.e("Tried to show pin, but there is no user!")
    }

    override fun onPinAccepted(commandStatus: CommandVehicleApiStatus, pin: String) {
        clearCurrentPinRequest()
    }

    override fun onPinInvalid(commandStatus: CommandVehicleApiStatus, pin: String) {
        withFragmentActivity {
            MBDialogFragment.Builder().also { builder ->
                builder.withTitle(this.getString(R.string.pin_popup_wrong_pin_title))
                builder.withMessage(this.getString(R.string.pin_popup_wrong_pin_msg))
                builder.withPositiveButton(this.getString(R.string.pin_popup_button_again)) {
                    currentPinRequest?.let {
                        requestPin(it)
                    }
                }
                builder.withNegativeButton(this.getString(R.string.pin_popup_cancel)) {
                    clearCurrentPinRequest()
                }
                builder.withOrientation(DialogButtonOrientation.VERTICAL)
            }.build().show(this.supportFragmentManager, null)
        }
    }

    override fun onUserBlocked(
        commandStatus: CommandVehicleApiStatus,
        pin: String,
        attempts: Int,
        blockingTimeSeconds: Int
    ) {
        withFragmentActivity {
            showUserBlockedDialog(this, attempts, blockingTimeSeconds.toLong())
            clearCurrentPinRequest()
            true
        } ?: run {
            clearCurrentPinRequest()
            false
        }
    }

    private fun showNoPinDialog(activity: FragmentActivity) {
        MBDialogFragment.Builder()
            .withTitle(activity.getString(R.string.pin_popup_title))
            .withMessage(activity.getString(R.string.pin_popup_default_no_pin_message))
            .withPositiveButton(activity.getString(R.string.general_ok)) {
                // nothing happened
            }
            .build().show(activity.supportFragmentManager, null)
    }

    private fun showPinDialog(activity: FragmentActivity) {
        MBPinDialogFragment.Builder()
            .withTitle(activity.getString(R.string.pin_popup_title))
            .withMessage(
                String.format(
                    activity.getString(R.string.pin_popup_description),
                    UserValidator.USER_PIN_DIGITS
                )
            )
            .withConfirmPinButton(activity.getString(R.string.pin_popup_send)) {
                currentPinRequest?.confirmPin(it)
            }
            .withCancelButton(activity.getString(R.string.pin_popup_cancel)) {
                currentPinRequest?.cancel("Cancelled by user.")
                clearCurrentPinRequest()
            }
            .build().show(activity.supportFragmentManager, null)
    }

    private fun showUserBlockedDialog(
        activity: FragmentActivity,
        attempts: Int,
        blockingTimeSeconds: Long
    ) {
        MBDialogFragment.Builder()
            .withTitle(activity.getString(R.string.general_error_title))
            .withMessage(
                String.format(
                    activity.getString(R.string.command_error_blocked_ciam_id),
                    attempts,
                    getMinutesLeft(blockingTimeSeconds)
                )
            )
            .withPositiveButton(activity.getString(R.string.general_ok))
            .build().show(activity.supportFragmentManager, null)
    }

    private fun clearCurrentPinRequest() {
        currentPinRequest = null
    }

    private fun getMinutesLeft(blockingTimeSeconds: Long): String =
        withActivity {
            when (val minutes =
                TimeUnit.SECONDS.toMinutes(blockingTimeSeconds) - TimeUnit.MILLISECONDS.toMinutes(
                    Date().time
                )) {
                1L -> String.format(getString(R.string.command_error_minute), minutes)
                else -> String.format(getString(R.string.command_error_minutes), minutes)
            }
        }.orEmpty()
}