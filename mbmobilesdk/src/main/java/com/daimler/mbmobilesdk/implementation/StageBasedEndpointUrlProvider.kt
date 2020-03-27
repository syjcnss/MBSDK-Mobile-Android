package com.daimler.mbmobilesdk.implementation

import com.daimler.mbmobilesdk.configuration.EndpointUrlProvider
import com.daimler.mbmobilesdk.configuration.Region
import com.daimler.mbmobilesdk.configuration.Stage

internal class StageBasedEndpointUrlProvider(region: Region, stage: Stage) : EndpointUrlProvider {

    override val isProductiveEnvironment: Boolean = stage == Stage.PROD

    override val authUrl: String =
        "https://keycloak.risingstars${stage.stageSuffix}${region.regionSuffix}.daimler.com"

    override val bffUrl: String =
        "https://bff-${stage.stageName}.risingstars${stage.stageSuffix}${region.regionSuffix}.daimler.com"

    override val socketUrl: String =
        "wss://websocket-${stage.stageName}.risingstars${stage.stageSuffix}${region.regionSuffix}.daimler.com/ws"
}