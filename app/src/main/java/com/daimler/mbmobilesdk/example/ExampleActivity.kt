package com.daimler.mbmobilesdk.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbmobilesdk.example.databinding.ActivityExampleBinding
import com.daimler.mbmobilesdk.example.login.LoginActivity
import com.daimler.mbmobilesdk.example.vehicleSelection.VehicleSelectionActivity
import com.daimler.mbmobilesdk.utils.extensions.isLoggedIn

class ExampleActivity : AppCompatActivity() {

    private val exampleViewModel = ExampleViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityExampleBinding>(
            this,
            R.layout.activity_example
        ).apply {
            viewModel = exampleViewModel
            lifecycleOwner = this@ExampleActivity
        }
        setupUI()
    }

    private fun setupUI() {
        subscribeToLoginEvent()
        setupSelectVehicleButton()
        exampleViewModel.requestVehicleImage()
    }


    private fun subscribeToLoginEvent() {
        exampleViewModel.onLoginEvent.observe(this, Observer {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(
                intent,
                LOGIN_REQUEST
            )
        })
    }

    private fun setupSelectVehicleButton() {
        exampleViewModel.onSelectVehicleEvent.observe(this, Observer {
            val intent = Intent(this, VehicleSelectionActivity::class.java)
            startActivityForResult(
                intent,
                VEHICLE_REQUEST
            )
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOGIN_REQUEST -> {
                exampleViewModel.isUserLoggedIn.postValue(MBIngressKit.authenticationService().isLoggedIn())
            }
            VEHICLE_REQUEST -> {
                exampleViewModel.requestVehicleImage()
                exampleViewModel.requestCurrentVehicleStatus()
            }
            else -> {
                // Unknown request code
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exampleViewModel.disconnect()
    }

    companion object {
        private const val LOGIN_REQUEST = 1
        private const val VEHICLE_REQUEST = 2
    }
}
