package com.daimler.mbmobilesdk.ui.widgets.edittexts

import android.text.method.PasswordTransformationMethod
import android.view.View

internal class MBPasswordTransformation : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    private inner class PasswordCharSequence(private val mSource: CharSequence) // Store char sequence
        : CharSequence {
        override val length: Int
            get() = mSource.length

        override fun get(index: Int) = '\u2022'

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return mSource.subSequence(startIndex, endIndex) // Return default
        }
    }
}