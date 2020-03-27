package com.daimler.mbmobilesdk.login.pin

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.implementation.IngressTokenProvider
import com.daimler.mbmobilesdk.implementation.AndroidStringProvider
import com.daimler.mbmobilesdk.implementation.DefaultErrorMessageProvider
import com.daimler.mbmobilesdk.login.credentials.UserLoginModel
import com.daimler.mbmobilesdk.login.credentials.PinInteractorImpl

internal class PinVerificationViewModelFactory(
    private val app: Application,
    private val userLoginModel: UserLoginModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val stringProvider = AndroidStringProvider(app.applicationContext.resources)
        val errorProvider = DefaultErrorMessageProvider(stringProvider)
        return PinVerificationViewModel(
            stringProvider,
            userLoginModel,
            errorProvider,
            PinInteractorImpl(
                MBIngressKit.userService()
            ),
            LoginInteractorImpl(
                MBIngressKit,
                IngressTokenProvider(),
                MBIngressKit.userService()
            )
        ) as T
    }
}