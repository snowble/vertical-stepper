package com.snowble.android.verticalstepper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
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

    private List<View> innerViews;

    private int outerHorizontalPadding;
    private int outerVerticalPadding;

    private int innerInactiveVerticalMargin;
    private int innerActiveVerticalMargin;

    private int iconDimension;
    private int iconMarginRight;
    private int iconMarginVertical;
    private Paint iconActiveBackgroundPaint;
    private Paint iconInactiveBackgroundPaint;
    private RectF reuseRectIconBackground;
    private TextPaint iconTextPaint;
    private Rect reuseRectIconTextBounds;

    private TextPaint titleActiveTextPaint;
    private TextPaint titleInactiveTextPaint;
    private Rect reuseRectTitleTextBounds;
    private float reuseBaselineTitle;
    private float reuseHeightTitle;
    private TextPaint summaryTextPaint;
    private float reuseBaselineSummary;
    private float reuseHeightSummary;
    private int titleMarginBottom;

    private int touchViewHeight;
    private int touchViewBackground;

    private int connectorWidth;
    private Paint connectorPaint;

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

        initPadding();
        initIconProperties();
        initTitleProperties();
        initSummaryProperties();
        initTouchViewProperties();
        initConnectorProperties();
    }

    private void initPadding() {
        outerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_horizontal);
        outerVerticalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_vertical);
        innerInactiveVerticalMargin = resources.getDimensionPixelSize(R.dimen.inner_inactive_margin_vertical);
        innerActiveVerticalMargin = resources.getDimensionPixelSize(R.dimen.inner_active_margin_vertical);
    }

    private void initIconProperties() {
        initIconDimension();
        initIconMargins();
        initIconBackground();
        initIconTextPaint();
        initIconReuseObjects();
    }

    private void initIconDimension() {
        iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
    }

    private void initIconMargins() {
        iconMarginRight = resources.getDimensionPixelSize(R.dimen.icon_margin_right);
        iconMarginVertical = resources.getDimensionPixelSize(R.dimen.icon_margin_vertical);
    }

    private void initIconBackground() {
        initActiveIconBackground();
        initInactiveIconBackground();
    }

    private void initActiveIconBackground() {
        //noinspection deprecation
        int defaultColor = resources.getColor(R.color.bg_active_icon);
        int iconBackground = getResolvedAttributeData(R.attr.colorPrimary, defaultColor, true);
        iconActiveBackgroundPaint = new Paint();
        iconActiveBackgroundPaint.setColor(iconBackground);
        iconActiveBackgroundPaint.setAntiAlias(true);
    }

    private void initInactiveIconBackground() {
        iconInactiveBackgroundPaint = new Paint();
        setPaintColor(iconInactiveBackgroundPaint, R.color.bg_inactive_icon);
        iconInactiveBackgroundPaint.setAntiAlias(true);
    }

    private void initIconTextPaint() {
        iconTextPaint = createTextPaint(R.color.white, R.dimen.icon_font_size);
    }

    private void initIconReuseObjects() {
        reuseRectIconBackground = new RectF(0, 0, iconDimension, iconDimension);
        reuseRectIconTextBounds = new Rect();
    }

    private void initTitleProperties() {
        initTitleDimensions();
        initTitleTextPaint();
        initTitleReuseObjects();
    }

    private void initTitleDimensions() {
        titleMarginBottom = resources.getDimensionPixelSize(R.dimen.title_margin_bottom);
    }

    private void initTitleTextPaint() {
        titleActiveTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
        titleActiveTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        titleInactiveTextPaint = createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size);
    }

    private void initTitleReuseObjects() {
        reuseRectTitleTextBounds = new Rect();
    }

    private void initSummaryProperties() {
        initSummaryTextPaint();
    }

    private void initSummaryTextPaint() {
        summaryTextPaint = createTextPaint(R.color.summary_color, R.dimen.summary_font_size);
    }

    private TextPaint createTextPaint(int colorRes, int fontDimenRes) {
        TextPaint textPaint = new TextPaint();
        setPaintColor(textPaint, colorRes);
        textPaint.setAntiAlias(true);
        int titleTextSize = resources.getDimensionPixelSize(fontDimenRes);
        textPaint.setTextSize(titleTextSize);
        return textPaint;
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

    private void initConnectorProperties() {
        initConnectorDimension();
        initConnectorPaint();
    }

    private void initConnectorDimension() {
        connectorWidth = resources.getDimensionPixelSize(R.dimen.connector_width);
    }

    private void initConnectorPaint() {
        connectorPaint = new Paint();
        setPaintColor(connectorPaint, R.color.connector_color);
        connectorPaint.setAntiAlias(true);
        connectorPaint.setStrokeWidth(connectorWidth);
    }

    private void setPaintColor(Paint paint, int colorRes) {
        int color = ResourcesCompat.getColor(resources, colorRes, context.getTheme());
        paint.setColor(color);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int childCount = getChildCount();
        innerViews = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            initInnerView(getChildAt(i));
        }

        for (View v : innerViews) {
            initTouchView(v);
        }
    }

    private void initInnerView(final View innerView) {
        innerView.setVisibility(View.GONE);
        innerViews.add(innerView);

        createAndAttachTouchView(innerView);
    }

    private void createAndAttachTouchView(View innerView) {
        getInternalLayoutParams(innerView).touchView = new InternalTouchView(context);
    }

    private void initTouchView(final View innerView) {
        InternalTouchView touchView = getTouchView(innerView);
        touchView.setBackgroundResource(touchViewBackground);
        // TODO See if the anonymous inner class can be avoided
        touchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStepActiveState(innerView);
            }
        });
        addView(touchView);
    }

    private void toggleStepActiveState(View innerView) {
        LayoutParams lp = getInternalLayoutParams(innerView);
        lp.active = !lp.active;

        int visibility = innerView.getVisibility();
        if (visibility == VISIBLE) {
            innerView.setVisibility(GONE);
        } else {
            innerView.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSizeFromSpec = MeasureSpec.getSize(widthMeasureSpec);
        int wModeFromSpec = MeasureSpec.getMode(widthMeasureSpec);

        int hSizeFromSpec = MeasureSpec.getSize(heightMeasureSpec);
        int hModeFromSpec = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        int height;

        int horizontalPadding = 0;
        int verticalPadding = 0;

        boolean measureWidth = wModeFromSpec != MeasureSpec.EXACTLY;
        boolean measureHeight = hModeFromSpec != MeasureSpec.EXACTLY;

        if (measureWidth) {
            horizontalPadding = outerHorizontalPadding + outerHorizontalPadding + getPaddingLeft() + getPaddingRight();
            width = horizontalPadding;
        } else {
            width = wSizeFromSpec;
        }

        if (measureHeight) {
            verticalPadding = outerVerticalPadding + outerVerticalPadding + getPaddingTop() + getPaddingBottom();
            height = verticalPadding;
        } else {
            height = hSizeFromSpec;
        }

        int widthWithoutPadding = 0;
        for (int i = 0, innerViewsSize = innerViews.size(); i < innerViewsSize; i++) {
            View v = innerViews.get(i);
            LayoutParams lp = getInternalLayoutParams(v);
            int innerViewHorizontalPadding = iconDimension + iconMarginRight + lp.leftMargin + lp.rightMargin;
            int innerViewVerticalPadding = lp.topMargin + lp.bottomMargin;
            int innerWms;
            int innerHms;
            if (measureWidth) {
                int stepDecoratorWidth = measureStepDecoratorWidth(v);
                widthWithoutPadding = Math.max(widthWithoutPadding, stepDecoratorWidth);
            }
            innerWms = getChildMeasureSpec(widthMeasureSpec, horizontalPadding + innerViewHorizontalPadding, lp.width);

            if (measureHeight) {
                int stepDecoratorHeight = measureStepDecoratorHeight(v);
                height += stepDecoratorHeight;
            }
            innerHms = getChildMeasureSpec(heightMeasureSpec, height + innerViewVerticalPadding, lp.height);
            if (measureHeight) {
                boolean hasMoreSteps = i + 1 < innerViewsSize;
                if (hasMoreSteps) {
                    height += getInnerVerticalMargin(lp);
                }
            }

            v.measure(innerWms, innerHms);
            if (measureWidth) {
                widthWithoutPadding = Math.max(widthWithoutPadding, v.getMeasuredWidth() + innerViewHorizontalPadding);
            }
            if (measureHeight && lp.active) {
                height += v.getMeasuredHeight() + innerViewVerticalPadding;
            }
        }
        width += widthWithoutPadding;

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);

        for (View v : innerViews) {
            measureTouchView(width, v);
        }

        setMeasuredDimension(width, height);
    }

    private int measureStepDecoratorWidth(View v) {
        int stepDecoratorWidth = iconDimension;
        stepDecoratorWidth += iconMarginRight;

        float titleWidth = measureTitleWidth(v);
        float summaryWidth = measureSummaryWidth(v);
        stepDecoratorWidth += Math.max(titleWidth, summaryWidth);

        return stepDecoratorWidth;
    }

    private int measureStepDecoratorHeight(View v) {
        measureTitleHeight(getInternalLayoutParams(v));
        measureSummaryHeight();
        int textTotalHeight = (int) (reuseHeightTitle + reuseHeightSummary);
        return Math.max(iconDimension, textTotalHeight);
    }

    private void measureTouchView(int width, View v) {
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(touchViewHeight, MeasureSpec.EXACTLY);
        getTouchView(v).measure(wms, hms);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0, innerViewsSize = innerViews.size(); i < innerViewsSize; i++) {
            View v = innerViews.get(i);
            boolean isFirstStep = i == 0;
            boolean isLastStep = i == innerViewsSize - 1;

            // TODO Update l,t,r,b based on translations

            InternalTouchView touchView = getTouchView(v);
            int touchLeft = left + getPaddingLeft();
            int touchTop = top;
            if (isFirstStep) {
                touchTop += getPaddingTop();
            }
            int touchRight = right - left - getPaddingRight();
            int touchBottomMax;
            if (isLastStep) {
                touchBottomMax = bottom - top - getPaddingBottom();
            } else {
                touchBottomMax = bottom;
            }
            int touchBottom = Math.min(top + touchView.getMeasuredHeight(), touchBottomMax);
            touchView.layout(touchLeft, touchTop, touchRight, touchBottom);

            LayoutParams lp = getInternalLayoutParams(v);
            if (lp.active) {
                int innerLeft = left + outerHorizontalPadding + getPaddingLeft() + lp.leftMargin
                        + iconDimension + iconMarginRight;
                int innerTop = (int) (top + lp.topMargin + reuseHeightTitle + titleMarginBottom);
                if (isFirstStep) {
                    innerTop += getPaddingTop() + outerVerticalPadding;
                }
                int innerRightMax = right - outerHorizontalPadding - getPaddingRight() - lp.rightMargin;
                int innerRight = Math.min(innerLeft + v.getMeasuredWidth(), innerRightMax);
                int innerBottomMax;
                if (isLastStep) {
                    innerBottomMax = bottom - outerVerticalPadding - getPaddingBottom() - lp.bottomMargin;
                } else {
                    innerBottomMax = bottom;
                }
                int innerBottom = Math.min(innerTop + v.getMeasuredHeight(), innerBottomMax);
                v.layout(innerLeft, innerTop, innerRight, innerBottom);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(outerHorizontalPadding + getPaddingLeft(), outerVerticalPadding + getPaddingTop());
        for (int i = 0, innerViewsSize = innerViews.size(); i < innerViewsSize; i++) {
            canvas.save();

            int stepNumber = i + 1;
            View innerView = innerViews.get(i);
            LayoutParams lp = getInternalLayoutParams(innerView);

            canvas.save();
            drawIcon(canvas, lp, stepNumber);
            canvas.restore();

            canvas.save();
            drawText(canvas, lp);
            canvas.restore();

            boolean hasMoreSteps = stepNumber < innerViewsSize;
            if (hasMoreSteps) {
                canvas.save();
                drawConnector(canvas, lp);
                canvas.restore();
            }

            canvas.restore();
        }
        canvas.restore();
    }

    private void drawIcon(Canvas canvas, LayoutParams lp, int stepNumber) {
        drawIconBackground(canvas, lp);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas, LayoutParams lp) {
        canvas.drawArc(reuseRectIconBackground, 0f, 360f, true, getIconColor(lp));
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

        iconTextPaint.getTextBounds(stepNumberString, 0, 1, reuseRectIconTextBounds);
        float centeredTextY = (iconDimension / 2) + (reuseRectIconTextBounds.height() / 2);

        canvas.drawText(stepNumberString, centeredTextX, centeredTextY, iconTextPaint);
    }

    private void drawText(Canvas canvas, LayoutParams lp) {
        canvas.translate(iconDimension + iconMarginRight, 0);

        measureTitleHeight(lp);
        TextPaint paint = getTitleTextPaint(lp);
        canvas.drawText(lp.title, 0, reuseBaselineTitle, paint);

        if (!TextUtils.isEmpty(lp.summary) && !lp.active) {
            canvas.translate(0, reuseHeightTitle);

            measureSummaryHeight();
            canvas.drawText(lp.summary, 0, reuseBaselineSummary, summaryTextPaint);
        }
        // TODO Handle optional case
    }

    private void drawConnector(Canvas canvas, LayoutParams lp) {
        canvas.translate((iconDimension - connectorWidth) / 2, 0);
        float startY = iconDimension + iconMarginVertical;
        float stopY = getInactiveStepHeightIncludingVerticalMargin(lp) - iconMarginVertical;
        canvas.drawLine(0, startY, 0, stopY, connectorPaint);
    }

    private float getInactiveStepHeightIncludingVerticalMargin(LayoutParams lp) {
        return reuseHeightTitle + reuseHeightSummary + getInnerVerticalMargin(lp);
    }

    private float measureTitleWidth(View v) {
        float titleWidth = 0f;
        LayoutParams lp = getInternalLayoutParams(v);
        if (!TextUtils.isEmpty(lp.title)) {
            titleWidth = getTitleTextPaint(lp).measureText(lp.title);
        }
        return titleWidth;
    }

    private void measureTitleHeight(LayoutParams lp) {
        reuseBaselineTitle = getTitleBaseline(lp);
        reuseHeightTitle = reuseBaselineTitle + getTitleTextPaint(lp).getFontMetrics().bottom;
    }

    private float getTitleBaseline(LayoutParams lp) {
        getTitleTextPaint(lp).getTextBounds(lp.title, 0, 1, reuseRectTitleTextBounds);
        return (iconDimension / 2) + (reuseRectTitleTextBounds.height() / 2);
    }

    private TextPaint getTitleTextPaint(LayoutParams lp) {
        return lp.active ? titleActiveTextPaint : titleInactiveTextPaint;
    }

    private float measureSummaryWidth(View v) {
        float summaryWidth = 0f;
        LayoutParams lp = getInternalLayoutParams(v);
        String summary = lp.summary;
        if (!TextUtils.isEmpty(summary)) {
            summaryWidth = summaryTextPaint.measureText(summary);
        }
        return summaryWidth;
    }

    private void measureSummaryHeight() {
        reuseBaselineSummary = getSummaryBaseline();
        reuseHeightSummary = reuseBaselineSummary + summaryTextPaint.getFontMetrics().bottom;
    }

    private float getSummaryBaseline() {
        return -summaryTextPaint.getFontMetrics().ascent;
    }

    private int getInnerVerticalMargin(LayoutParams lp) {
        return lp.active ? innerActiveVerticalMargin : innerInactiveVerticalMargin;
    }

    private Paint getIconColor(LayoutParams lp) {
        return lp.active ? iconActiveBackgroundPaint : iconInactiveBackgroundPaint;
    }
    private static InternalTouchView getTouchView(View innerView) {
        return getInternalLayoutParams(innerView).touchView;
    }

    private static LayoutParams getInternalLayoutParams(View innerView) {
        return (LayoutParams) innerView.getLayoutParams();
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

        @SuppressWarnings("NullableProblems")
        @NonNull
        String title;
        @Nullable
        String summary;


        boolean active;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.VerticalStepper_Layout);
            try {
                //noinspection ConstantConditions
                title = a.getString(R.styleable.VerticalStepper_Layout_step_title);
                summary = a.getString(R.styleable.VerticalStepper_Layout_step_summary);
            } finally {
                a.recycle();
            }
            if (TextUtils.isEmpty(title)) {
                throw new IllegalArgumentException("step_title cannot be empty.");
            }
            active = false;
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
