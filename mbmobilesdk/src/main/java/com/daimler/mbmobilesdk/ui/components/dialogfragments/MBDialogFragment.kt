package com.daimler.mbmobilesdk.ui.components.dialogfragments

import com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons.*
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBGenericDialogViewModel

/**
 * Simple dialog fragment that can show a title, message and multiple buttons.
 *
 * ```
 *  class MyActivity : MBBaseViewModelActivity<MyViewModel>() {
 *
 *      // ...
 *
 *      fun showDialog() {
 *          MBDialogFragment.Builder().apply {
 *              withTitle("MyTitle")
 *              withMessage("MyMessage")
 *              withPositiveButton("OK") { toast("Okay") }
 *              withNegativeButton("Cancel") { toast("Cancel") }
 *              addNeutralButton("Neutral Button")
 *          }.build()
 *          .show(supportFragmentManager, null)
 *      }
 *  }
 * ```
 */
internal class MBDialogFragment : MBGenericDialogFragment<MBGenericDialogViewModel>() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> onButtonClicked(button: DialogClickable<T>) {
        when (button.purpose) {
            ClickablePurpose.POSITIVE -> button.invokeClickAction(Unit)
            ClickablePurpose.NEGATIVE -> button.invokeClickAction(Unit)
            ClickablePurpose.NEUTRAL -> button.invokeClickAction(Unit)
        }
    }

    class Builder : BaseDialogBuilder<Unit, Unit>() {

        fun withTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun withMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun withOrientation(orientation: DialogButtonOrientation): Builder {
            this.orientation = orientation
            return this
        }

        fun addNeutralButton(title: String, clickAction: ((Unit) -> Unit)? = null): Builder {
            neutralButtons.add(TypedDialogButton(title, ClickablePurpose.NEUTRAL, DialogButtonStyle.SECONDARY, true, clickAction))
            return this
        }

        fun withPositiveButton(title: String, clickAction: ((Unit) -> Unit)? = null): Builder {
            positiveButton = TypedDialogButton(title, ClickablePurpose.POSITIVE, DialogButtonStyle.PRIMARY, true, clickAction)
            return this
        }

        fun withNegativeButton(title: String, clickAction: ((Unit) -> Unit)? = null): Builder {
            negativeButton = TypedDialogButton(title, ClickablePurpose.NEGATIVE, DialogButtonStyle.SECONDARY, true, clickAction)
            return this
        }

        fun build(): MBDialogFragment {
            return MBDialogFragment().apply {
                applyBundleToFragment(this)
                applyButtonsToFragment(this)
            }
        }
    }
}