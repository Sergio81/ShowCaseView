package smartdevelop.ir.eram.showcaseviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.os.Build;
import android.text.Spannable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.APPEARING_ANIMATION_DURATION;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.BACKGROUND_COLOR;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.CIRCLE_INDICATOR_COLOR;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.CIRCLE_INDICATOR_SIZE;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.CIRCLE_INNER_INDICATOR_COLOR;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.INDICATOR_HEIGHT;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.LINE_INDICATOR_COLOR;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.LINE_INDICATOR_WIDTH_SIZE;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.MARGIN_INDICATOR;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.MESSAGE_VIEW_PADDING;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.RADIUS_SIZE_TARGET_RECT;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.SIZE_ANIMATION_DURATION;
import static smartdevelop.ir.eram.showcaseviewlib.GlobalVariables.STROKE_CIRCLE_INDICATOR_SIZE;

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 */

public class GuideView extends FrameLayout {
    //region Global variables
    static final String TAG = "GuideView";

    private final Xfermode X_FER_MODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private final Paint selfPaint = new Paint();
    protected final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private View target;
    protected RectF targetRect;
    private final Rect selfRect = new Rect();

    private float density, stopY;
    private boolean isTop;
    private boolean mIsShowing;
    private int yMessageView = 0;

    private float circleIndicatorSizeFinal;
    private int messageViewPadding;
    private float marginGuide;
    private float indicatorHeight;

    private boolean isPerformedAnimationSize = false;

    private GuideListener mGuideListener;
    private DismissType dismissType;
    private GuideMessageView mMessageView;
    private Boolean showSemitransparentBackground = true;

    private Position position = Position.Auto;
    private Indicator indicator;
    protected Boolean isChild = false;
    //endregion

    private GuideView(Context context, View view) {
        super(context);
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.target = view;
        density = context.getResources().getDisplayMetrics().density;
        init();

        int[] locationTarget = new int[2];
        target.getLocationOnScreen(locationTarget);
        targetRect = new RectF(locationTarget[0],
                locationTarget[1],
                locationTarget[0] + target.getWidth(),
                locationTarget[1] + target.getHeight());

        mMessageView = new GuideMessageView(getContext());
        mMessageView.setPadding(messageViewPadding, messageViewPadding, messageViewPadding, messageViewPadding);
        mMessageView.setColor(Color.WHITE);

        indicator = new Indicator(target, mMessageView);

        addView(mMessageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setMessageLocation(resolveMessageViewLocation());

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                setMessageLocation(resolveMessageViewLocation());
                int[] locationTarget = new int[2];
                target.getLocationOnScreen(locationTarget);

                targetRect = new RectF(locationTarget[0],
                        locationTarget[1],
                        locationTarget[0] + target.getWidth(),
                        locationTarget[1] + target.getHeight());

                selfRect.set(getPaddingLeft(),
                        getPaddingTop(),
                        getWidth() - getPaddingRight(),
                        getHeight() - getPaddingBottom());

                marginGuide = (int) (isTop ? marginGuide : -marginGuide);
                stopY = yMessageView + indicatorHeight;


                getViewTreeObserver().addOnGlobalLayoutListener(this);

            }
        };

        this.post( new Runnable() {
            @Override
            public void run() {
                startAnimationSize();
            }
        });

        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private void startAnimationSize() {
        if (!isPerformedAnimationSize) {
            final ValueAnimator circleSizeAnimator = ValueAnimator.ofFloat(0f, circleIndicatorSizeFinal);
            circleSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    indicator.setCircleIndicatorSize((float) circleSizeAnimator.getAnimatedValue());
                    indicator.setCircleInnerIndicatorSize((float) circleSizeAnimator.getAnimatedValue() - density);
                    postInvalidate();
                }
            });

            final ValueAnimator linePositionAnimator = ValueAnimator.ofFloat(indicator.getInitAnimation(), indicator.getFinalAnimation());
            linePositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    indicator.setCurrentAnimatedPosition((float)linePositionAnimator.getAnimatedValue());
                    postInvalidate();
                }
            });

            linePositionAnimator.setDuration(SIZE_ANIMATION_DURATION);
            indicator.setLocked(true);
            linePositionAnimator.start();
            linePositionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    circleSizeAnimator.setDuration(SIZE_ANIMATION_DURATION);
                    circleSizeAnimator.start();
                }

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            circleSizeAnimator.addListener(new Animator.AnimatorListener(){
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isPerformedAnimationSize = false;
                    indicator.setLocked(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    private void init() {
        //lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE * density;
        marginGuide = MARGIN_INDICATOR * density;
        indicatorHeight = INDICATOR_HEIGHT * density;
        messageViewPadding = (int) (MESSAGE_VIEW_PADDING * density);
        //strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE * density;
        circleIndicatorSizeFinal = CIRCLE_INDICATOR_SIZE * density;
        targetPaint.setXfermode(X_FER_MODE_CLEAR);
        targetPaint.setAntiAlias(true);
    }

    private int getNavigationBarSize() {
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private boolean isLandscape() {
        int display_mode = getResources().getConfiguration().orientation;
        return display_mode != Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (target != null) {

            // Background Color
            if (showSemitransparentBackground) {
                selfPaint.setColor(BACKGROUND_COLOR);
                selfPaint.setStyle(Paint.Style.FILL);
                selfPaint.setAntiAlias(true);
                canvas.drawRect(selfRect, selfPaint);
            }

            indicator.draw(canvas);

            if (showSemitransparentBackground)
                canvas.drawRoundRect(targetRect, RADIUS_SIZE_TARGET_RECT, RADIUS_SIZE_TARGET_RECT, targetPaint);
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void dismiss() {
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
        mIsShowing = false;
        if (mGuideListener != null) {
            mGuideListener.onDismiss(target);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean success = false;

        //if (!isChild)
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (dismissType) {
                case outside:
                    if (!isViewContains(mMessageView, x, y)) {
                        dismiss();
                        if (!isChild) success = true;
                    }
                    break;

                case message:
                    if (isViewContains(mMessageView, x, y)) {
                        dismiss();
                        success = true;
                    }
                    break;

                case anywhere:
                    dismiss();
                    if (!isChild) success = true;
                    break;

                case targetView:
                    if (targetRect.contains(x, y)) {
                        target.performClick();
                        dismiss();
                        if (!isChild) success = true;
                    }
                    break;

            }
            return success;
        }
        return false;
    }

    private boolean isViewContains(View view, float rx, float ry) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }

    private void setMessageLocation(Point p) {
        mMessageView.setX(p.x);
        mMessageView.setY(p.y);
        indicator.updatePosition();
        postInvalidate();
    }

    public void updateGuideViewLocation() {
        requestLayout();
    }

    private Point resolveMessageViewLocation() {
        int xMessageView;

        switch (position){
            case Top:
                xMessageView = (int) (targetRect.left - (targetRect.width() / 2));
                yMessageView = (int) (targetRect.top - indicatorHeight - mMessageView.getHeight());
                break;
            case Bottom:
                xMessageView = (int) (targetRect.left - (targetRect.width() / 2));
                yMessageView = (int) (targetRect.top + targetRect.height() + indicatorHeight);
                break;
            case Left:
                xMessageView = (int) (targetRect.left - mMessageView.getWidth() - indicatorHeight);
                yMessageView = (int) ((targetRect.top + (targetRect.height() / 2)) - (mMessageView.getHeight() / 2));
                break;
            case Right:
                xMessageView = (int) (targetRect.right + indicatorHeight);
                yMessageView = (int) ((targetRect.top + (targetRect.height() / 2)) - (mMessageView.getHeight() / 2));
                break;
            default:
                xMessageView = (int) targetRect.top - mMessageView.getHeight();
                break;
        }

        return new Point(xMessageView, yMessageView);
    }

    public void show() {
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setClickable(false);

        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(APPEARING_ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);
        mIsShowing = true;
    }

    public void setTitle(String str) {
        mMessageView.setTitle(str);
    }

    public void setPosition(Position position) {
        this.position = position;
        indicator.setPosition(position);
    }

    public void setContentText(String str) {
        mMessageView.setContentText(str);
    }

    public void setContentSpan(Spannable span) {
        mMessageView.setContentSpan(span);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mMessageView.setTitleTypeFace(typeFace);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mMessageView.setContentTypeFace(typeFace);
    }

    public void setTitleTextSize(int size) {
        mMessageView.setTitleTextSize(size);
    }

    public void setContentTextSize(int size) {
        mMessageView.setContentTextSize(size);
    }

    public void setSemitransparentBackground(Boolean show) {
        showSemitransparentBackground = show;
    }

    public void setDismissType(DismissType dismissType) {
        this.dismissType = dismissType;
    }

    public static class Builder {
        private View targetView;
        private String title, contentText;
        private Gravity gravity;
        private DismissType dismissType;
        private Context context;
        private Spannable contentSpan;
        private Typeface titleTypeFace, contentTypeFace;
        private GuideListener guideListener;
        private int titleTextSize;
        private int contentTextSize;
        private float lineIndicatorHeight;
        private float lineIndicatorWidthSize;
        private float circleIndicatorSize;
        private float circleInnerIndicatorSize;
        private float strokeCircleWidth;
        private Boolean showSemitransparentBackground = true;
        private Position position = Position.Auto;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTargetView(View view) {
            this.targetView = view;
            return this;
        }

        /**
         * gravity GuideView
         *
         * @param gravity it should be one type of Gravity enum.
         **/
        public Builder setGravity(Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * defining a title
         *
         * @param title a title. for example: submit button.
         **/
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * defining a description for the target view
         *
         * @param contentText a description. for example: this button can for submit your information..
         **/
        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        /**
         * setting spannable type
         *
         * @param span a instance of spannable
         **/
        public Builder setContentSpan(Spannable span) {
            this.contentSpan = span;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/
        public Builder setContentTypeFace(Typeface typeFace) {
            this.contentTypeFace = typeFace;
            return this;
        }

        /**
         * adding a listener on show case view
         *
         * @param guideListener a listener for events
         **/
        public Builder setGuideListener(GuideListener guideListener) {
            this.guideListener = guideListener;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/
        public Builder setTitleTypeFace(Typeface typeFace) {
            this.titleTypeFace = typeFace;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setContentTextSize(int size) {
            this.contentTextSize = size;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setTitleTextSize(int size) {
            this.titleTextSize = size;
            return this;
        }

        /**
         * this method defining the type of dismissing function
         *
         * @param dismissType should be one type of DismissType enum. for example: outside -> Dismissing with click on outside of MessageView
         */
        public Builder setDismissType(DismissType dismissType) {
            this.dismissType = dismissType;
            return this;
        }

        /**
         * changing line height indicator
         *
         * @param height you can change height indicator (Converting to Dp)
         */
        public Builder setIndicatorHeight(float height) {
            this.lineIndicatorHeight = height;
            return this;
        }

        /**
         * changing line long indicator
         *
         * @param width you can change long indicator
         */
        public Builder setIndicatorWidthSize(float width) {
            this.lineIndicatorWidthSize = width;
            return this;
        }

        /**
         * changing circle size indicator
         *
         * @param size you can change circle size indicator
         */
        public Builder setCircleIndicatorSize(float size) {
            this.circleIndicatorSize = size;
            return this;
        }

        /**
         * changing inner circle size indicator
         *
         * @param size you can change inner circle indicator size
         */
        public Builder setCircleInnerIndicatorSize(float size) {
            this.circleInnerIndicatorSize = size;
            return this;
        }

        /**
         * changing stroke circle size indicator
         *
         * @param size you can change stroke circle indicator size
         */
        public Builder setCircleStrokeIndicatorSize(float size) {
            this.strokeCircleWidth = size;
            return this;
        }

        public Builder setSemitransparentBackground(Boolean show) {
            this.showSemitransparentBackground = show;
            return this;
        }

        public Builder setPosition(Position position) {
            this.position = position;
            return this;
        }

        public GuideView build() {
            GuideView guideView = new GuideView(context, targetView);
            guideView.dismissType = dismissType != null ? dismissType : DismissType.targetView;
            guideView.showSemitransparentBackground = this.showSemitransparentBackground;

            float density = context.getResources().getDisplayMetrics().density;

            guideView.setPosition(this.position);
            guideView.setTitle(title);

            if (contentText != null)
                guideView.setContentText(contentText);
            if (titleTextSize != 0)
                guideView.setTitleTextSize(titleTextSize);
            if (contentTextSize != 0)
                guideView.setContentTextSize(contentTextSize);
            if (contentSpan != null)
                guideView.setContentSpan(contentSpan);
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace);
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace);
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener;
            }
            if (lineIndicatorHeight != 0) {
                guideView.indicatorHeight = lineIndicatorHeight * density;
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

            return guideView;
        }


    }
}

