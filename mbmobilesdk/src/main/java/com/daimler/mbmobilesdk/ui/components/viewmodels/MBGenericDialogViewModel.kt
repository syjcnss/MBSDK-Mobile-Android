package com.daimler.mbmobilesdk.ui.components.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogButtonOrientation
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogClickable
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogFragmentButtonGroup
import com.daimler.mbmobilesdk.ui.components.recyclerview.MutableLiveArrayList
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveEvent
import com.daimler.mbmobilesdk.ui.lifecycle.events.MutableLiveUnitEvent

internal open class MBGenericDialogViewModel(
    val title: String?,
    val message: String?
) : ViewModel(), DialogFragmentButtonGroup.ButtonGroupListener {

    val buttonOrientation = MutableLiveData<DialogButtonOrientation>()
    val buttons = MutableLiveArrayList<DialogClickable<*>>()
    val buttonsInitializedEvent = MutableLiveUnitEvent()

    internal val buttonClickedEvent = MutableLiveEvent<DialogClickable<*>>()

    internal fun updateOrientation(orientation: DialogButtonOrientation) {
        this.buttonOrientation.postValue(orientation)
    }

    internal fun currentButtons(): List<DialogClickable<*>> = buttons.value.toList()

    internal fun updateButtons(buttons: List<DialogClickable<*>>) {
        this.buttons.apply {
            value.clear()
            value.addAll(buttons)
            dispatchChange()
        }
    }

    override fun onButtonClicked(clickable: DialogClickable<*>) {
        buttonClickedEvent.sendEvent(clickable)
    }

    override fun onButtonsInitialized() {
        buttonsInitializedEvent.sendEvent()
    }
}