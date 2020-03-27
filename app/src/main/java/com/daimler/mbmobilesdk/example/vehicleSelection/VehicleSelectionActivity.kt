package com.daimler.mbmobilesdk.example.vehicleSelection

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.daimler.mbmobilesdk.example.R
import com.daimler.mbmobilesdk.example.databinding.ActivityVehicleSelectionBinding
import kotlinx.android.synthetic.main.activity_vehicle_selection.*

class VehicleSelectionActivity : AppCompatActivity() {

    private val vehicleSelectionViewModel = VehicleSelectionViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityVehicleSelectionBinding>(
            this,
            R.layout.activity_vehicle_selection
        ).apply {
            viewModel = vehicleSelectionViewModel
            lifecycleOwner = this@VehicleSelectionActivity
        }
        subscribeToSelectionEvent()
        fragment_vehicle_selection_recycler_view.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayout.VERTICAL
            )
        )
    }

    override fun onStart() {
        super.onStart()
        vehicleSelectionViewModel.fetchVehiclesForSelection()
    }

    private fun subscribeToSelectionEvent() {
        vehicleSelectionViewModel.onVehicleSelectedEvent.observe(this, Observer {
            setResult(RESULT_OK)
            finish()
        })
    }
}