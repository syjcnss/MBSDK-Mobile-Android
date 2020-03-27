package com.daimler.mbmobilesdk.example.login

import android.app.Activity
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.login.MBMobileSDKLoginActivity

class LoginActivity : MBMobileSDKLoginActivity() {

    override fun onLoginFailed(error: String) {
        super.onLoginFailed(error)
        MBLoggerKit.e("Login failed")
    }

    override fun onUserLoggedIn() {
        MBLoggerKit.d("User login success")
        setResult(Activity.RESULT_OK)
        finish()
    }
}