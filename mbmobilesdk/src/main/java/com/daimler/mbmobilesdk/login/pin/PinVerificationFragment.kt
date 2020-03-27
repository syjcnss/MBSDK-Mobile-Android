package com.daimler.mbmobilesdk.login.pin

import android.os.Bundle
import android.os.Handler
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbmobilesdk.BR
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.databinding.FragmentMbsdkPinVerificationBinding
import com.daimler.mbmobilesdk.login.BaseLoginFragment
import com.daimler.mbmobilesdk.login.credentials.UserLoginModel
import com.daimler.mbmobilesdk.utils.extensions.*
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver
import com.daimler.mbmobilesdk.ui.utils.extensions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_mbsdk_pin_verification.*

internal class PinVerificationFragment : BaseLoginFragment<PinVerificationViewModel>() {

    override fun createViewModel(): PinVerificationViewModel {
        val userLoginModel: UserLoginModel = requireArguments().getParcelable(ARG_USER)
            ?: throw IllegalArgumentException("No user in parcel")
        return ViewModelProvider(this, PinVerificationViewModelFactory(
            application, userLoginModel
        )).get(PinVerificationViewModel::class.java)
    }

    override fun getLayoutRes(): Int = R.layout.fragment_mbsdk_pin_verification

    override fun getModelId(): Int = BR.model

    override fun onBindingCreated(binding: ViewDataBinding) {
        super.onBindingCreated(binding)

        viewModel.processing.observe(this, progressChanged())
        viewModel.onPinError.observe(this, onPinError())
        viewModel.onPinVerified.observe(this, onPinVerified())
        viewModel.onPinRequested.observe(this, onPinRequested())
        viewModel.onPinRequestError.observe(this, onPinRequestError())

        notifyToolbarTitle(getString(R.string.verification_title))
    }

    private fun onPinError() = LiveEventObserver<String> {
        notifyPinError(it)
    }

    private fun progressChanged() = Observer<Boolean> {
        val progress = it == true
        if (progress) activity?.hideKeyboard()
    }

    private fun onPinRequested() = LiveEventObserver<Unit> {
        (binding as? FragmentMbsdkPinVerificationBinding)?.editPin?.clear()
        if (canShowDialog()) {
            activity?.showOkayDialog(getString(R.string.verification_send_again_msg))
        }
    }

    private fun onPinRequestError() = simpleTextObserver()

    private fun onPinVerified() =
        LiveEventObserver<PinVerificationViewModel.PinVerificationEvent> {
            notifyPinVerified(it.userLoginModel)
        }

    fun focusVerificationCodeInputField() {
        // Minimal delay is necessary for other UI-animations to complete first
        Handler().postDelayed({ edit_pin.toggleKeyboard() }, SHOW_KEYBOARD_DELAY)
    }

    companion object {

        private const val ARG_USER = "arg.pin.user"
        private const val SHOW_KEYBOARD_DELAY = 50L

        fun getInstance(userLoginModel: UserLoginModel): PinVerificationFragment {
            val pinFragment = PinVerificationFragment()
            val arguments = Bundle()
            arguments.apply {
                putParcelable(ARG_USER, userLoginModel)
            }
            pinFragment.arguments = arguments
            return pinFragment
        }
    }
}