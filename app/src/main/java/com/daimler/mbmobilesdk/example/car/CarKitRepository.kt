package com.daimler.mbmobilesdk.example.car

import com.daimler.mbcarkit.MBCarKit
import com.daimler.mbcarkit.business.model.command.*
import com.daimler.mbcarkit.business.model.vehicle.*
import com.daimler.mbcarkit.business.model.vehicle.image.*
import com.daimler.mbcarkit.socket.observable.VehicleObserver
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.socket.ConnectionState
import com.daimler.mbnetworkkit.socket.SocketConnectionListener
import com.daimler.mbnetworkkit.socket.message.Observables
import com.daimler.mbnetworkkit.socket.message.dispose
import com.daimler.mbnetworkkit.socket.message.observe
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.TaskObject

class CarKitRepository : SocketConnectionListener {

    private var statusListeners = mutableListOf<CarKitVehicleStatusListener>()
    private var connectionListeners = mutableListOf<CarKitConnectionListener>()
    private var observables: Observables? = null
    private val doorObserver = VehicleObserver.Doors { doorsStateUpdated(it) }
    private val windowObserver = VehicleObserver.Windows { windowsStateUpdated(it) }
    private val tankLevelObserver = VehicleObserver.Tank { tankLevelUpdated(it) }

    fun fetchVehicles(): FutureTask<Vehicles, ResponseError<out RequestError>?> {
        val task = TaskObject<Vehicles, ResponseError<out RequestError>?>()
        MBIngressKit.refreshTokenIfRequired().onComplete { token ->
            MBCarKit.vehicleService()
                .fetchVehicles(token.jwtToken.plainToken)
                .onComplete {
                    MBLoggerKit.i("Car retrieval successful: " + it.vehicles.size)
                    task.complete(it)
                }
                .onFailure {
                    task.fail(it)
                    MBLoggerKit.e("Error while fetching vehicles")
                }
        }
        return task
    }

    fun selectVehicle(vehicleInfo: VehicleInfo) {
        MBCarKit.selectVehicle(vehicleInfo)
    }

    fun loadCurrentVehicleStatus() {
        MBCarKit.selectedFinOrVin()?.let {
            val vehicleStatus = MBCarKit.loadCurrentVehicleState(it)

            doorsStateUpdated(vehicleStatus.doors)
            windowsStateUpdated(vehicleStatus.windows)
            tankLevelUpdated(vehicleStatus.tank)
        }
    }

    fun connectToSocket() {
        MBIngressKit.refreshTokenIfRequired().onComplete {
            MBCarKit.connectToWebSocket(it.jwtToken.plainToken, this)
        }
    }

    fun disconnect() {
        MBCarKit.disconnectFromWebSocket()
    }

    fun fetchVehicleImages(): FutureTask<List<VehicleImage>, ResponseError<out RequestError>?> {
        val task: TaskObject<List<VehicleImage>, ResponseError<out RequestError>?> = TaskObject()
        MBIngressKit.refreshTokenIfRequired().onComplete { token ->
            MBCarKit.selectedFinOrVin()?.let { vin ->
                val imageRequest = createImageRequest(vin)
                MBCarKit.vehicleImageService()
                    .fetchVehicleImages(token.jwtToken.plainToken, imageRequest)
                    .onComplete { task.complete(it) }
                    .onFailure { task.fail(it) }
            }
        }
        return task
    }

    fun fetchVehicleImagesWithVin(vin: String): FutureTask<List<VehicleImage>, ResponseError<out RequestError>?> {
        val task: TaskObject<List<VehicleImage>, ResponseError<out RequestError>?> = TaskObject()
        MBIngressKit.refreshTokenIfRequired().onComplete { token ->
            val imageRequest = createImageRequest(vin)
            MBCarKit.vehicleImageService()
                .fetchVehicleImages(token.jwtToken.plainToken, imageRequest)
                .onComplete { task.complete(it) }
                .onFailure { task.fail(it) }
        }
        return task
    }

    private fun createImageRequest(finOrVin: String): VehicleImageRequest {
        val imageConfig = ImageConfig.Builder(ImageBackground.CUTOUT).build()
        val imageKey = ImageKey.DynamicExterior(
            ImagePerspective.PerspectiveExterior(Degrees.DEGREES_0),
            VehicleImageType.Png(VehicleImagePngSize.SIZE_1920x1080),
            ImageManipulation.None
        )
        return VehicleImageRequest.Builder(finOrVin, imageConfig)
            .apply { addImage(imageKey) }
            .build()
    }

    fun unlockDoors() {
        MBCarKit.selectedFinOrVin()?.let {
            val vehicleCommand: VehicleCommand.DoorsUnlock =
                VehicleCommand.DoorsUnlock(it)

            MBCarKit.sendVehicleCommand(vehicleCommand, object :
                VehicleCommandCallback<DoorsUnlockError> {
                override fun onError(timestamp: Long?, errors: List<DoorsUnlockError>) {
                    MBLoggerKit.e(errors.toString())
                }

                override fun onSuccess(timestamp: Long?) {
                    MBLoggerKit.i("DoorsLockedEvent successful")
                }

                override fun onUpdate(status: VehicleCommandStatusUpdate) {
                    MBLoggerKit.i("DoorsLockedEvent updated")
                }
            })
        }
    }

    fun lockDoors() {
        MBCarKit.selectedFinOrVin()?.let {
            val vehicleCommand: VehicleCommand.DoorsLock =
                VehicleCommand.DoorsLock(it)

            MBCarKit.sendVehicleCommand(
                vehicleCommand,
                object : VehicleCommandCallback<DoorsLockError> {
                    override fun onError(timestamp: Long?, errors: List<DoorsLockError>) {
                        MBLoggerKit.e(errors.toString())
                    }

                    override fun onSuccess(timestamp: Long?) {
                        MBLoggerKit.i("DoorsLockedEvent successful")
                    }

                    override fun onUpdate(status: VehicleCommandStatusUpdate) {
                        MBLoggerKit.i("DoorsLockedEvent updated")
                    }
                })
        }
    }

    fun openWindows() {
        MBCarKit.selectedFinOrVin()?.let {
            val vehicleCommand: VehicleCommand.WindowsOpen =
                VehicleCommand.WindowsOpen(it)

            MBCarKit.sendVehicleCommand(vehicleCommand, object :
                VehicleCommandCallback<WindowsOpenError> {
                override fun onError(timestamp: Long?, errors: List<WindowsOpenError>) {
                    MBLoggerKit.e(errors.toString())
                }

                override fun onSuccess(timestamp: Long?) {
                    MBLoggerKit.i("WindowsOpenEvent successful")
                }

                override fun onUpdate(status: VehicleCommandStatusUpdate) {
                    MBLoggerKit.i("WindowsOpenEvent updated")
                }
            })
        }
    }

    fun closeWindows() {
        MBCarKit.selectedFinOrVin()?.let {
            val vehicleCommand: VehicleCommand.WindowsClose =
                VehicleCommand.WindowsClose(it)

            MBCarKit.sendVehicleCommand(vehicleCommand, object :
                VehicleCommandCallback<WindowsCloseError> {
                override fun onError(timestamp: Long?, errors: List<WindowsCloseError>) {
                    MBLoggerKit.e(errors.toString())
                }

                override fun onSuccess(timestamp: Long?) {
                    MBLoggerKit.i("WindowsCloseEvent successful")
                }

                override fun onUpdate(status: VehicleCommandStatusUpdate) {
                    MBLoggerKit.i("WindowsCloseEvent updated")
                }
            })
        }
    }

    override fun connectionStateChanged(connectionState: ConnectionState) {
        when (connectionState) {
            is ConnectionState.Connected -> {
                observables = connectionState.observables
                observables?.let { registerObservers(it) }

            }
            is ConnectionState.ConnectionLost, ConnectionState.Disconnected -> {
                observables?.let { disposeObservers(it) }
                observables = null

                clearVehicleStatus()
            }
        }
        connectionListeners.forEach {
            it.onConnectionStateChanged(connectionState)
        }
    }

    private fun registerObservers(observables: Observables) {
        observables.observe(doorObserver)
        observables.observe(windowObserver)
        observables.observe(tankLevelObserver)
    }

    private fun disposeObservers(observables: Observables) {
        observables.dispose(doorObserver)
        observables.dispose(windowObserver)
        observables.dispose(tankLevelObserver)
    }

    private fun clearVehicleStatus() {
        doorsStateUpdated(null)
        windowsStateUpdated(null)
        tankLevelUpdated(null)
    }

    private fun doorsStateUpdated(doors: Doors?) {
        doors?.lockStateOverall?.value?.let { status ->
            statusListeners.forEach {
                it.onDoorLockOverallStatusChanged(status)
            }
        } ?: run {
            statusListeners.forEach {
                it.onDoorLockOverallStatusChanged(DoorLockOverallStatus.UNKNOWN)
            }
        }
    }

    private fun windowsStateUpdated(windows: Windows?) {
        windows?.overallState?.value?.let { status ->
            statusListeners.forEach {
                it.onWindowOverallStatusChanged(status)
            }
        } ?: run {
            statusListeners.forEach {
                it.onWindowOverallStatusChanged(WindowsOverallStatus.UNKNOWN)
            }
        }
    }

    private fun tankLevelUpdated(tank: Tank?) {
        tank?.let { currentTank ->
            statusListeners.forEach {
                it.onTankInformationChanged(currentTank)
            }
        }
    }

    fun addStatusListener(vararg listener: CarKitVehicleStatusListener) {
        statusListeners.addAll(listener)
    }

    fun removeStatusListener(vararg listener: CarKitVehicleStatusListener) {
        statusListeners.removeAll(listener)
    }

    fun addConnectionListener(vararg listener: CarKitConnectionListener) {
        connectionListeners.addAll(listener)
    }

    fun removeConnectionListener(vararg listener: CarKitConnectionListener) {
        connectionListeners.removeAll(listener)
    }
}