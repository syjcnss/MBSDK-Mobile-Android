package com.daimler.mbmobilesdk.ui.components.dialogfragments

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.BR
import com.daimler.mbmobilesdk.databinding.ViewPartMbsdkPinDialogBinding
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.*
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBPinDialogViewModel
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBPinDialogViewModelFactory
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver

/**
 * Simple dialog fragment that shows a title, a message, a pin input field and multiple buttons.
 *
 * ```
 *  class MyActivity : MBBaseViewModelActivity<MyViewModel>() {
 *
 *      // ...
 *
 *      fun showDialog() {
 *          MBPinDialogFragment.Builder().apply {
 *              withTitle("MyTitle")
 *              withMessage("MyMessage")
 *              withConfirmButton("Confirm") { handlePinInput(it) }
 *              withCancelButton("Cancel") { handlePinInputCancelled() }
 *              addNeutralButton("Help") { showHelp() }
 *          }.build()
 *          .show(supportFragmentManager, null)
 *      }
 *  }
 * ```
 */
internal class MBPinDialogFragment : MBGenericDialogFragment<MBPinDialogViewModel>() {

    private var pinChangedListener: PinChangedListener? = null
    private var pinLength = DEFAULT_PIN_LENGTH

    override fun createViewModel(): MBPinDialogViewModel {
        arguments?.getInt(ARG_PIN_LENGTH)?.let { pinLength = it }
        val factory = MBPinDialogViewModelFactory(title, msg, pinLength)
        return ViewModelProvider(this, factory).get(MBPinDialogViewModel::class.java)
    }

    override fun onBindingCreated(binding: ViewDataBinding) {
        super.onBindingCreated(binding)
        viewModel.buttonsInitializedEvent.observe(this, onButtonsInitialized())
        viewModel.pin.observe(this, onPinChanged())
    }

    override fun onInflateBelowMessage(root: ViewGroup) {
        super.onInflateBelowMessage(root)
        val binding = DataBindingUtil.inflate<ViewPartMbsdkPinDialogBinding>(
            layoutInflater,
            R.layout.view_part_mbsdk_pin_dialog,
            root,
            true
        )
        binding.apply {
            setLifecycleOwner(this@MBPinDialogFragment)
            setVariable(BR.model, viewModel)
        }
    }

    override fun <T> onButtonClicked(button: DialogClickable<T>) {
        when (button.purpose) {
            ClickablePurpose.POSITIVE -> button.invokeClickAction(viewModel.pin.value.orEmpty())
            ClickablePurpose.NEGATIVE -> button.invokeClickAction(Unit)
            ClickablePurpose.NEUTRAL -> button.invokeClickAction(Unit)
        }
    }

    private fun onButtonsInitialized() = LiveEventObserver<Unit> {
        updateButtonEnabledState(ClickablePurpose.POSITIVE, false)
    }

    private fun onPinChanged() = Observer<String> {
        updateButtonEnabledState(ClickablePurpose.POSITIVE, it.length == pinLength)
        pinChangedListener?.onPinChanged(it, pinLength)
    }

    class Builder : BaseDialogBuilder<String, Unit>() {

        private var pinChangedListener: PinChangedListener? = null

        private var pinLength = DEFAULT_PIN_LENGTH

        fun withTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun withMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun withPinChangedListener(pinChangedListener: PinChangedListener): Builder {
            this.pinChangedListener = pinChangedListener
            return this
        }

        fun withOrientation(orientation: DialogButtonOrientation): Builder {
            this.orientation = orientation
            return this
        }

        fun withPinLength(pinLength: Int): Builder {
            this.pinLength = pinLength
            return this
        }

        fun addNeutralButton(
            title: String,
            style: DialogButtonStyle = DialogButtonStyle.SECONDARY,
            dismissOnClick: Boolean = true,
            clickAction: ((Unit) -> Unit)? = null
        ): Builder {
            neutralButtons.add(TypedDialogButton(title, ClickablePurpose.NEUTRAL, style, dismissOnClick, clickAction))
            return this
        }

        fun withConfirmPinButton(title: String, action: (String) -> Unit): Builder {
            positiveButton = TypedDialogButton(title, ClickablePurpose.POSITIVE, DialogButtonStyle.PRIMARY, true, action)
            return this
        }

        fun withCancelButton(title: String, action: ((Unit) -> Unit)? = null): Builder {
            negativeButton = TypedDialogButton(title, ClickablePurpose.NEGATIVE, DialogButtonStyle.SECONDARY, true, action)
            return this
        }

        fun build(): MBPinDialogFragment {
            return MBPinDialogFragment().apply {
                applyBundleToFragment(this)
                arguments?.putInt(ARG_PIN_LENGTH, pinLength)
                applyButtonsToFragment(this)
                this.pinChangedListener = this@Builder.pinChangedListener
            }
        }
    }

    interface PinChangedListener {
        fun onPinChanged(pin: String, pinLength: Int)
    }

    private companion object {

        const val ARG_PIN_LENGTH = "arg.pin.dialog.pin.length"
        const val DEFAULT_PIN_LENGTH = 4
    }
}