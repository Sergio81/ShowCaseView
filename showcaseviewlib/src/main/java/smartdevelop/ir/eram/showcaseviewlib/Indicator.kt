package smartdevelop.ir.eram.showcaseviewlib

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.CIRCLE_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.CIRCLE_INNER_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.LINE_INDICATOR_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.LINE_INDICATOR_WIDTH_SIZE
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.MARGIN_INDICATOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.STROKE_CIRCLE_INDICATOR_SIZE
import smartdevelop.ir.eram.showcaseviewlib.utils.PointF
import smartdevelop.ir.eram.showcaseviewlib.utils.Position

/**
 * Created by Sergio Fabian Aguilar Vega on 4/03/2019
 */

internal class Indicator {
    //region Properties
    private val init: PointF
        get() = getInitialPoint()
    private val final: PointF
        get() = getFinalPoint()

    var circleIndicatorSize = 0f
    var circleInnerIndicatorSize = 0f
    var locked = false

    var position = Position.Bottom
    //endregion

    //region Private Variables
    private var viewRect: RectF = RectF(0f, 0f, 0f, 0f)
    private var messageRect: RectF = RectF(0f, 0f, 0f, 0f)

    private var offset = 0.5f
    private var currentFinalPosition = PointF()

    private val paintLine = Paint()
    private val paintCircle = Paint()
    private val paintCircleInner = Paint()

    private var lineIndicatorWidthSize: Float = 0f
    private var strokeCircleWidth: Float = 0f
    //endregion

    init {
        initVariables()
    }

    private fun initVariables() {
        lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE.toFloat() // * messageView!!.density
        strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE.toFloat() // * messageView!!.density
    }

    fun updatePosition(targetRect: RectF, messageRect: RectF) {
        this.viewRect.set(targetRect)
        this.messageRect.set(messageRect)

        if (!locked) {
            currentFinalPosition.x = init.x
            currentFinalPosition.y = init.y
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

        // Line from the Message to circle indicator
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

    fun getInitAnimation(): Float = when (position) {
        Position.Top, Position.Bottom -> init.y
        Position.Left, Position.Right -> init.x
    }

    fun getFinalAnimation(): Float = when (position) {
        Position.Top, Position.Bottom -> final.y
        Position.Left, Position.Right -> final.x
    }

    private fun getOverflow(): Float {
        val centerView = PointF(
                viewRect.width() / 2f,
                viewRect.height() / 2f
        )
        val tolerance = 0.3f
        val centerMessagePoint = PointF(
                messageRect.left + (messageRect.width() / 2f),
                messageRect.top + (messageRect.height() / 2f)
        )
        val centerViewPoint = PointF(
                viewRect.left + (viewRect.width() / 2f),
                viewRect.top + (viewRect.height() / 2f)
        )
        val viewTolerance = PointF(
                viewRect.width() * tolerance,
                viewRect.height() * tolerance
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

    private fun getInitialPoint(): PointF {
        val marginSpace = 10
        return when (position) {
            Position.Top -> PointF(
                    viewRect.left + (viewRect.width() * offset) + getOverflow(),
                    messageRect.bottom - marginSpace)
            Position.Bottom -> PointF(
                    viewRect.left + (viewRect.width() * offset) + getOverflow(),
                    messageRect.top + marginSpace)
            Position.Left -> PointF(
                    messageRect.right - marginSpace,
                    viewRect.top + (viewRect.height() * offset) + getOverflow())
            Position.Right -> PointF(
                    messageRect.left + marginSpace,
                    viewRect.top + (viewRect.height() * offset) + getOverflow())
        }
    }

    private fun getFinalPoint(): PointF {
        return when (position) {
            Position.Top -> PointF(
                    viewRect.left + (viewRect.width() * offset) + getOverflow(),
                    viewRect.top - (MARGIN_INDICATOR))
            Position.Bottom -> PointF(
                    viewRect.left + (viewRect.width() * offset) + getOverflow(),
                    viewRect.top + viewRect.height() + MARGIN_INDICATOR)
            Position.Left -> PointF(
                    viewRect.left - (MARGIN_INDICATOR),
                    viewRect.top + (viewRect.height() * offset) + getOverflow())
            Position.Right -> PointF(
                    viewRect.right + (MARGIN_INDICATOR),
                    viewRect.top + (viewRect.height() * offset) + getOverflow())
        }
    }

}
