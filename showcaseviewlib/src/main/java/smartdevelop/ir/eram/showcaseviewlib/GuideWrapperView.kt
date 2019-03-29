package smartdevelop.ir.eram.showcaseviewlib

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.APPEARING_ANIMATION_DURATION
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.BACKGROUND_COLOR
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.RADIUS_SIZE_TARGET_RECT
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType

class GuideWrapperView(context: Context) : FrameLayout(context) {
    private val selfPaint = Paint()
    private val selfRect = Rect()
    private var mIsShowing: Boolean = true
    private val guideViews = ArrayList<GuideView>()

    init {
        setWillNotDraw(false)
        // this allow X_FER_MODE_CLEAR works
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        viewTreeObserver.removeOnPreDrawListener(this)

                        selfRect.set(paddingLeft,
                                paddingTop,
                                width - paddingRight,
                                height - paddingBottom)

                        return true
                    }
                })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        selfPaint.color = BACKGROUND_COLOR
        selfPaint.style = Paint.Style.FILL
        selfPaint.isAntiAlias = true
        canvas.drawRect(selfRect, selfPaint)

        // Show the children views without the background
        for(g:GuideView in guideViews){
            canvas.drawRoundRect(
                    g.targetRect,
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    g.targetPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            for(g:GuideView in guideViews)
                g.dismiss()
            dismiss()
            return true
        }
        return false
    }

    fun dismiss() {
        ((context as Activity).window.decorView as ViewGroup).removeView(this)
        mIsShowing = false
    }

    fun isShowing(): Boolean {
        return mIsShowing
    }

    fun show() {
        val startAnimation = AlphaAnimation(0.0f, 1.0f)

        this.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)

        this.isClickable = false

        ((context as Activity).window.decorView as ViewGroup).addView(this)

        startAnimation.duration = APPEARING_ANIMATION_DURATION.toLong()
        startAnimation.fillAfter = true

        this.startAnimation(startAnimation)

        for(g:GuideView in guideViews){
            g.setSemitransparentBackground(false)
            g.setDismissType(DismissType.message)
            g.isChild = true
            g.show()
        }

        mIsShowing = true
    }

    class Builder(private val context: Context) {
        private val tmpGuideViews = ArrayList<GuideView>()

        fun setTargetGuideView(guideView: GuideView) = apply {
            tmpGuideViews.add(guideView)
        }

        fun build(): GuideWrapperView = GuideWrapperView(context).apply {
                this.guideViews.addAll(tmpGuideViews)
        }

    }
}