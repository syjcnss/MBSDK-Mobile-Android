package com.daimler.mbmobilesdk.example

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daimler.mbcarkit.business.model.vehicle.DoorLockOverallStatus
import com.daimler.mbcarkit.business.model.vehicle.Tank
import com.daimler.mbcarkit.business.model.vehicle.WindowsOverallStatus
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.MBMobileSDK
import com.daimler.mbmobilesdk.example.car.CarKitConnectionListener
import com.daimler.mbmobilesdk.example.car.CarKitRepository
import com.daimler.mbmobilesdk.example.car.CarKitVehicleStatusListener
import com.daimler.mbmobilesdk.example.car.getBitmap
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveUnitEvent
import com.daimler.mbmobilesdk.utils.extensions.isLoggedIn
import com.daimler.mbnetworkkit.socket.ConnectionState

class ExampleViewModel : ViewModel(), CarKitConnectionListener, CarKitVehicleStatusListener {

    private val carKitRepository = CarKitRepository()
    val onLoginEvent = MutableLiveUnitEvent()
    val onSelectVehicleEvent = MutableLiveUnitEvent()
    val isUserLoggedIn = MutableLiveData<Boolean>(false)
    val isVehicleConnected = MutableLiveData<Boolean>(false)
    val isDoorsLocked = MutableLiveData<Boolean>(false)
    val doorStateText = MutableLiveData<String>()
    val isDoorStateValid = MutableLiveData<Boolean>(false)
    val isWindowsLocked = MutableLiveData<Boolean>(false)
    val windowStateText = MutableLiveData<String>()
    val isWindowStateValid = MutableLiveData<Boolean>(false)
    val vehicleImage = MutableLiveData<Bitmap>()
    val gasRangeText = MutableLiveData<String>()
    val liquidRangeText = MutableLiveData<String>()
    val electricRangeText = MutableLiveData<String>()


    init {
        carKitRepository.addStatusListener(this)
        carKitRepository.addConnectionListener(this)
        isUserLoggedIn.postValue(MBIngressKit.authenticationService().isLoggedIn())
    }

    fun onLoginClicked() {
        onLoginEvent.sendEvent()
    }

    fun onLogoutClicked() {
        isUserLoggedIn.postValue(false)
        MBMobileSDK.logoutAndCleanUp {
            reset()
            carKitRepository.disconnect()
        }
    }

    fun onSelectVehicleClicked() {
        onSelectVehicleEvent.sendEvent()
    }

    fun onCarConnectClicked() {
        carKitRepository.connectToSocket()
        requestCurrentVehicleStatus()
    }

    fun onCarDisconnectClicked() {
        resetCarTextInfo()
        carKitRepository.disconnect()
    }

    fun onUnlockDoorsClicked() {
        carKitRepository.unlockDoors()
    }

    fun onLockDoorsClicked() {
        carKitRepository.lockDoors()
    }

    fun onOpenWindowsClicked() {
        carKitRepository.openWindows()
    }

    fun onCloseWindowsClicked() {
        carKitRepository.closeWindows()
    }

    fun requestVehicleImage() {
        carKitRepository.fetchVehicleImages().onComplete { vehicleImages ->
            vehicleImage.postValue(vehicleImages.first().getBitmap())
        }
    }

    fun requestCurrentVehicleStatus() {
        carKitRepository.loadCurrentVehicleStatus()
    }

    fun disconnect() {
        carKitRepository.disconnect()
    }

    override fun onConnectionStateChanged(connectionState: ConnectionState) {
        isVehicleConnected.postValue(connectionState is ConnectionState.Connected)
        isUserLoggedIn.postValue(MBIngressKit.authenticationService().isLoggedIn())
    }

    override fun onDoorLockOverallStatusChanged(doorLockOverallStatus: DoorLockOverallStatus) {
        isDoorsLocked.postValue(doorLockOverallStatus == DoorLockOverallStatus.LOCKED)
        doorStateText.postValue(doorLockOverallStatus.name)
        isDoorStateValid.postValue(doorLockOverallStatus != DoorLockOverallStatus.UNKNOWN)
    }

    override fun onWindowOverallStatusChanged(windowsOverallStatus: WindowsOverallStatus) {
        isWindowsLocked.postValue(windowsOverallStatus == WindowsOverallStatus.CLOSED)
        windowStateText.postValue(windowsOverallStatus.name)
        isWindowStateValid.postValue(windowsOverallStatus != WindowsOverallStatus.UNKNOWN)
    }

    override fun onTankInformationChanged(tank: Tank) {
        tank.gasRange.value?.let { gasRangeValue ->
            gasRangeText.postValue(gasRangeValue.toString())
        } ?: run { gasRangeText.postValue(String()) }

        tank.liquidRange.value?.let { liquidRangeValue ->
            liquidRangeText.postValue(liquidRangeValue.toString())
        } ?: run { liquidRangeText.postValue(String()) }

        tank.electricRange.value?.let { electricRangeValue ->
            electricRangeText.postValue(electricRangeValue.toString())
        } ?: run { electricRangeText.postValue(String()) }
    }

    override fun onCleared() {
        super.onCleared()
        carKitRepository.removeStatusListener(this)
        carKitRepository.removeConnectionListener(this)
    }

    private fun reset() {
        isVehicleConnected.value = false
        isDoorsLocked.value = false
        isWindowsLocked.value = false
        vehicleImage.value = null
        resetCarTextInfo()
    }

    private fun resetCarTextInfo() {
        doorStateText.value = String()
        windowStateText.value = String()
        gasRangeText.value = String()
        liquidRangeText.value = String()
        electricRangeText.value = String()
        isDoorStateValid.value = false
        isWindowStateValid.value = false
    }
}