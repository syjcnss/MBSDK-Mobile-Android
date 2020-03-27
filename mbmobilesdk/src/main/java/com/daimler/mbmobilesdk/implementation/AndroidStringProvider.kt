package com.daimler.mbmobilesdk.implementation

import android.content.res.Resources
import com.daimler.mbmobilesdk.business.StringProvider

class AndroidStringProvider(
    private val resources: Resources
) : StringProvider {

    override fun getString(resourceId: Int): String = resources.getString(resourceId)
}