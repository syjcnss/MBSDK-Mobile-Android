package com.daimler.mbmobilesdk.login.credentials

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.implementation.AndroidStringProvider
import com.daimler.mbmobilesdk.implementation.DefaultErrorMessageProvider
import com.daimler.mbmobilesdk.implementation.UserInputErrorMessageProviderImpl

internal class CredentialsViewModelFactory(
    private val app: Application,
    private val userId: String?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val stringProvider = AndroidStringProvider(app.applicationContext.resources)
        return CredentialsViewModel(
            stringProvider,
            userId,
            UserInputErrorMessageProviderImpl(DefaultErrorMessageProvider(stringProvider)),
            PinInteractorImpl(
                MBIngressKit.userService()
            )
        ) as T
    }
}