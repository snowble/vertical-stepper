package com.snowble.android.verticalstepper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
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

    private int outerHorizontalPadding;
    private int outerVerticalPadding;
    private boolean useSuggestedPadding;

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
        init(attrs);
    }

    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        init(null);
    }

    private void init(@Nullable AttributeSet attrs) {
        init(attrs, 0);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        init(attrs, defStyleAttr, 0);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setWillNotDraw(false);

        context = getContext();
        resources = getResources();
        initPropertiesFromAttrs(attrs, defStyleAttr, defStyleRes);

        initMargins();
        initIconProperties();
        initTouchViewProperties();
    }

    private void initPropertiesFromAttrs(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalStepper,
                    defStyleAttr, defStyleRes);
            try {
                validateSuggestedPaddingUsage(a);
                useSuggestedPadding = a.getBoolean(R.styleable.VerticalStepper_useSuggestedPadding, false);
            } finally {
                a.recycle();
            }
        }
    }

    private void validateSuggestedPaddingUsage(TypedArray a) {
        if (a.hasValue(R.styleable.VerticalStepper_useSuggestedPadding)
                && (getPaddingLeft() != 0
                || getPaddingTop() != 0
                || getPaddingRight() != 0
                || getPaddingBottom() != 0)) {
            throw new IllegalStateException("padding values must be zero when useSuggestedPadding is true.");
        }
    }

    private void initMargins() {
        if (useSuggestedPadding) {
            outerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_horizontal);
            outerVerticalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_vertical);
        } else {
            outerHorizontalPadding = 0;
            outerVerticalPadding = 0;
        }
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
        iconTextPaint = createTextPaint(R.color.white, R.dimen.icon_font_size);
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

    private TextPaint createTextPaint(int colorRes, int fontDimenRes) {
        TextPaint textPaint = new TextPaint();
        setTextColor(textPaint, colorRes);
        textPaint.setAntiAlias(true);
        int titleTextSize = resources.getDimensionPixelSize(fontDimenRes);
        textPaint.setTextSize(titleTextSize);
        return textPaint;
    }

    private void setTextColor(TextPaint paint, int colorRes) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = resources.getColor(colorRes, context.getTheme());
        } else {
            color = resources.getColor(colorRes);
        }
        paint.setColor(color);
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
            width = outerHorizontalPadding + getPaddingLeft() + getPaddingRight();
        } else {
            width = wSizeFromSpec;
        }

        if (measureHeight) {
            height = outerVerticalPadding + getPaddingTop() + getPaddingBottom();
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
            InternalTouchView touchView = getTouchView(v);
            touchView.layout(left, top, right - left, top + touchView.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (useSuggestedPadding) {
            canvas.translate(outerHorizontalPadding, outerVerticalPadding);
        } else {
            canvas.translate(getPaddingBottom(), getPaddingTop());
        }
        for (int i = 0; i < stepViews.size(); i++) {
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
