package com.daimler.mbmobilesdk.ui.widgets.buttons

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import com.daimler.mbmobilesdk.R
import kotlin.math.roundToInt

/**
 * Primary button widget with rounded corners.
 *
 * @see R.style.MBPrimaryButtonStyle
 */
internal open class MBPrimaryButton : Button {

    private val paint = Paint()
    private val path = Path()

    private val startReflexColor by lazy {
        ContextCompat.getColor(context, R.color.mb_button_primary_enabled_reflex_gradient_color_start)
    }

    private val startBackgroundColor by lazy {
        ContextCompat.getColor(context, R.color.mb_button_primary_background_gradient_color_start)
    }

    private val endColor by lazy {
        ContextCompat.getColor(context, R.color.mb_button_primary_gradient_color_end)
    }

    private val reflexIndent by lazy { dpToPx(reflexIndentDp) }

    private val reflexGradient by lazy {
        RadialGradient(
            centerTopPointRelative.x,
            centerTopPointRelative.y,
            0.8f,
            startReflexColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }

    private val backgroundGradient by lazy {
        RadialGradient(
            0f,
            0f,
            1f,
            startBackgroundColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }

    constructor(context: Context) :
        super(context, null, 0, R.style.MBPrimaryButtonStyle)

    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs, 0, R.style.MBPrimaryButtonStyle)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        paint.reset()
        drawReflex(canvas)
        drawBackground(canvas)
    }

    private fun drawReflex(canvas: Canvas?) {
        path.moveTo(startPointRelative.x * width, startPointRelative.y * height)
        path.lineTo(centerTopPointRelative.x * width + reflexIndent, centerTopPointRelative.y * height)
        path.lineTo(centerBottomPointRelative.x * width - reflexIndent, centerBottomPointRelative.y * height)
        path.lineTo(endPointRelative.x * width, endPointRelative.y * height)
        path.close()

        val localGradientMatrix = Matrix()
        reflexGradient.getLocalMatrix(localGradientMatrix)
        localGradientMatrix.setRotate(reflexGradientAngle, 0.5f, 0.5f)
        localGradientMatrix.setScale(width.toFloat(), height.toFloat())
        reflexGradient.setLocalMatrix(localGradientMatrix)

        paint.shader = reflexGradient
        updateAlpha()

        canvas?.drawPath(path, paint)
    }

    private fun drawBackground(canvas: Canvas?) {
        paint.shader = backgroundGradient
        updateAlpha()

        canvas?.save()
        canvas?.translate(width.toFloat() / 2, height.toFloat())
        canvas?.scale(width.toFloat(), height.toFloat() / 1.85f)
        canvas?.drawPaint(paint)
        canvas?.restore()
        path.reset()
        paint.reset()
    }

    private fun updateAlpha() {
        if (isEnabled) paint.alpha = enabledAlpha else paint.alpha = disabledAlpha
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    companion object {
        private val startPointRelative = PointF(0f, 0f)
        private val centerTopPointRelative = PointF(0.5f, 0f)
        private val centerBottomPointRelative = PointF(0.5f, 1f)
        private val endPointRelative = PointF(0f, 1f)

        private const val reflexIndentDp = 9 // dp
        private const val reflexGradientAngle = 20f
        private const val enabledAlpha = 255
        private const val disabledAlpha = 127
    }
}