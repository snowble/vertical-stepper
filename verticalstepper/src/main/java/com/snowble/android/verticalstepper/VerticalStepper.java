package com.snowble.android.verticalstepper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerticalStepper extends ViewGroup {

    private Context context;
    private Resources resources;

    private List<View> stepViews;

    private int outerHorizontalMargin;
    private int outerVerticalMargin;

    private int iconDimension;
    private Paint iconBackgroundPaint;
    private RectF reuseRectIconBackground;
    private TextPaint iconTextPaint;
    private Rect reuseRectIconText;

    private int touchViewHeight;
    private int touchViewBackground;

    public VerticalStepper(Context context) {
        super(context);
        init();
    }

    public VerticalStepper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        context = getContext();
        resources = getResources();

        initMargins();
        initIconProperties();
        initTouchViewProperties();
    }

    private void initMargins() {
        outerHorizontalMargin = resources.getDimensionPixelSize(R.dimen.outer_margin_horizontal);
        outerVerticalMargin = resources.getDimensionPixelSize(R.dimen.outer_margin_vertical);
    }

    private void initIconProperties() {
        initIconDimension();
        initIconBackground();
        initIconTextPaint();
        initIconRectsForReuse();
    }

    private void initIconDimension() {
        iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
    }

    private void initIconBackground() {
        //noinspection deprecation
        int defaultColor = resources.getColor(R.color.bg_icon);
        int iconBackground = getResolvedAttributeData(R.attr.colorPrimary, defaultColor, true);
        iconBackgroundPaint = new Paint();
        iconBackgroundPaint.setColor(iconBackground);
        iconBackgroundPaint.setAntiAlias(true);
    }

    private void initIconTextPaint() {
        iconTextPaint = new TextPaint();
        iconTextPaint.setColor(Color.WHITE);
        iconTextPaint.setAntiAlias(true);
        int iconTextSize = resources.getDimensionPixelSize(R.dimen.icon_font_size);
        iconTextPaint.setTextSize(iconTextSize);
    }

    private void initIconRectsForReuse() {
        reuseRectIconBackground = new RectF(0, 0, iconDimension, iconDimension);
        reuseRectIconText = new Rect();
    }

    private void initTouchViewProperties() {
        touchViewHeight = resources.getDimensionPixelSize(R.dimen.touch_height);
        touchViewBackground = getResolvedAttributeData(R.attr.selectableItemBackground, 0, false);
    }

    private int getResolvedAttributeData(int attr, int defaultData, boolean resolveRefs) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, resolveRefs);
        int resolvedAttributeData;
        if (value.type != TypedValue.TYPE_NULL) {
            resolvedAttributeData = value.data;
        } else {
            resolvedAttributeData = defaultData;
        }
        return resolvedAttributeData;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int childCount = getChildCount();
        stepViews = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            initStepView(getChildAt(i));
        }

        for (View v : stepViews) {
            InternalTouchView touchView = getTouchView(v);
            initTouchView(touchView);
        }
    }

    private void initStepView(final View stepView) {
        stepView.setVisibility(View.GONE);
        stepViews.add(stepView);

        createAndAttachTouchView(stepView);
    }

    private void createAndAttachTouchView(final View stepView) {
        getInternalLayoutParams(stepView).touchView = new InternalTouchView(context);
    }

    private void initTouchView(InternalTouchView touchView) {
        touchView.setBackgroundResource(touchViewBackground);
        // TODO See if the anonymous inner class can be avoided
        touchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Make step view visible.
            }
        });
        addView(touchView);
        LayoutParams lp = (LayoutParams) touchView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = touchViewHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSizeFromSpec = MeasureSpec.getSize(widthMeasureSpec);
        int wModeFromSpec = MeasureSpec.getMode(widthMeasureSpec);

        int hSizeFromSpec = MeasureSpec.getSize(heightMeasureSpec);
        int hModeFromSpec = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        int height;

        boolean measureWidth = wModeFromSpec != MeasureSpec.EXACTLY;
        boolean measureHeight = hModeFromSpec != MeasureSpec.EXACTLY;

        if (measureWidth) {
            width = outerHorizontalMargin + getPaddingLeft() + getPaddingRight();
        } else {
            width = wSizeFromSpec;
        }

        if (measureHeight) {
            height = outerVerticalMargin + getPaddingTop() + getPaddingBottom();
        } else {
            height = hSizeFromSpec;
        }

        for (View v : stepViews) {
            int stepWidth = 0;
            int stepHeight = 0;
            if (measureWidth) {
                stepWidth += iconDimension;
                width = Math.max(width, stepWidth);
            }

            if (measureHeight) {
                stepHeight += iconDimension;
                height += stepHeight;
                height = Math.max(height, touchViewHeight);
            }

            // TODO Measure active child and add that to our measurements
        }

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);

        for (View v : stepViews) {
            int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int hms = MeasureSpec.makeMeasureSpec(touchViewHeight, MeasureSpec.EXACTLY);
            getTouchView(v).measure(wms, hms);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (View v : stepViews) {
            // TODO Update l,t,r,b based on translations
            int leftPos = left + getPaddingLeft();
            int rightPos = right - left - getPaddingRight();
            int topPos = top + getPaddingTop();
            InternalTouchView touchView = getTouchView(v);
            touchView.layout(leftPos, topPos, rightPos, topPos + touchView.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingBottom(), getPaddingTop());
        for (int i = 0; i < stepViews.size(); i++) {
            boolean isFirstChild = i == 0;
            if (isFirstChild) {
                canvas.translate(outerHorizontalMargin, outerVerticalMargin);
            }
            int stepNumber = i + 1;
            drawIcon(canvas, stepNumber);
        }
        canvas.restore();
    }

    private void drawIcon(Canvas canvas, int stepNumber) {
        drawIconBackground(canvas);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas) {
        canvas.drawArc(reuseRectIconBackground, 0f, 360f, true, iconBackgroundPaint);
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

        iconTextPaint.getTextBounds(stepNumberString, 0, 1, reuseRectIconText);
        float centeredTextY = (iconDimension / 2) + (reuseRectIconText.height() / 2);

        canvas.drawText(stepNumberString, centeredTextX, centeredTextY, iconTextPaint);
    }

    private static InternalTouchView getTouchView(View stepView) {
        return getInternalLayoutParams(stepView).touchView;
    }

    private static LayoutParams getInternalLayoutParams(View stepView) {
        return (LayoutParams) stepView.getLayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(context, attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        InternalTouchView touchView;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private static class InternalTouchView extends View {
        public InternalTouchView(Context context) {
            super(context);
        }
    }
}
