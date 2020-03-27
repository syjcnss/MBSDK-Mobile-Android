package com.daimler.mbmobilesdk.example

import android.app.Application
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbloggerkit.PrinterConfig
import com.daimler.mbloggerkit.adapter.AndroidLogAdapter
import com.daimler.mbloggerkit.adapter.PersistingLogAdapter
import com.daimler.mbloggerkit.shake.LogOverlay
import com.daimler.mbmobilesdk.BuildConfig
import com.daimler.mbmobilesdk.MBMobileSDK
import com.daimler.mbmobilesdk.configuration.MBMobileSDKConfiguration
import com.daimler.mbmobilesdk.configuration.Region
import com.daimler.mbmobilesdk.configuration.Stage

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupMobileSdk()
    }

    private fun setupMobileSdk() {

        //Enable logging in the modules
        MBLoggerKit.usePrinterConfig(
            PrinterConfig.Builder()
                .showLogMenuWithShake(this, LogOverlay.Order.CHRONOLOGICAL_DESCENDING)
                .addAdapter(
                    AndroidLogAdapter.Builder()
                        .setLoggingEnabled(BuildConfig.DEBUG)
                        .build())
                .addAdapter(
                    PersistingLogAdapter.Builder(this)
                        .useMemoryLogging()
                        .setLoggingEnabled(BuildConfig.DEBUG)
                        .build())
                .build())

        //Basic setup of the modules
        val config = MBMobileSDKConfiguration.Builder(
            application = this,
            appIdentifier = "reference",
            clientId = "app"
        ).defaultRegionAndStage(Region.ECE, Stage.MOCK)
            .build()

        MBMobileSDK.setup(config)
    }
}