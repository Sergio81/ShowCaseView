package smartdevelop.ir.eram.showcaseviewlib

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Spannable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout

import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import smartdevelop.ir.eram.showcaseviewlib.utils.PointF
import smartdevelop.ir.eram.showcaseviewlib.utils.Position

import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.APPEARING_ANIMATION_DURATION
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.BACKGROUND_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.CIRCLE_INDICATOR_SIZE
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.INDICATOR_HEIGHT
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.MESSAGE_VIEW_PADDING
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.RADIUS_SIZE_TARGET_RECT
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.SIZE_ANIMATION_DURATION

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 * Updated by Sergio Fabian Aguilar Vega on 4/03/2019
 */

class GuideView private constructor(context: Context, private val target: View) : FrameLayout(context) {

    //region Global variables
    private val X_FER_MODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    private val selfPaint = Paint()
    private val selfRect = Rect()

    private var overrideX = 0f
    private var overrideY = 0f

    private var overrideTargetWidth = 0f
    private var overrideTargetHeight = 0f
    private val overrideTargetPosition = PointF(0f, 0f)

    internal val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var targetRect: RectF = createTargetRec(0, 0)

    private val density: Float = context.resources.displayMetrics.density
    var isShowing: Boolean = false
        private set

    private var circleIndicatorSizeFinal: Float = 0.toFloat()
    private var messageViewPadding: Int = 0
    private var indicatorHeight: Float = 0.toFloat()

    private var isPerformedAnimationSize = false

    private var mGuideListener: GuideListener? = null
    private var dismissType: DismissType? = null
    private var showSemitransparentBackground: Boolean? = true

    private var position = Position.Top
    internal var isChild: Boolean = false

    // Components
    private val mMessageView: GuideMessageView = GuideMessageView(context)
    private val indicator = Indicator()

    //endregion

    init {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        initVariables()
        setMessageView()

        target.viewTreeObserver.addOnGlobalLayoutListener { this.updateMeasures() }
    }

    private fun setMessageView() {
        mMessageView.setPadding(messageViewPadding, messageViewPadding, messageViewPadding, messageViewPadding)
        mMessageView.setColor(Color.WHITE)

        addView(mMessageView, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun initVariables() {
        indicatorHeight = INDICATOR_HEIGHT * density
        messageViewPadding = (MESSAGE_VIEW_PADDING * density).toInt()
        circleIndicatorSizeFinal = CIRCLE_INDICATOR_SIZE * density
        targetPaint.xfermode = X_FER_MODE_CLEAR
        targetPaint.isAntiAlias = true
    }

    private fun updateMeasures() {
        val locationTarget1 = IntArray(2)
        target.getLocationOnScreen(locationTarget1)

        targetRect = createTargetRec(locationTarget1[0], locationTarget1[1])

        selfRect.set(
                paddingLeft,
                paddingTop,
                width - paddingRight,
                height - paddingBottom)

        setMessageLocation(resolveMessageViewLocation())
        indicator.updatePosition(targetRect, mMessageView.mRect)
    }

    private fun createTargetRec(x: Int, y: Int): RectF {
        return RectF(
                x + overrideTargetPosition.x,
                y + overrideTargetPosition.y,
                x.toFloat() + target.width.toFloat() + overrideTargetWidth,
                y.toFloat() + target.height.toFloat() + overrideTargetHeight
        )
    }

    private fun startAnimationSize() {
        if (!isPerformedAnimationSize) {
            val circleSizeAnimator = ValueAnimator.ofFloat(0f, circleIndicatorSizeFinal)
            circleSizeAnimator.addUpdateListener {
                indicator.circleIndicatorSize = circleSizeAnimator.animatedValue as Float
                indicator.circleInnerIndicatorSize = circleSizeAnimator.animatedValue as Float - density
                postInvalidate()
            }

            val linePositionAnimator = ValueAnimator.ofFloat(indicator.getInitAnimation(), indicator.getFinalAnimation())
            linePositionAnimator.addUpdateListener {
                indicator.setCurrentAnimatedPosition(linePositionAnimator.animatedValue as Float)
                postInvalidate()
            }

            linePositionAnimator.duration = SIZE_ANIMATION_DURATION.toLong()
            indicator.locked = true
            linePositionAnimator.start()

            linePositionAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animator: Animator) {
                    circleSizeAnimator.duration = SIZE_ANIMATION_DURATION.toLong()
                    circleSizeAnimator.start()
                }

                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })

            circleSizeAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    isPerformedAnimationSize = false
                    indicator.locked = false
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Background Color
        if (showSemitransparentBackground!!) {
            selfPaint.color = BACKGROUND_COLOR
            selfPaint.style = Paint.Style.FILL
            selfPaint.isAntiAlias = true
            canvas.drawRect(selfRect, selfPaint)
        }

        indicator.draw(canvas)

        if (showSemitransparentBackground!!)
            canvas.drawRoundRect(targetRect, RADIUS_SIZE_TARGET_RECT.toFloat(), RADIUS_SIZE_TARGET_RECT.toFloat(), targetPaint)
    }

    fun dismiss() {
        ((context as Activity).window.decorView as ViewGroup).removeView(this)
        isShowing = false
        if (mGuideListener != null) {
            mGuideListener!!.onDismiss(target)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        var success = false

        //if (!isChild)
        if (event.action == MotionEvent.ACTION_DOWN) {
            when (dismissType) {
                DismissType.Outside -> if (!isViewContains(mMessageView, x, y)) {
                    dismiss()
                    if (!isChild) success = true
                }

                DismissType.Message -> if (isViewContains(mMessageView, x, y)) {
                    dismiss()
                    success = true
                }

                DismissType.Anywhere -> {
                    dismiss()
                    if (!isChild) success = true
                }

                DismissType.TargetView -> if (targetRect.contains(x, y)) {
                    target.performClick()
                    dismiss()
                    if (!isChild) success = true
                }
            }
            return success
        }
        return false
    }

    private fun isViewContains(view: View, rx: Float, ry: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        val w = view.width
        val h = view.height

        return !(rx < x || rx > x + w || ry < y || ry > y + h)
    }

    private fun setMessageLocation(p: Point) {
        mMessageView.updatePosition(p.x.toFloat(), p.y.toFloat())
        postInvalidate()
    }

    private fun resolveMessageViewLocation(): Point = when (position) {
        Position.Top -> Point(
                (targetRect.left - mMessageView.width.toFloat() / 2 + targetRect.width() / 2 + overrideX).toInt(),
                (targetRect.top - indicatorHeight - mMessageView.height.toFloat() + overrideY).toInt())
        Position.Bottom -> Point(
                (targetRect.left - mMessageView.width.toFloat() / 2 + targetRect.width() / 2 + overrideX).toInt(),
                (targetRect.top + targetRect.height() + indicatorHeight + overrideY).toInt())
        Position.Left -> Point(
                (targetRect.left - mMessageView.width.toFloat() - indicatorHeight + overrideX).toInt(),
                (targetRect.top + targetRect.height() / 2 - mMessageView.height / 2 + overrideY).toInt())
        Position.Right -> Point(
                (targetRect.right + indicatorHeight + overrideX).toInt(),
                (targetRect.top + targetRect.height() / 2 - mMessageView.height / 2 + overrideY).toInt())
    }

    fun show() {
        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.isClickable = false

        ((context as Activity).window.decorView as ViewGroup).addView(this)

        val startAnimation = AlphaAnimation(0.0f, 1.0f)
        startAnimation.duration = APPEARING_ANIMATION_DURATION.toLong()
        startAnimation.fillAfter = true
        this.startAnimation(startAnimation)

        startAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                startAnimationSize()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        isShowing = true
    }

    //region Setters
    fun setTitle(str: String?) {
        mMessageView.setTitle(str)
    }

    fun setPosition(position: Position) {
        this.position = position
        indicator.position = position
    }

    fun setContentText(str: String) {
        mMessageView.setContentText(str)
    }

    fun setContentSpan(span: Spannable) {
        mMessageView.setContentSpan(span)
    }

    fun setTitleTypeFace(typeFace: Typeface) {
        mMessageView.setTitleTypeFace(typeFace)
    }

    fun setContentTypeFace(typeFace: Typeface) {
        mMessageView.setContentTypeFace(typeFace)
    }

    fun setTitleTextSize(size: Int) {
        mMessageView.setTitleTextSize(size)
    }

    fun setContentTextSize(size: Int) {
        mMessageView.setContentTextSize(size)
    }

    fun setSemitransparentBackground(show: Boolean?) {
        showSemitransparentBackground = show
    }

    fun setDismissType(dismissType: DismissType) {
        this.dismissType = dismissType
    }
    //endregion

    class Builder(private val context: Context) {
        private var targetView: View? = null
        private var title: String? = null
        private var contentText: String? = null
        private var gravity: Gravity? = null
        private var dismissType: DismissType? = null
        private var contentSpan: Spannable? = null
        private var titleTypeFace: Typeface? = null
        private var contentTypeFace: Typeface? = null
        private var guideListener: GuideListener? = null
        private var titleTextSize: Int = 0
        private var contentTextSize: Int = 0
        private var lineIndicatorHeight: Float = 0.toFloat()
        private var lineIndicatorWidthSize: Float = 0.toFloat()
        private var circleIndicatorSize: Float = 0.toFloat()
        private var circleInnerIndicatorSize: Float = 0.toFloat()
        private var strokeCircleWidth: Float = 0.toFloat()
        private var showSemitransparentBackground: Boolean? = true
        private var position = Position.Top
        private var overrideX = 0f
        private var overrideY = 0f

        private var overrideTargetWidth = 0f
        private var overrideTargetHeight = 0f
        private val overrideTargetPosition = PointF(0f, 0f)

        //region setters
        fun setTargetView(view: View): Builder {
            this.targetView = view
            return this
        }

        /**
         * gravity GuideView
         *
         * @param gravity it should be one type of Gravity enum.
         */
        fun setGravity(gravity: Gravity): Builder {
            this.gravity = gravity
            return this
        }

        /**
         * defining a title
         *
         * @param title a title. for example: submit button.
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * defining a description for the target view
         *
         * @param contentText a description. for example: this button can for submit your information..
         */
        fun setContentText(contentText: String): Builder {
            this.contentText = contentText
            return this
        }

        /**
         * setting spannable type
         *
         * @param span a instance of spannable
         */
        fun setContentSpan(span: Spannable): Builder {
            this.contentSpan = span
            return this
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         */
        fun setContentTypeFace(typeFace: Typeface): Builder {
            this.contentTypeFace = typeFace
            return this
        }

        /**
         * adding a listener on show case view
         *
         * @param guideListener a listener for events
         */
        fun setGuideListener(guideListener: GuideListener): Builder {
            this.guideListener = guideListener
            return this
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         */
        fun setTitleTypeFace(typeFace: Typeface): Builder {
            this.titleTypeFace = typeFace
            return this
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        fun setContentTextSize(size: Int): Builder {
            this.contentTextSize = size
            return this
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        fun setTitleTextSize(size: Int): Builder {
            this.titleTextSize = size
            return this
        }

        /**
         * this method defining the type of dismissing function
         *
         * @param dismissType should be one type of DismissType enum. for example: Outside -> Dismissing with click on Outside of MessageView
         */
        fun setDismissType(dismissType: DismissType): Builder {
            this.dismissType = dismissType
            return this
        }

        /**
         * changing line height indicator
         *
         * @param height you can change height indicator (Converting to Dp)
         */
        fun setIndicatorHeight(height: Float): Builder {
            this.lineIndicatorHeight = height
            return this
        }

        /**
         * changing line long indicator
         *
         * @param width you can change long indicator
         */
        fun setIndicatorWidthSize(width: Float): Builder {
            this.lineIndicatorWidthSize = width
            return this
        }

        /**
         * changing circle size indicator
         *
         * @param size you can change circle size indicator
         */
        fun setCircleIndicatorSize(size: Float): Builder {
            this.circleIndicatorSize = size
            return this
        }

        /**
         * changing inner circle size indicator
         *
         * @param size you can change inner circle indicator size
         */
        fun setCircleInnerIndicatorSize(size: Float): Builder {
            this.circleInnerIndicatorSize = size
            return this
        }

        /**
         * changing stroke circle size indicator
         *
         * @param size you can change stroke circle indicator size
         */
        fun setCircleStrokeIndicatorSize(size: Float): Builder {
            this.strokeCircleWidth = size
            return this
        }

        fun setSemitransparentBackground(show: Boolean?): Builder {
            this.showSemitransparentBackground = show
            return this
        }

        fun setPosition(position: Position): Builder {
            this.position = position
            return this
        }

        fun overrideXMessage(x: Float): Builder {
            overrideX = x
            return this
        }

        fun overrideYMessage(y: Float): Builder {
            overrideY = y
            return this
        }

        fun overrideTargetWidth(w: Float): Builder {
            overrideTargetWidth = w
            return this
        }

        fun overrideTargetHeight(h: Float): Builder {
            overrideTargetHeight = h
            return this
        }

        fun overrideXTarget(x: Float): Builder {
            overrideTargetPosition.x = x
            return this
        }

        fun overrideYTarget(y: Float): Builder {
            overrideTargetPosition.y = y
            return this
        }
        //endregion

        fun build(): GuideView {
            val guideView = GuideView(context, targetView!!)
            guideView.dismissType = if (dismissType != null) dismissType else DismissType.TargetView
            guideView.showSemitransparentBackground = this.showSemitransparentBackground
            guideView.overrideX = this.overrideX
            guideView.overrideY = this.overrideY
            guideView.overrideTargetHeight = this.overrideTargetHeight
            guideView.overrideTargetWidth = this.overrideTargetWidth

            guideView.overrideTargetPosition.x = this.overrideTargetPosition.x
            guideView.overrideTargetPosition.y = this.overrideTargetPosition.y

            val density = context.resources.displayMetrics.density

            guideView.setPosition(this.position)
            guideView.setTitle(title)

            if (contentText != null)
                guideView.setContentText(contentText!!)
            if (titleTextSize != 0)
                guideView.setTitleTextSize(titleTextSize)
            if (contentTextSize != 0)
                guideView.setContentTextSize(contentTextSize)
            if (contentSpan != null)
                guideView.setContentSpan(contentSpan!!)
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace!!)
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace!!)
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener
            }
            if (lineIndicatorHeight != 0f) {
                guideView.indicatorHeight = lineIndicatorHeight * density
            }
            //            if (lineIndicatorWidthSize != 0) {
            //                guideView.lineIndicatorWidthSize = lineIndicatorWidthSize * density;
            //            }
            //            if (circleIndicatorSize != 0) {
            //                guideView.circleIndicatorSize = circleIndicatorSize * density;
            //            }
            //            if (circleInnerIndicatorSize != 0) {
            //                guideView.circleInnerIndicatorSize = circleInnerIndicatorSize * density;
            //            }
            //            if (strokeCircleWidth != 0) {
            //                guideView.strokeCircleWidth = strokeCircleWidth * density;
            //            }

            return guideView
        }


    }
}

