package smartdevelop.ir.eram.showcaseviewlib

internal class Indicator(private val messageView: GuideMessageView){
    var offset = 0.5f
    var init: PointF = getMessageMiddle()
    var final: PointF = getFinalPoint()
    var long = 100

    var position = Position.Auto
    set(value) {
        field = value
        init = getMessageMiddle()
        final = getFinalPoint()
    }

    private fun getMessageMiddle():PointF =
        when(position){
            Position.Top -> PointF(
                    messageView.x + (messageView.width * offset),
                    messageView.y)
            Position.Bottom -> PointF(
                    messageView.x + (messageView.width * offset),
                    messageView.y + messageView.height)
            Position.Left -> PointF(
                    messageView.x,
                    messageView.y + (messageView.height * offset))
            Position.Right -> PointF(
                    messageView.x + messageView.width,
                    messageView.y + (messageView.height * offset))
            Position.Auto -> PointF(
                    messageView.x,
                    messageView.y)
        }

    private fun getFinalPoint():PointF =
            when(position){
                Position.Top -> PointF(
                        messageView.x + (messageView.width * offset),
                        messageView.y - long)
                Position.Bottom -> PointF(
                        messageView.x + (messageView.width * offset),
                        messageView.y + messageView.height + long)
                Position.Left -> PointF(
                        messageView.x - long,
                        messageView.y + (messageView.height * offset))
                Position.Right -> PointF(
                        messageView.x + messageView.width + long,
                        messageView.y + (messageView.height * offset))
                Position.Auto -> PointF(
                        messageView.x,
                        messageView.y)
            }
}
