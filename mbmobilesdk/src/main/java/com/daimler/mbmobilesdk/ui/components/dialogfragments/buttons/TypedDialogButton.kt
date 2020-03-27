package com.daimler.mbmobilesdk.ui.components.dialogfragments.buttons

internal data class TypedDialogButton<T>(
    override val title: String,
    override val purpose: ClickablePurpose,
    override val style: DialogButtonStyle,
    override val dismissAfterClick: Boolean,
    override val clickAction: ((T) -> Unit)? = null,
    override val enabled: Boolean = true
) : DialogClickable<T>