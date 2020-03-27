package com.daimler.mbmobilesdk.login

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.BR
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.login.credentials.CredentialsFragment
import com.daimler.mbmobilesdk.login.credentials.UserLoginModel
import com.daimler.mbmobilesdk.login.pin.PinVerificationFragment
import com.daimler.mbmobilesdk.utils.extensions.isLoggedIn
import com.daimler.mbmobilesdk.ui.components.activities.MBBaseViewModelActivity
import com.daimler.mbmobilesdk.ui.components.dialogfragments.MBDialogFragment
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver
import com.daimler.mbmobilesdk.ui.utils.extensions.replaceFragment
import kotlinx.android.synthetic.main.activity_mbsdk_login.*

/**
 * Basic login activity. Subclass this activity and implement the abstract methods.
 */
abstract class MBMobileSDKLoginActivity : MBBaseViewModelActivity<MBBaseLoginViewModel>(),
    MBBaseLoginCallback {

    /**
     * Called when the login user confirmed. The user is fully authorized afterwards.
     */
    protected abstract fun onUserLoggedIn()

    /**
     * Called when the user is not logged in and the credentials input is shown.
     */
    protected open fun onUserLoginRequired() = Unit

    /**
     * Called when the user requests a login with a mail or mobile number.
     */
    protected open fun onUserStartedLogin() = Unit

    /**
     * Called when there is no user account available for the given user name and an registration
     * is required.
     */
    protected open fun onUserRegistrationRequired(userName: String, isMail: Boolean) {
        MBDialogFragment.Builder().apply {
            withMessage(getString(R.string.login_user_not_registered))
            withPositiveButton(getString(R.string.general_ok))
        }.build().show(supportFragmentManager, null)
    }

    /**
     * Trigger showing of PinVerification fragment.
     */
    protected open fun showPinVerificationFragment(user: UserLoginModel) {
        onShowPinVerification(user)
    }

    /**
     * Called when the login process failed.
     * The default implementation shows an error dialog.
     */
    open fun onLoginFailed(error: String) {
        MBDialogFragment.Builder()
            .withTitle(getString(R.string.general_error_title))
            .withMessage(error)
            .withPositiveButton(getString(R.string.general_ok)) { requestVerificationCodeFieldFocus() }
            .build()
            .show(supportFragmentManager, null)
    }

    override fun createViewModel(): MBBaseLoginViewModel {
        val factory = MBLoginViewModelFactory()
        return ViewModelProvider(this, factory).get(MBBaseLoginViewModel::class.java)
    }

    override fun getLayoutRes(): Int = R.layout.activity_mbsdk_login

    override fun getModelId(): Int = BR.model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            onUserLoggedIn()
        } else {
            onUserLoginRequired()
            replaceFragment(R.id.content_container, CredentialsFragment.newInstance())
        }
    }

    override fun onBindingCreated(binding: ViewDataBinding) {
        super.onBindingCreated(binding)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        viewModel.backClickedEvent.observe(this, onBackClicked())
    }

    final override fun onUserNotRegistered(dataLoginModel: UserLoginModel) {
        onUserRegistrationRequired(dataLoginModel.user, dataLoginModel.isMail)
    }

    final override fun onLoginSuccess(dataLoginModel: UserLoginModel) {
        onUserLoggedIn()
    }

    final override fun onLoginError(error: String) {
        onLoginFailed(error)
    }

    final override fun onLoginStarted() {
        onUserStartedLogin()
    }

    final override fun onShowPinVerification(dataLoginModel: UserLoginModel) {
        replaceFragment(R.id.content_container,
            PinVerificationFragment.getInstance(dataLoginModel),
            tag = TAG_TAN_FRAGMENT, addToBackStack = true)
    }

    final override fun hideToolbar() {
        viewModel.toolbarVisible.postValue(false)
    }

    final override fun setToolbarTitle(title: String) {
        viewModel.toolbarTitle.postValue(title)
        viewModel.toolbarVisible.postValue(true)
    }

    private fun isLoggedIn(): Boolean = MBIngressKit.authenticationService().isLoggedIn()

    private fun onBackClicked() = LiveEventObserver<Unit> {
        onBackPressed()
    }

    private fun requestVerificationCodeFieldFocus() {
        (supportFragmentManager.findFragmentByTag(TAG_TAN_FRAGMENT) as? PinVerificationFragment)?.focusVerificationCodeInputField()
    }

    companion object {

        private const val TAG_TAN_FRAGMENT = "ris.tag.fragment.tan"
    }
}