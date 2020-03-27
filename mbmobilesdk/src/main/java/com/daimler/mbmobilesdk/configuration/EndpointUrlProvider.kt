package com.daimler.mbmobilesdk.configuration

/**
 * Provider for endpoint URLs used by the SDK.
 */
interface EndpointUrlProvider {

    /**
     * True, if the given URLs point to a productive environment.
     */
    val isProductiveEnvironment: Boolean

    /**
     * Returns the URL used for authorization token exchanges.
     */
    val authUrl: String

    /**
     * Returns the URL used for the backend.
     */
    val bffUrl: String

    /**
     * Returns the URL used for socket connections.
     */
    val socketUrl: String
}