package com.daimler.mbmobilesdk.ui.components.dialogfragments

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.daimler.mbmobilesdk.R
import com.daimler.mbmobilesdk.BR
import com.daimler.mbmobilesdk.databinding.FragmentMbsdkDialogBinding
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.ClickablePurpose
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogButtonOrientation
import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.DialogClickable
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBGenericDialogViewModel
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBGenericDialogViewModelFactory
import com.daimler.mbmobilesdk.ui.lifecycle.events.LiveEventObserver

/**
 * Base dialog fragment that is a [MBBaseDialogFragment] and provides a layout that can hold
 * a title, a message, up to two buttons and a generic layout container that can be filled with
 * any views.
 */
internal abstract class MBGenericDialogFragment<T : MBGenericDialogViewModel> : MBBaseDialogFragment<T>() {

    /** Dialog's title. */
    protected var title: String? = null

    /** Dialog's message. */
    protected var msg: String? = null

    /** The orientation for the buttons below the dialog content. */
    private var buttonOrientation: DialogButtonOrientation? = null

    /** The buttons of the dialog. */
    protected var dialogButtons: List<DialogClickable<*>> = emptyList()

    final override fun getLayoutRes(): Int = R.layout.fragment_mbsdk_dialog

    final override fun getModelId(): Int = BR.model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
    }

    @Suppress("UNCHECKED_CAST")
    override fun createViewModel(): T {
        val factory = MBGenericDialogViewModelFactory(title, msg)
        return ViewModelProvider(this, factory).get(MBGenericDialogViewModel::class.java) as T
    }

    override fun onBindingCreated(binding: ViewDataBinding) {
        super.onBindingCreated(binding)
        (binding as FragmentMbsdkDialogBinding).let {
            onInflateAboveMessage(it.viewContainerTop)
            onInflateBelowMessage(it.viewContainerBottom)
        }

        isCancelable = false

        arguments?.let {
            adjustTextAlignment(it, binding)
        }

        // We do not need to calculate buttons if they are already added. (Orientation change)
        if (viewModel.currentButtons().isEmpty()) {
            val buttons = dialogButtons
            checkButtons(buttons)
            val orientation = getButtonOrientation(buttons)
            viewModel.updateOrientation(orientation)
            viewModel.updateButtons(buttons)
        }
        viewModel.buttonClickedEvent.observe(this, onDialogButtonClickedEvent())
    }

    private fun adjustTextAlignment(args: Bundle, binding: FragmentMbsdkDialogBinding) {
        binding.tvTitle.centerAlignIf(args.getBoolean(ARG_TITLE_CENTERED, true))
        binding.tvMessage.centerAlignIf(args.getBoolean(ARG_MESSAGE_CENTERED, true))
    }

    private fun onDialogButtonClickedEvent() =
        LiveEventObserver<DialogClickable<*>> {
            dispatchButtonClick(it)
        }

    /**
     * Add additional views to the given container above the dialog's message.
     */
    open fun onInflateAboveMessage(root: ViewGroup) = Unit

    /**
     * Add additional views to the given container below the dialog's message.
     */
    open fun onInflateBelowMessage(root: ViewGroup) = Unit

    /**
     * Called when one of the given [DialogClickable]s was clicked.
     */
    protected abstract fun <T> onButtonClicked(button: DialogClickable<T>)

    /**
     * Casts `this` [DialogClickable] to a [DialogClickable] with the generic type [T] and
     * invokes its [DialogClickable.clickAction] with the given parameter [t].
     *
     * @throws ClassCastException if the cast did not succeed
     */
    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T> DialogClickable<*>.invokeClickAction(t: T) =
        (this as DialogClickable<T>).clickAction?.invoke(t)

    /**
     * Updates the enabled state for buttons with the given purpose.
     * Can only be called after [onBindingCreated] was invoked.
     */
    fun updateButtonEnabledState(purpose: ClickablePurpose, enabled: Boolean) {
        (binding as? FragmentMbsdkDialogBinding)?.viewButtons?.updateButtonEnabledState(purpose, enabled)
    }

    private fun getButtonOrientation(buttons: List<DialogClickable<*>>): DialogButtonOrientation {
        val requestedOrientation = buttonOrientation
        return when {
            buttons.size > VERTICAL_ORIENTATION_THRESHOLD -> DialogButtonOrientation.VERTICAL
            requestedOrientation != null -> requestedOrientation
            else -> DialogButtonOrientation.HORIZONTAL
        }
    }

    private fun initArguments() {
        val args = arguments
            ?: throw java.lang.IllegalArgumentException("Create this fragment with arguments through applyBundleToFragment()!")
        title = args.getString(ARG_TITLE)
        msg = args.getString(ARG_MESSAGE)
        buttonOrientation = args.getSerializable(ARG_BUTTONS_ORIENTATION) as? DialogButtonOrientation
    }

    protected fun putBundle(
        title: String?,
        msg: String?,
        orientation: DialogButtonOrientation?,
        titleCentered: Boolean = true,
        messageCentered: Boolean = true
    ) {
        val bundle = Bundle().apply {
            putString(ARG_TITLE, title)
            putString(ARG_MESSAGE, msg)
            putSerializable(ARG_BUTTONS_ORIENTATION, orientation)
            putBoolean(ARG_TITLE_CENTERED, titleCentered)
            putBoolean(ARG_MESSAGE_CENTERED, messageCentered)
        }
        arguments = bundle
    }

    private fun notifyPositiveAndDismiss() {
        dismiss()
    }

    private fun notifyNegativeAndDismiss() {
        dismiss()
    }

    private fun checkButtons(buttons: List<DialogClickable<*>>) {
        if (buttons.isNullOrEmpty()) {
            throw IllegalArgumentException("You need to provide at least one button!")
        }
        if (buttons.count { it.purpose == ClickablePurpose.POSITIVE } > 1) {
            throw IllegalArgumentException("You can only provide one positive button!")
        }
        if (buttons.count { it.purpose == ClickablePurpose.NEGATIVE } > 1) {
            throw IllegalArgumentException("You can only provide one negative button!")
        }
    }

    /* This is only used until the deprecated "fixed-two-buttons" logic is removed. */
    private fun <T> dispatchButtonClick(clickable: DialogClickable<T>) {
        when (clickable.purpose) {
            ClickablePurpose.POSITIVE -> notifyButtonClickAndDismissIfNecessary(clickable)
            ClickablePurpose.NEGATIVE -> notifyButtonClickAndDismissIfNecessary(clickable)
            ClickablePurpose.NEUTRAL -> notifyButtonClickAndDismissIfNecessary(clickable)
        }
    }

    private fun notifyButtonClickAndDismissIfNecessary(clickable: DialogClickable<*>) {
        if (clickable.dismissAfterClick) dismiss()
        onButtonClicked(clickable)
    }

    private fun TextView.centerAlignIf(condition: Boolean) {
        textAlignment = if (condition) TextView.TEXT_ALIGNMENT_CENTER else TextView.TEXT_ALIGNMENT_TEXT_START
    }

    abstract class BaseDialogBuilder<Positive, Negative> {

        var title: String? = null
        var message: String? = null
        var orientation: DialogButtonOrientation? = null
        var positiveButton: DialogClickable<Positive>? = null
        var negativeButton: DialogClickable<Negative>? = null
        var titleCentered: Boolean = true
        var messageCentered: Boolean = true
        val neutralButtons = mutableListOf<DialogClickable<*>>()

        fun applyBundleToFragment(fragment: MBGenericDialogFragment<*>) {
            fragment.putBundle(title, message, orientation, titleCentered, messageCentered)
        }

        fun applyButtonsToFragment(fragment: MBGenericDialogFragment<*>) {
            val allButtons = mutableListOf<DialogClickable<*>>()
            positiveButton?.let { allButtons.add(it) }
            negativeButton?.let { allButtons.add(it) }
            allButtons.addAll(neutralButtons)
            fragment.dialogButtons = allButtons
        }
    }

    companion object {

        protected const val ARG_ID = "arg.dialog.id"
        protected const val ARG_TITLE = "arg.dialog.title"
        protected const val ARG_MESSAGE = "arg.dialog.message"
        protected const val ARG_NEGATIVE_BUTTON = "arg.dialog.button.negative"
        protected const val ARG_POSITIVE_BUTTON = "arg.dialog.button.positive"
        protected const val ARG_BUTTONS_ORIENTATION = "arg.dialog.buttons.orientation"
        protected const val ARG_TITLE_CENTERED = "arg.dialog.title.alignment"
        protected const val ARG_MESSAGE_CENTERED = "arg.dialog.message.alignment"

        protected const val DEFAULT_ID = -1

        private const val VERTICAL_ORIENTATION_THRESHOLD = 2
    }
}