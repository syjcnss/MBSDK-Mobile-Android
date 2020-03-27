package com.daimler.mbmobilesdk.login.credentials

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbmobilesdk.BR
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.databinding.FragmentMbsdkCredentialsBinding
import com.daimler.mbmobilesdk.login.*
import com.daimler.mbmobilesdk.ui.components.dialogfragments.MBDialogFragment
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver
import com.daimler.mbmobilesdk.utils.extensions.application
import com.daimler.mbmobilesdk.utils.extensions.canShowDialog
import com.daimler.mbmobilesdk.utils.extensions.simpleTextObserver

internal class CredentialsFragment : BaseLoginFragment<CredentialsViewModel>() {

    private var player: LoginMediaPlayer? = null

    override fun createViewModel(): CredentialsViewModel {
        val userId = arguments?.getString(EXTRA_ME_ID, null)
        val factory =
            CredentialsViewModelFactory(
                application,
                userId
            )
        return ViewModelProvider(this, factory).get(CredentialsViewModel::class.java)
    }

    override fun getLayoutRes(): Int = R.layout.fragment_mbsdk_credentials

    override fun getModelId(): Int = BR.model

    override fun onBindingCreated(binding: ViewDataBinding) {
        super.onBindingCreated(binding)
        val credentialsBinding: FragmentMbsdkCredentialsBinding = binding as FragmentMbsdkCredentialsBinding

        context?.let { ctx ->
            player = LoginMediaPlayer(
                ctx,
                credentialsBinding.textureVideoView
            )
        }

        viewModel.navigateToPinVerification.observe(this, navigateToPinVerification())
        viewModel.pinRequestError.observe(this, requestPinFailed())
        viewModel.pinRequestStarted.observe(this, pinRequestStarted())
        viewModel.errorEvent.observe(this, errorEvent())
        viewModel.showMmeIdInfoEvent.observe(this, showMmeIdInfo())
        viewModel.userNotRegisteredEvent.observe(this, userNotRegistered())

        notifyHideToolbar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        player?.resume()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
        player = null
    }

    private fun pinRequestStarted() = LiveEventObserver<String> {
        notifyLoginStarted()
        viewModel.continueLogin(it)
    }

    private fun requestPinFailed() = LiveEventObserver<String> {
        notifyPinError(it)
    }

    private fun userNotRegistered() = LiveEventObserver<UserLoginModel> {
        notifyUserNotRegistered(it)
    }

    private fun navigateToPinVerification() = LiveEventObserver<UserLoginModel> {
        notifyShowPinVerification(it)
    }

    private fun errorEvent() = simpleTextObserver()

    private fun showMmeIdInfo() = LiveEventObserver<Unit> {
        if (canShowDialog()) {
            activity?.let { activity ->
                MBDialogFragment.Builder().apply {
                    withTitle(getString(R.string.login_info_alert_title))
                    withMessage(getString(R.string.login_info_alert_message))
                    withPositiveButton(getString(R.string.general_ok))
                }.build().show(activity.supportFragmentManager, null)
            }
        }
    }

    companion object {

        private const val EXTRA_ME_ID = "extra.credentials.me.id"

        fun newInstance(userId: String? = null): CredentialsFragment {
            val fragment =
                CredentialsFragment()
            val bundle = Bundle().apply {
                putString(EXTRA_ME_ID, userId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}