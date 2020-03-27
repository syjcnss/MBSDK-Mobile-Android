package com.daimler.mbmobilesdk

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.daimler.mbcarkit.MBCarKit
import com.daimler.mbcarkit.MBCarKitServiceConfig
import com.daimler.mbcarkit.proto.ProtoMessageParser
import com.daimler.mbcarkit.proto.ServiceProtoMessageParser
import com.daimler.mbingresskit.IngressServiceConfig
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.configuration.EndpointUrlProvider
import com.daimler.mbmobilesdk.configuration.MBMobileSDKConfiguration
import com.daimler.mbmobilesdk.configuration.IngressStage
import com.daimler.mbmobilesdk.preferences.PreferencesProxy
import com.daimler.mbmobilesdk.preferences.mbmobilesdk.MBMobileSDKSettings
import com.daimler.mbmobilesdk.utils.extensions.format
import com.daimler.mbnetworkkit.MBNetworkKit
import com.daimler.mbnetworkkit.NetworkServiceConfig
import com.daimler.mbnetworkkit.SocketServiceConfig
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.socket.SocketService
import java.util.*

/**
 * The core API class for the MBMobileSDK. Here the SDK is setup.
 */
@SuppressLint("StaticFieldLeak")
object MBMobileSDK {

    private lateinit var appSessionId: UUID
    private lateinit var context: Context
    private lateinit var preferencesProxy: PreferencesProxy
    private var initialized = false

    /**
     * Use this method to setup the SDK.
     * This should be your first call before calling any other components of the MBMobileSDK.
     *
     * @param configuration A configuration object can easily be created with help of [MBMobileSDKConfiguration.Builder].
     */
    fun setup(
        configuration: MBMobileSDKConfiguration
    ) {
        appSessionId = UUID.randomUUID()
        context = configuration.application
        preferencesProxy = PreferencesProxy(
            context,
            isSSOEnabled(configuration),
            configuration.sharedUserId
        )
        val deviceId = handleDeviceId(preferencesProxy, configuration.deviceId)

        val headerService = setupNetworkKit(configuration)

        setupIngressKit(configuration, headerService, deviceId)
        setupCarKit(configuration, headerService)
        setupSocketService(configuration, headerService)

        initialized = true
    }

    /**
     * Perform a logout. Done is called when logout is finished.
     *
     * @param done Callback lambda.
     */
    fun logoutAndCleanUp(done: () -> Unit) {
        MBIngressKit.logout()
            .onAlways { _, _, _ ->
                MBCarKit.disconnectFromWebSocket()
                MBCarKit.clearLocalCache()
                MBIngressKit.clearLocalCache()
                done()
            }
    }

    // Persists the deviceId if necessary and returns it.
    private fun handleDeviceId(settings: MBMobileSDKSettings, deviceId: String?): String {
        val pref = settings.mobileSdkDeviceId
        return deviceId?.takeIf {
            it.isNotBlank() && it != pref.get()
        }?.let {
            pref.set(it)
            it
        } ?: pref.get()
    }

    private fun setupNetworkKit(configuration: MBMobileSDKConfiguration): HeaderService {
        val networkConfig =
            NetworkServiceConfig.Builder(
                configuration.appIdentifier,
                appVersion(),
                configuration.mobileSdkVersion
            ).apply {
                useOSVersion(Build.VERSION.RELEASE)
                useLocale(Locale.getDefault().format())
            }.build()
        MBNetworkKit.init(networkConfig)
        return MBNetworkKit.headerService()
    }

    private fun setupIngressKit(
        configuration: MBMobileSDKConfiguration,
        headerService: HeaderService,
        deviceId: String
    ) {
        val urlProvider = configuration.urlProvider
        val ingressConfig =
            IngressServiceConfig.Builder(
                context,
                urlProvider.authUrl,
                urlProvider.bffUrl,
                ingressStage(urlProvider),
                configuration.ingressKeyStoreAlias,
                headerService,
                configuration.clientId
            ).apply {
                if (isSSOEnabled(configuration)) {
                    enableSso(configuration.sharedUserId)
                }
                useAppSessionId(appSessionId)
                useDeviceId(deviceId)
                configuration.expiredHandler?.let {
                    useSessionExpiredHandler(it)
                }
                configuration.certificateConfiguration?.let { certificateConfiguration ->
                        useCertificatePinning(certificateConfiguration, configuration.errorProcessor)
                }
            }
                .build()

        MBIngressKit.init(ingressConfig)
    }

    private fun setupCarKit(
        configuration: MBMobileSDKConfiguration,
        headerService: HeaderService
    ) {
        val carKitConfig = MBCarKitServiceConfig.Builder(
            context, configuration.urlProvider.bffUrl,
            headerService
        ).apply {
            configuration.pinProvider?.let {
                usePinProvider(it)
            }
            if (isSSOEnabled(configuration)) {
                shareSelectedVehicle(configuration.sharedUserId)
            }
            configuration.certificateConfiguration?.let { certificateConfiguration ->
                    useCertificatePinning(certificateConfiguration, configuration.errorProcessor)
            }
        }.build()

        MBCarKit.init(carKitConfig)
    }

    private fun setupSocketService(
        configuration: MBMobileSDKConfiguration,
        headerService: HeaderService
    ) {

        val socketServiceBuilder = SocketServiceConfig.Builder(
            configuration.urlProvider.socketUrl,
            MBCarKit.createMycarMessageProcessor(
                ProtoMessageParser(),
                configuration.pinCommandVehicleApiStatusCallback,
                MBCarKit.createServiceMessageProcessor(ServiceProtoMessageParser())
            )
        ).apply {
            useAppSessionId(appSessionId)
            useHeaderService(headerService)
            configuration.reconnectConfig?.let {
                tryPeriodicReconnect(it.first.toLong(), it.second, configuration.tokenProvider)
            }
            configuration.certificateConfiguration?.let { certificateConfiguration ->
                    useCertificatePinning(certificateConfiguration, configuration.errorProcessor)
            }
        }
        SocketService.init(socketServiceBuilder.create())
    }

    private fun appVersion() =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    private fun ingressStage(urlProvider: EndpointUrlProvider): String {
        val stage = if (urlProvider.isProductiveEnvironment) {
            IngressStage.PROD
        } else {
            IngressStage.INT
        }
        return stage.identifier
    }

    private fun isSSOEnabled(configuration: MBMobileSDKConfiguration): Boolean {
        return configuration.ingressKeyStoreAlias.isNotBlank() && configuration.sharedUserId.isNotBlank()
    }
}