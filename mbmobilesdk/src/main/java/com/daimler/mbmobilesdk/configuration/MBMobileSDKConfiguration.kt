package com.daimler.mbmobilesdk.configuration

import android.app.Application
import com.daimler.mbcarkit.business.PinProvider
import com.daimler.mbcarkit.socket.PinCommandVehicleApiStatusCallback
import com.daimler.mbingresskit.login.SessionExpiredHandler
import com.daimler.mbmobilesdk.BuildConfig
import com.daimler.mbmobilesdk.implementation.IngressTokenProvider
import com.daimler.mbmobilesdk.implementation.DefaultPinProvider
import com.daimler.mbmobilesdk.implementation.StageBasedEndpointUrlProvider
import com.daimler.mbnetworkkit.certificatepinning.CertificateConfiguration
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinningErrorProcessor
import com.daimler.mbnetworkkit.common.TokenProvider

class MBMobileSDKConfiguration private constructor(
    val application: Application,
    val expiredHandler: SessionExpiredHandler?,
    val appIdentifier: String,
    val clientId: String,
    val sharedUserId: String,
    val urlProvider: EndpointUrlProvider,
    val pinProvider: PinProvider?,
    val ingressKeyStoreAlias: String,
    val reconnectConfig: Pair<Int, Int>?,
    val tokenProvider: TokenProvider,
    val pinCommandVehicleApiStatusCallback: PinCommandVehicleApiStatusCallback,
    val mobileSdkVersion: String,
    val certificateConfiguration: List<CertificateConfiguration>?,
    val errorProcessor: CertificatePinningErrorProcessor?,
    val deviceId: String?
) {

    class Builder(
        /**
         * Android application.
         */
        private val application: Application,
        /**
         * The client id.
         */
        private var clientId: String,
        /**
         * Application identifier.
         */
        private var appIdentifier: String
    ) {
        // optional / default initialized parameter
        private val defaultPinProvider =
            DefaultPinProvider(application)
        private var sharedUserId: String = ""
        private var reconnectConfig: Pair<Int, Int>? = null
        private var pinProvider: PinProvider = defaultPinProvider
        private var pinCommandVehicleApiStatusCallback: PinCommandVehicleApiStatusCallback =
            defaultPinProvider
        private var sessionExpiredHandler: SessionExpiredHandler? = null
        private var urlProvider: EndpointUrlProvider = StageBasedEndpointUrlProvider(
            Region.ECE, Stage.PROD)
        private var mobileSdkVersion: String = BuildConfig.MOBILE_SDK_VERSION
        private var tokenProvider: TokenProvider =
            IngressTokenProvider()
        private var ingressKeyStoreAlias: String = ""
        private var certificateConfiguration: List<CertificateConfiguration>? = null
        private var errorProcessor: CertificatePinningErrorProcessor? = null
        private var deviceId: String? = null

        /**
         * Enables or disables the SSO feature as well as all sharing of files and information
         * for family apps. If nothing is set SSO is not enabled by default. If multiple apps
         * have the same sharedUserId they must all be signed with the same signature.
         */
        fun enableSso(sharedUserId: String, keyStoreAlias: String): Builder {
            this.sharedUserId = sharedUserId
            this.ingressKeyStoreAlias = keyStoreAlias
            return this
        }

        /**
         * Specifies parameters for the socket reconnection in case of connection failures.
         */
        fun usePeriodicReconnect(periodInSeconds: Int, maxRetries: Int): Builder {
            this.reconnectConfig = periodInSeconds to maxRetries
            return this
        }

        /**
         * Sets the default [PinProvider] that shall be used for car commands that
         * require pin authentication.
         */
        fun usePinProvider(pinProvider: PinProvider): Builder {
            this.pinProvider = pinProvider
            return this
        }

        /**
         * Sets the default [SessionExpiredHandler] that shall be used for the case that the
         * current tokens are in an invalid state.
         */
        fun useSessionExpiredHandler(sessionExpiredHandler: SessionExpiredHandler): Builder {
            this.sessionExpiredHandler = sessionExpiredHandler
            return this
        }

        /**
         * Sets the token provider which is used to provide the user's authentication token. E.g. while trying to
         * automatically reconnect to the web socket.
         */
        fun useTokenProvider(tokenProvider: TokenProvider): Builder {
            this.tokenProvider = tokenProvider
            return this
        }

        /**
         * Use custom SDK version.
         * Used for identification of requests to the backend.
         * Default value is the current version of the MobileSDK.
         */
        fun useMobileSdkVersion(mobileSdkVersion: String): Builder {
            this.mobileSdkVersion = mobileSdkVersion
            return this
        }

        /**
         * Enables certificate pinning for networking operations within the MobileSDK ecosystem.
         */
        fun enableCertificatePinning(certificateConfiguration: List<CertificateConfiguration>, errorProcessor: CertificatePinningErrorProcessor?): Builder {
            this.certificateConfiguration = certificateConfiguration
            this.errorProcessor = errorProcessor
            return this
        }

        /**
         * [Region] and [Stage] that is used for the initialization of the underlying modules.
         * Default is [Region.ECE] and [Stage.PROD].
         * Any calls that were prior made to [useCustomUrlProvider] will be discarded.
         */
        fun defaultRegionAndStage(region: Region, stage: Stage): Builder {
            this.urlProvider = StageBasedEndpointUrlProvider(region, stage)
            return this
        }

        /**
         * Sets a custom provider for endpoint URLs. Any calls that were prior made to
         * [defaultRegionAndStage] will be discarded.
         */
        fun useCustomUrlProvider(urlProvider: EndpointUrlProvider): Builder {
            this.urlProvider = urlProvider
            return this
        }

        /**
         * In order to allow critical command (e.g. Unlock vehicle doors) [PinCommandVehicleApiStatusCallback] must be set.
         */
        fun usePinCallback(
            vehicleApiStatusCallback: PinCommandVehicleApiStatusCallback
        ): Builder {
            this.pinCommandVehicleApiStatusCallback = vehicleApiStatusCallback
            return this
        }

        /**
         * Overrides the device ID that is used to uniquely identify the user's device within
         * the MobileSDK ecosystem.
         * It is also possible and recommended to just let the SDK generate a device ID.
         */
        fun useDeviceId(deviceId: String): Builder {
            this.deviceId = deviceId
            return this
        }

        /**
         * Creates and returns an [MBMobileSDKConfiguration] object.
         */
        fun build(): MBMobileSDKConfiguration {
            return MBMobileSDKConfiguration(
                application,
                sessionExpiredHandler,
                appIdentifier,
                clientId,
                sharedUserId,
                urlProvider,
                pinProvider,
                ingressKeyStoreAlias,
                reconnectConfig,
                tokenProvider,
                pinCommandVehicleApiStatusCallback,
                mobileSdkVersion,
                certificateConfiguration,
                errorProcessor,
                deviceId
            )
        }
    }
}