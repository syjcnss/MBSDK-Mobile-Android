package com.daimler.mbmobilesdk.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.daimler.mbmobilesdk.ui.components.fragments.MBBaseViewModelFragment
import com.daimler.mbmobilesdk.login.credentials.UserLoginModel

internal abstract class BaseLoginFragment<T : ViewModel> : MBBaseViewModelFragment<T>() {

    private var baseLoginCallback: MBBaseLoginCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MBBaseLoginCallback) {
            "Parent Activity which hosts ${BaseLoginFragment::class.java.simpleName} " +
                "must implement ${MBBaseLoginCallback::class.java.simpleName}"
        }
        baseLoginCallback = context
    }

    override fun onDestroy() {
        super.onDestroy()
        baseLoginCallback = null
    }

    internal fun notifyHideToolbar() {
        baseLoginCallback?.hideToolbar()
    }

    internal fun notifyToolbarTitle(title: String) {
        baseLoginCallback?.setToolbarTitle(title)
    }

    internal fun notifyPinVerified(userLoginModel: UserLoginModel) {
        baseLoginCallback?.onLoginSuccess(userLoginModel)
    }

    internal fun notifyPinError(error: String) {
        baseLoginCallback?.onLoginError(error)
    }

    internal fun notifyUserNotRegistered(userLoginModel: UserLoginModel) {
        baseLoginCallback?.onUserNotRegistered(userLoginModel)
    }

    internal fun notifyShowPinVerification(userLoginModel: UserLoginModel) {
        baseLoginCallback?.onShowPinVerification(userLoginModel)
    }

    internal fun notifyLoginStarted() {
        baseLoginCallback?.onLoginStarted()
    }
}