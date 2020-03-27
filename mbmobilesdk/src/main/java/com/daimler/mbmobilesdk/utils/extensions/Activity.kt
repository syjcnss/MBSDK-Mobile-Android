package com.daimler.mbmobilesdk.utils.extensions

import androidx.fragment.app.FragmentActivity
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.ui.components.dialogfragments.MBDialogFragment

fun FragmentActivity.showOkayDialog(
    message: String,
    listener: ((Unit) -> Unit)? = null
) {
    MBDialogFragment.Builder().apply {
        withMessage(message)
        withPositiveButton(getString(R.string.general_ok), listener)
    }.build().show(supportFragmentManager, null)
}
