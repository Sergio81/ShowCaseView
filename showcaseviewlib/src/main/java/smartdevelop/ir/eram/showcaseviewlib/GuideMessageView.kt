package smartdevelop.ir.eram.showcaseviewlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Spannable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.Companion.RADIUS_SIZE_TARGET_RECT

/**
 * Created by Mohammad Reza Eram  on 20/01/2018.
 */

class GuideMessageView internal constructor(context: Context) : LinearLayout(context) {
    private val density: Float = context.resources.displayMetrics.density
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTitleTextView: TextView
    private val mContentTextView: TextView
    private var location = IntArray(2)
    private val padding = (10 * density).toInt()

    internal val mRect: RectF = RectF()

    init {
        setWillNotDraw(false)
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        mPaint.strokeCap = Paint.Cap.ROUND

        val paddingBetween = (3 * density).toInt()

        mTitleTextView = TextView(context).apply {
            setTextColor(Color.BLACK)
            setPadding(padding, padding, padding, paddingBetween)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            gravity = Gravity.CENTER
        }

        addView(mTitleTextView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        mContentTextView = TextView(context).apply {
            setTextColor(Color.BLACK)
            setPadding(padding, paddingBetween, padding, padding)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            gravity = Gravity.CENTER
        }

        addView(mContentTextView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun updatePosition(x: Float, y: Float) {
        this.x = x
        this.y = y
        mRect.set(x, y, x + width, y + height)
    }

    fun setTitle(title: String?) {
        if (title == null) {
            mContentTextView.setPadding(padding, padding, padding, padding)
            removeView(mTitleTextView)
            return
        }
        mTitleTextView.text = title
    }

    fun setContentText(content: String?) {
        if(content == null){
            mTitleTextView.setPadding(padding, padding, padding, padding)
            removeView(mContentTextView)
            return
        }
        mContentTextView.text = content
    }

    fun setContentSpan(content: Spannable) {
        mContentTextView.text = content
    }

    fun setContentTypeFace(typeFace: Typeface) {
        mContentTextView.typeface = typeFace
    }

    fun setTitleTypeFace(typeFace: Typeface) {
        mTitleTextView.typeface = typeFace
    }

    fun setTitleTextSize(size: Int) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }

    fun setContentTextSize(size: Int) {
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }

    fun setColor(color: Int) {

        mPaint.alpha = 255
        mPaint.color = color

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        this.getLocationOnScreen(location)

        mRect.set(paddingLeft.toFloat(),
                paddingTop.toFloat(),
                (width - paddingRight).toFloat(),
                (height - paddingBottom).toFloat())

        canvas.drawRoundRect(mRect,
                RADIUS_SIZE_TARGET_RECT,
                RADIUS_SIZE_TARGET_RECT,
                mPaint)
    }
}
