package com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.daimler.mbmobilesdk.ui.widgets.buttons.MBPrimaryTextButton
import com.daimler.mbmobilesdk.ui.widgets.buttons.MBSecondaryTextButton

/**
 * ViewGroup responsible to show a group of buttons for the usage in dialog fragments.
 *
 * @see DialogButtonOrientation
 * @see DialogClickable
 * @see ButtonGroupListener
 */
internal class DialogFragmentButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var orientation: DialogButtonOrientation = DialogButtonOrientation.HORIZONTAL
    private val buttons = mutableListOf<DialogClickable<*>>()
    private var listener: ButtonGroupListener? = null
    private var delegate: ButtonGroupDelegate = getDelegate(orientation)

    fun setOrientation(orientation: DialogButtonOrientation?) {
        orientation?.takeIf {
            this.orientation != it
        }?.let {
            this.delegate = getDelegate(it)
            if (buttons.isNotEmpty()) sortButtons()
            applyButtons()
        }
    }

    fun setButtons(buttons: List<DialogClickable<*>>) {
        this.buttons.clear()
        this.buttons.addAll(delegate.sortButtons(buttons))
        applyButtons()
    }

    fun setListener(listener: ButtonGroupListener?) {
        this.listener = listener
    }

    fun updateButtonEnabledState(purpose: ClickablePurpose, enabled: Boolean) {
        buttons.filter {
            it.purpose == purpose
        }.forEach {
            findViewWithTag<View>(generateButtonViewTag(it))?.isEnabled = enabled
        }
    }

    private fun generateButtonViewTag(clickable: DialogClickable<*>): Int =
        clickable.hashCode()

    private fun getDelegate(orientation: DialogButtonOrientation) =
        when (orientation) {
            DialogButtonOrientation.HORIZONTAL -> HorizontalButtonGroupDelegate()
            DialogButtonOrientation.VERTICAL -> VerticalButtonGroupDelegate()
        }

    private fun sortButtons() {
        val sorted = delegate.sortButtons(buttons)
        buttons.clear()
        buttons.addAll(sorted)
    }

    private fun applyButtons() {
        removeAllViews()
        val views = buttons.map { createButtonView(it) }
        delegate.addViews(views, this)
        listener?.onButtonsInitialized()
    }

    private fun <T> createButtonView(clickable: DialogClickable<T>): View {
        val view = if (clickable.style == DialogButtonStyle.PRIMARY) {
            MBPrimaryTextButton(context)
        } else {
            MBSecondaryTextButton(context)
        }
        return view.apply {
            tag = generateButtonViewTag(clickable)
            text = clickable.title
            isEnabled = clickable.enabled
            setOnClickListener {
                listener?.onButtonClicked(clickable)
            }
        }
    }

    /* Delegate */

    private interface ButtonGroupDelegate {
        fun sortButtons(buttons: List<DialogClickable<*>>): List<DialogClickable<*>>
        fun addViews(views: List<View>, container: LinearLayout)
    }

    private abstract class BaseButtonGroupDelegate : ButtonGroupDelegate {
        abstract val comparator: Comparator<DialogClickable<*>>
        abstract val coordinator: ButtonCoordinator

        override fun sortButtons(buttons: List<DialogClickable<*>>): List<DialogClickable<*>> {
            return buttons.sortedWith(comparator)
        }

        override fun addViews(views: List<View>, container: LinearLayout) {
            return coordinator.addButtons(views, container)
        }
    }

    private class HorizontalButtonGroupDelegate : BaseButtonGroupDelegate() {
        override val comparator: Comparator<DialogClickable<*>> = HorizontalDialogClickableComparator()
        override val coordinator: ButtonCoordinator = HorizontalButtonCoordinator()
    }

    private class VerticalButtonGroupDelegate : BaseButtonGroupDelegate() {
        override val comparator: Comparator<DialogClickable<*>> = VerticalDialogClickableComparator()
        override val coordinator: ButtonCoordinator = VerticalButtonCoordinator()
    }

    /* Coordinator */

    private abstract class ButtonCoordinator {

        abstract fun addButtons(buttons: List<View>, container: LinearLayout)
    }

    private class HorizontalButtonCoordinator : ButtonCoordinator() {

        override fun addButtons(buttons: List<View>, container: LinearLayout) {
            container.apply {
                orientation = HORIZONTAL
                weightSum = minOf(buttons.size, MAX_HORIZONTAL_WEIGHT_SUM).toFloat()
                val params = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                buttons.forEach {
                    it.layoutParams = params
                    addView(it)
                }
            }
        }
    }

    private class VerticalButtonCoordinator : ButtonCoordinator() {

        override fun addButtons(buttons: List<View>, container: LinearLayout) {
            container.apply {
                orientation = VERTICAL
                buttons.forEach {
                    addView(it)
                }
            }
        }
    }

    /* Comparator */

    private abstract class DialogClickableComparator : Comparator<DialogClickable<*>> {

        protected abstract val order: List<ClickablePurpose>

        override fun compare(o1: DialogClickable<*>, o2: DialogClickable<*>): Int {
            return order.indexOf(o1.purpose) - order.indexOf(o2.purpose)
        }
    }

    private class HorizontalDialogClickableComparator : DialogClickableComparator() {

        override val order: List<ClickablePurpose> = listOf(
            ClickablePurpose.NEUTRAL,
            ClickablePurpose.NEGATIVE,
            ClickablePurpose.POSITIVE
        )
    }

    private class VerticalDialogClickableComparator : DialogClickableComparator() {

        override val order: List<ClickablePurpose> = listOf(
            ClickablePurpose.POSITIVE,
            ClickablePurpose.NEGATIVE,
            ClickablePurpose.NEUTRAL
        )
    }

    /* -- */

    /**
     * Listener that gets notified when a button from this view got clicked.
     */
    interface ButtonGroupListener {

        /**
         * Called when the given [clickable] was clicked by the user.
         */
        fun onButtonClicked(clickable: DialogClickable<*>)

        /**
         * Called when the buttons are set and added.
         */
        fun onButtonsInitialized()
    }

    private companion object {
        private const val MAX_HORIZONTAL_WEIGHT_SUM = 2
    }
}