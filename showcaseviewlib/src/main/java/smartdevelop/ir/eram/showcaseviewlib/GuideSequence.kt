package smartdevelop.ir.eram.showcaseviewlib

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleObserver
import smartdevelop.ir.eram.showcaseviewlib.listener.OnDismissGuideListener

class GuideSequence(context: Context) : FrameLayout(context), LifecycleObserver, OnDismissGuideListener {
    private val selfPaint = Paint()
    private val sequences = ArrayList<GuideWrapperView>()
    private var currentIndex = 0
    private var currentSequence: GuideWrapperView? = null

    var isShowing = false

    init {
        setWillNotDraw(false)
        // this allow X_FER_MODE_CLEAR works
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        selfPaint.color = GlobalVariables.BACKGROUND_COLOR
        selfPaint.style = Paint.Style.FILL
        selfPaint.isAntiAlias = true

        if (currentSequence != null) {
            canvas!!.drawRect(currentSequence!!.selfRect, selfPaint)

            // Show the children views without the background
            for (g: GuideView in currentSequence!!.guideViews) {
                canvas.drawRoundRect(
                        g.targetRect,
                        GlobalVariables.RADIUS_SIZE_TARGET_RECT,
                        GlobalVariables.RADIUS_SIZE_TARGET_RECT,
                        g.targetPaint)
            }
        }
    }

    fun dismiss() {
        if (currentSequence != null) {
            currentSequence!!.dismiss()
        }
        ((context as Activity).window.decorView as ViewGroup).removeView(this)
        isShowing = false
    }

    override fun onDismissGuide(view: View?) {
        currentSequence = setCurrentSequence()
        if (currentSequence == null) {
            dismiss()
        }
    }

    fun show() {
        val startAnimation = AlphaAnimation(0.0f, 1.0f)

        isShowing = true
        this.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)

        this.isClickable = false

        ((context as Activity).window.decorView as ViewGroup).addView(this)

        startAnimation.duration = GlobalVariables.APPEARING_ANIMATION_DURATION.toLong()
        startAnimation.fillAfter = true

        this.startAnimation(startAnimation)
        currentSequence = setCurrentSequence()
    }

    private fun setCurrentSequence(): GuideWrapperView? =
            if (sequences.size > 0 && currentIndex < sequences.size && isShowing) {
                sequences[currentIndex].apply {
                    isChildSequence = true
                    setOnGuideDismissListener(this@GuideSequence)
                    show()
                    currentIndex++
                    postInvalidate()
                }
            } else {
                null
            }

    class Builder {
        private val sequences = ArrayList<GuideWrapperView>()

        fun addSequence(sequence: GuideWrapperView) = apply {
            sequences.add(sequence)
        }

        fun build(context: Context): GuideSequence = GuideSequence(context).apply {
            this.sequences.addAll(this@Builder.sequences)
        }
    }
}