package com.daimler.mbmobilesdk.implementation

import com.daimler.mbcarkit.business.model.command.CommandVehicleApiStatus
import com.daimler.mbcarkit.socket.PinCommandVehicleApiStatusCallback

internal class DefaultPinCommandVehicleApiStatusListener : PinCommandVehicleApiStatusCallback {

    override fun onPinAccepted(commandStatus: CommandVehicleApiStatus, pin: String) {
    }

    override fun onPinInvalid(commandStatus: CommandVehicleApiStatus, pin: String) {
    }

    override fun onUserBlocked(
        commandStatus: CommandVehicleApiStatus,
        pin: String,
        attempts: Int,
        blockingTimeSeconds: Int
    ) {
    }
}