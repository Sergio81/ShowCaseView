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

internal class Indicator(
        private val view: View,
        private val messageView: GuideMessageView
){
    // position and measures of the view

    // length of the line

    // position and measure of the message

    var offset = 0.5f
    private var init: PointF = PointF()
    private var final: PointF = PointF()
    private var long = 0

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

    var circleIndicatorSize = LINE_INDICATOR_WIDTH_SIZE * messageView.density
    var circleInnerIndicatorSize = 0f

    fun updatePosition(){
        var viewPosition = IntArray(2)

        view.getLocationOnScreen(viewPosition)

        init = getMessageMiddle()

        if(viewPosition.size == 2)
            final = getFinalPoint(viewPosition[0].toFloat(), viewPosition[1].toFloat())
    }

    fun draw(canvas: Canvas){
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

        // Line from the message to circle indicator
        canvas.drawLine(init.x, init.y, final.x, final.y, paintLine)
        // Indicator
        canvas.drawCircle(final.x, final.y, circleIndicatorSize, paintCircle)
        // Inner Indicator
        canvas.drawCircle(final.x, final.y, circleInnerIndicatorSize, paintCircleInner)
    }

    private fun getMessageMiddle():PointF =
        when(position){
            Position.Top -> PointF(
                    view.x + (view.width * offset),
                    messageView.y + messageView.height - MESSAGE_VIEW_PADDING)
            Position.Bottom -> PointF(
                    view.x + (view.width * offset),
                    messageView.y + MESSAGE_VIEW_PADDING)
            Position.Left -> PointF(
                    messageView.x + messageView.width - MESSAGE_VIEW_PADDING,
                    messageView.y + (messageView.height * offset))
            Position.Right -> PointF(
                    messageView.x + MESSAGE_VIEW_PADDING,
                    messageView.y + (messageView.height * offset))
            Position.Auto -> PointF(
                    messageView.x,
                    messageView.y)
        }

    private fun getFinalPoint(viewX:Float, viewY:Float):PointF =
            when(position){
                Position.Top -> PointF(
                        view.x + (view.width * offset),
                        viewY - (MARGIN_INDICATOR))
                Position.Bottom -> PointF(
                        view.x + (view.width * offset),
                         viewY + view.height + MARGIN_INDICATOR)
                        //messageView.y + messageHeight + long)
                Position.Left -> PointF(
                        viewX - (MARGIN_INDICATOR),
                        messageView.y + (messageView.height * offset))
                Position.Right -> PointF(
                        viewX + view.width + MARGIN_INDICATOR,
                        messageView.y + (messageView.height * offset))
                Position.Auto -> PointF(
                        messageView.x,
                        messageView.y)
            }
}
