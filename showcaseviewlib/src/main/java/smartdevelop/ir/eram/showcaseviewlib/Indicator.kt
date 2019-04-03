package smartdevelop.ir.eram.showcaseviewlib

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.CIRCLE_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.CIRCLE_INNER_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.LINE_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.LINE_INDICATOR_WIDTH_SIZE
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.MARGIN_INDICATOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.MESSAGE_VIEW_PADDING
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.STROKE_CIRCLE_INDICATOR_SIZE
import smartdevelop.ir.eram.showcaseviewlib.utils.PointF
import smartdevelop.ir.eram.showcaseviewlib.utils.Position

/**
 * Created by Sergio Fabian Aguilar Vega on 4/03/2019
 */

internal class Indicator(
        private val view: View,
        private val messageView: GuideMessageView
) {
    var offset = 0.5f
    var init: PointF = PointF()
    var final: PointF = PointF()
    private var currentFinalPosition = PointF()

    var position = Position.Bottom
        set(value) {
            field = value
            updatePosition()
        }

    private val paintLine = Paint()
    private val paintCircle = Paint()
    private val paintCircleInner = Paint()

    private var lineIndicatorWidthSize: Float = LINE_INDICATOR_WIDTH_SIZE * messageView.density
    private var strokeCircleWidth: Float = STROKE_CIRCLE_INDICATOR_SIZE * messageView.density

    var circleIndicatorSize = 0f
    var circleInnerIndicatorSize = 0f
    var locked = false

    fun updatePosition() {
        val viewPosition = IntArray(2)

        view.getLocationOnScreen(viewPosition)

        if (viewPosition.size == 2) {
            init = getInitialPoint(viewPosition[0].toFloat(), viewPosition[1].toFloat())
            final = getFinalPoint(viewPosition[0].toFloat(), viewPosition[1].toFloat())
            if (!locked) {
                currentFinalPosition.x = init.x
                currentFinalPosition.y = init.y
            }
        }
    }

    fun draw(canvas: Canvas) {
        // Line Indicator
        paintLine.style = Paint.Style.FILL
        paintLine.color = LINE_INDICATOR_COLOR
        paintLine.strokeWidth = lineIndicatorWidthSize
        paintLine.isAntiAlias = true

        // Circle Indicator
        paintCircle.style = Paint.Style.STROKE
        paintCircle.color = CIRCLE_INDICATOR_COLOR
        paintCircle.strokeCap = Paint.Cap.ROUND
        paintCircle.strokeWidth = strokeCircleWidth
        paintCircle.isAntiAlias = true

        // Inner Circle Indicator
        paintCircleInner.style = Paint.Style.FILL
        paintCircleInner.color = CIRCLE_INNER_INDICATOR_COLOR
        paintCircleInner.isAntiAlias = true


        //canvas.drawLine(init.x, init.y, final.x, final.y, paintLine)
        when (position) {
            Position.Top, Position.Bottom ->
                currentFinalPosition.x = final.x
            Position.Left, Position.Right ->
                currentFinalPosition.y = final.y
        }

        // Line from the message to circle indicator
        canvas.drawLine(init.x, init.y, currentFinalPosition.x, currentFinalPosition.y, paintLine)
        // Indicator
        canvas.drawCircle(currentFinalPosition.x, currentFinalPosition.y, circleIndicatorSize, paintCircle)
        // Inner Indicator
        canvas.drawCircle(currentFinalPosition.x, currentFinalPosition.y, circleInnerIndicatorSize, paintCircleInner)
    }

    fun setCurrentAnimatedPosition(newPosition: Float) {
        when (position) {
            Position.Top, Position.Bottom ->
                currentFinalPosition.y = newPosition
            Position.Left, Position.Right ->
                currentFinalPosition.x = newPosition
        }
    }

    fun getInitAnimation(): Float =
            when (position) {
                Position.Top, Position.Bottom -> init.y
                Position.Left, Position.Right -> init.x
            }

    fun getFinalAnimation(): Float =
            when (position) {
                Position.Top, Position.Bottom -> final.y
                Position.Left, Position.Right -> final.x
            }

    private fun getOverflow(viewX: Float, viewY: Float): Float {
        val centerView = PointF(
                view.width / 2f,
                view.height / 2f
        )
        val tolerance = 0.3f
        val centerMessagePoint = PointF(
                messageView.x + (messageView.width / 2f),
                messageView.y + (messageView.height / 2f)
        )
        val centerViewPoint = PointF(
                viewX + (view.width / 2f),
                viewY + (view.height / 2f)
        )

        val viewTolerance = PointF(
                view.width * tolerance,
                view.height * tolerance
        )

        return when (position) {
            Position.Top,
            Position.Bottom -> when {
                centerMessagePoint.x in centerViewPoint.x - viewTolerance.x..centerViewPoint.x + viewTolerance.x ->
                    0f // middle
                centerMessagePoint.x in 0f..centerViewPoint.x ->
                    -(centerView.x - 20) // left
                else ->
                    centerView.x - 20 // right
            }
            Position.Left,
            Position.Right -> when {
                centerMessagePoint.y in centerViewPoint.y - viewTolerance.y..centerViewPoint.y + viewTolerance.y ->
                    0f // middle
                centerMessagePoint.y in 0f..centerViewPoint.y ->
                    -(centerView.y - 20) // top
                else ->
                    centerView.y - 10 // bottom
            }
        }
    }

    private fun getInitialPoint(viewX: Float, viewY: Float): PointF =
            when (position) {
                Position.Top -> PointF(
                        view.x + (view.width * offset) + getOverflow(viewX, viewY),
                        messageView.y + messageView.height - MESSAGE_VIEW_PADDING)
                Position.Bottom -> PointF(
                        view.x + (view.width * offset) + getOverflow(viewX, viewY),
                        messageView.y + MESSAGE_VIEW_PADDING)
                Position.Left -> PointF(
                        messageView.x + messageView.width - MESSAGE_VIEW_PADDING,
                        viewY + (view.height * offset) + getOverflow(viewX, viewY))
                Position.Right -> PointF(
                        messageView.x + MESSAGE_VIEW_PADDING,
                        viewY + (view.height * offset) + getOverflow(viewX, viewY))
            }

    private fun getFinalPoint(viewX: Float, viewY: Float): PointF {
        return when (position) {
            Position.Top -> PointF(
                    view.x + (view.width * offset) + getOverflow(viewX, viewY),
                    viewY - (MARGIN_INDICATOR))
            Position.Bottom -> PointF(
                    view.x + (view.width * offset) + getOverflow(viewX, viewY),
                    viewY + view.height + MARGIN_INDICATOR)
            Position.Left -> PointF(
                    viewX - (MARGIN_INDICATOR),
                    viewY + (view.height * offset) + getOverflow(viewX, viewY))
            Position.Right -> PointF(
                    viewX + view.width + MARGIN_INDICATOR,
                    viewY + (view.height * offset) + getOverflow(viewX, viewY))
        }
    }

}
