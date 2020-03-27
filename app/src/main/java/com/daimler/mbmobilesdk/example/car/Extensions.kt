package com.daimler.mbmobilesdk.example.car

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.daimler.mbcarkit.business.model.vehicle.image.VehicleImage

fun VehicleImage.getBitmap(): Bitmap? = imageBytes?.let {
    BitmapFactory.decodeByteArray(
        it,
        0,
        it.size
    )
}
