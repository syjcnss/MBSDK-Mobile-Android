package com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons

/**
 * Interface representing a button of an dialog fragment that can be clicked by the user.
 */
internal interface DialogClickable<T> {

    /** The text of the button. */
    val title: String

    /** The meaning of the button. */
    val purpose: ClickablePurpose

    /** The style of the button. */
    val style: DialogButtonStyle

    /** True if the dialog fragment should be dismissed after the button was clicked. */
    val dismissAfterClick: Boolean

    /** An action to execute when the button was clicked. */
    val clickAction: ((T) -> Unit)?

    /** Specifies whether the clickable is enabled. */
    val enabled: Boolean
}