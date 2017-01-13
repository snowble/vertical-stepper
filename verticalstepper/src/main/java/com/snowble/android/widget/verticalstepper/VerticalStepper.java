package com.snowble.android.widget.verticalstepper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerticalStepper extends ViewGroup {
    private Context context;
    private Resources resources;
    private Step.Common commonStepValues;

    @VisibleForTesting
    List<Step> steps;

    @VisibleForTesting
    int outerHorizontalPadding;
    @VisibleForTesting
    int outerVerticalPadding;
    @VisibleForTesting
    int iconInactiveColor;
    @VisibleForTesting
    int iconActiveColor;
    @VisibleForTesting
    int iconCompleteColor;
    @VisibleForTesting
    int continueButtonStyle;

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
        initPadding();

        commonStepValues = new Step.Common(context, iconActiveColor, iconInactiveColor, iconCompleteColor);
        steps = new ArrayList<>();
    }

    @VisibleForTesting
    Step.Common getCommonStepValues() {
        return commonStepValues;
    }

    @VisibleForTesting
    void initPropertiesFromAttrs(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepper,
                    defStyleAttr, defStyleRes);
        try {
            initIconPropertiesFromAttrs(a);
            initNavButtonPropertiesFromAttrs(a);
        } finally {
            a.recycle();
        }
    }

    private void initIconPropertiesFromAttrs(TypedArray a) {
        int defaultActiveColor =
                ThemeUtils.getResolvedAttributeData(context.getTheme(), R.attr.colorPrimary, R.color.bg_active_icon);
        iconActiveColor = a.getColor(R.styleable.VerticalStepper_iconColorActive,
                ResourcesCompat.getColor(resources, defaultActiveColor, context.getTheme()));
        iconInactiveColor = a.getColor(R.styleable.VerticalStepper_iconColorInactive,
                ResourcesCompat.getColor(resources, R.color.bg_inactive_icon, context.getTheme()));
        iconCompleteColor = a.getColor(R.styleable.VerticalStepper_iconColorComplete,
                iconActiveColor);
    }

    @SuppressLint("PrivateResource") // https://code.google.com/p/android/issues/detail?id=230985
    private void initNavButtonPropertiesFromAttrs(TypedArray a) {
        continueButtonStyle = a.getResourceId(
                R.styleable.VerticalStepper_continueButtonStyle, R.style.Widget_AppCompat_Button_Colored);
    }

    private void initPadding() {
        outerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_horizontal);
        outerVerticalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_vertical);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initSteps();
    }

    @VisibleForTesting
    void initSteps() {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, continueButtonStyle);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            initStep(new Step(getChildAt(i), new InternalTouchView(context),
                    new AppCompatButton(contextWrapper, null, 0), commonStepValues));
        }

        for (Step s : steps) {
            initTouchView(s);
            initNavButtons(s);
        }
    }

    @VisibleForTesting
    void initStep(Step step) {
        steps.add(step);
        step.getInnerView().setVisibility(View.GONE);
    }

    @VisibleForTesting
    void initTouchView(final Step step) {
        InternalTouchView touchView = step.getTouchView();
        touchView.setBackgroundResource(step.getTouchViewBackgroundResource());
        touchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseOtherSteps(step);
                toggleStepExpandedState(step);
            }
        });
        addView(touchView);
    }

    @VisibleForTesting
    void initNavButtons(final Step step) {
        AppCompatButton continueButton = step.getContinueButton();
        continueButton.setVisibility(GONE);
        continueButton.setText(R.string.continue_button);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, step.getNavButtonHeight());
        lp.topMargin = step.getNavButtonTopMargin();
        continueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                completeStep(step);

            }
        });
        addView(continueButton, lp);
    }

    @VisibleForTesting
    void completeStep(Step step) {
        // TODO Add step validation
        step.setComplete(true);
        toggleStepExpandedState(step);

        int nextIndex = steps.indexOf(step) + 1;
        if (nextIndex < steps.size()) {
            toggleStepExpandedState(steps.get(nextIndex));
        } else {
            // TODO this is the last step. Complete the form
            // TODO Add listener for entire stepper validation
        }
    }

    @VisibleForTesting
    void collapseOtherSteps(Step stepToExcludeFromCollapse) {
        for (Step s : steps) {
            if (s != stepToExcludeFromCollapse && s.isActive()) {
                toggleStepExpandedState(s);
            }
        }
    }

    @VisibleForTesting
    void toggleStepExpandedState(Step step) {
        toggleActiveState(step);
        toggleViewVisibility(step.getInnerView());
        toggleViewVisibility(step.getContinueButton());
    }

    private void toggleActiveState(Step step) {
        step.setActive(!step.isActive());
    }

    private void toggleViewVisibility(View view) {
        int visibility = view.getVisibility();
        if (visibility == VISIBLE) {
            view.setVisibility(GONE);
        } else {
            view.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        doMeasurement(widthMeasureSpec, heightMeasureSpec);
    }

    @VisibleForTesting
    void doMeasurement(int widthMeasureSpec, int heightMeasureSpec) {
        measureStepDecoratorHeights();
        measureStepBottomMarginHeights();
        measureActiveViews(widthMeasureSpec, heightMeasureSpec);
        int width = calculateWidth();
        int height = calculateHeight();

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);

        measureTouchViews(width);

        setMeasuredDimension(width, height);
    }

    @VisibleForTesting
    void measureStepDecoratorHeights() {
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            steps.get(i).measureStepDecoratorHeight();
        }
    }

    @VisibleForTesting
    void measureStepBottomMarginHeights() {
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize - 1; i++) {
            steps.get(i).measureBottomMarginToNextStep();
        }
    }

    @VisibleForTesting
    void measureActiveViews(int widthMeasureSpec, int heightMeasureSpec) {
        int currentHeight = calculateVerticalPadding();
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            Step step = steps.get(i);
            int activeViewsHeight = 0;

            currentHeight += step.getDecoratorHeight();

            View innerView = step.getInnerView();
            measureActiveView(step, innerView, widthMeasureSpec, heightMeasureSpec, currentHeight);
            int innerHeight = calculateActiveHeight(step, innerView);
            activeViewsHeight += innerHeight;
            currentHeight += innerHeight;

            View continueButton = step.getContinueButton();
            measureActiveView(step, continueButton, widthMeasureSpec, heightMeasureSpec, currentHeight);
            int continueHeight = calculateActiveHeight(step, continueButton);
            activeViewsHeight += continueHeight;
            currentHeight += continueHeight;

            step.setActiveViewsHeight(activeViewsHeight);

            currentHeight += step.getBottomMarginHeight();
        }
    }

    @VisibleForTesting
    void measureActiveView(Step step, View activeView, int parentWms, int parentHms, int currentHeight) {
        LayoutParams lp = (LayoutParams) activeView.getLayoutParams();
        int activeViewUsedWidth = calculateHorizontalPadding() + step.calculateHorizontalUsedSpace(activeView);
        int activeViewWms = nonStaticGetChildMeasureSpec(parentWms, activeViewUsedWidth, lp.width);

        int activeViewUsedHeight = step.calculateVerticalUsedSpace(activeView) + currentHeight;
        int activeViewHms = nonStaticGetChildMeasureSpec(parentHms, activeViewUsedHeight, lp.height);

        activeView.measure(activeViewWms, activeViewHms);
    }

    /***
     * This is simply a non-static version of {@link ViewGroup#getChildMeasureSpec(int, int, int)} for testing.
     */
    @VisibleForTesting
    int nonStaticGetChildMeasureSpec(int spec, int padding, int childDimension) {
        return ViewGroup.getChildMeasureSpec(spec, padding, childDimension);
    }

    @VisibleForTesting
    int calculateActiveHeight(Step step, View activeView) {
        if (step.isActive()) {
            return activeView.getMeasuredHeight() + step.calculateVerticalUsedSpace(activeView);
        }
        return 0;
    }

    @VisibleForTesting
    int calculateWidth() {
        return calculateHorizontalPadding() + calculateMaxStepWidth();
    }

    @VisibleForTesting
    int calculateMaxStepWidth() {
        int width = 0;
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            Step step = steps.get(i);

            width = Math.max(width, step.calculateStepDecoratorWidth());

            View innerView = step.getInnerView();
            int innerViewHorizontalPadding = step.calculateHorizontalUsedSpace(innerView);
            width = Math.max(width, innerView.getMeasuredWidth() + innerViewHorizontalPadding);

            AppCompatButton continueButton = step.getContinueButton();
            int continueHorizontalPadding = step.calculateHorizontalUsedSpace(continueButton);
            width = Math.max(width, continueButton.getMeasuredWidth() + continueHorizontalPadding);
        }
        return width;
    }

    @VisibleForTesting
    int calculateHeight() {
        int height = calculateVerticalPadding();
        for (Step step : steps) {
            height += step.getDecoratorHeight();
            height += step.getChildrenVisibleHeight();
            height += step.getBottomMarginHeight();
        }
        return height;
    }

    @VisibleForTesting
    void measureTouchViews(int width) {
        for (Step s : steps) {
            measureTouchView(width, s.getTouchViewHeight(), s.getTouchView());
        }
    }

    private void measureTouchView(int width, int height, InternalTouchView view) {
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        view.measure(wms, hms);
    }

    @VisibleForTesting
    int calculateHorizontalPadding() {
        return outerHorizontalPadding + outerHorizontalPadding + getPaddingLeft() + getPaddingRight();
    }

    @VisibleForTesting
    int calculateVerticalPadding() {
        return outerVerticalPadding + outerVerticalPadding + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (steps.isEmpty()) {
            return;
        }
        Rect rect = steps.get(0).getTempRectForLayout();
        rect.set(getPaddingLeft() + outerHorizontalPadding,
                getPaddingTop() + outerVerticalPadding,
                right - left - getPaddingRight() - outerHorizontalPadding,
                bottom - top - getPaddingBottom() - outerVerticalPadding);
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            Step step = steps.get(i);

            layoutTouchView(rect, step.getTouchView());

            if (step.isActive()) {
                layoutActiveViews(rect, step);
            }
            rect.top += step.calculateYDistanceToNextStep();
        }
    }

    @VisibleForTesting
    void layoutTouchView(Rect rect, InternalTouchView touchView) {
        // The touch view isn't clipped to the outer padding for so offset it.
        int touchLeft = rect.left - outerHorizontalPadding;

        int touchTop = rect.top - outerVerticalPadding;

        int touchRight = rect.right + outerHorizontalPadding;

        int touchBottomMax = rect.bottom + outerVerticalPadding;
        int touchBottom = Math.min(touchTop + touchView.getMeasuredHeight(), touchBottomMax);

        touchView.layout(touchLeft, touchTop, touchRight, touchBottom);
    }

    @VisibleForTesting
    void layoutActiveViews(Rect rect, Step step) {
        int originalLeft = rect.left;
        int originalTop = rect.top;

        rect.left += step.calculateStepDecoratorIconWidth();
        rect.top += step.calculateYDistanceToTextBottom();

        layoutInnerView(rect, step);

        rect.top += step.getInnerView().getHeight();
        layoutNavButtons(rect, step);

        rect.left = originalLeft;
        rect.top = originalTop;
    }

    @VisibleForTesting
    void layoutInnerView(Rect rect, Step step) {
        layoutActiveView(rect, step.getInnerView());
    }

    @VisibleForTesting
    void layoutNavButtons(Rect rect, Step step) {
        layoutActiveView(rect, step.getContinueButton());
    }

    @VisibleForTesting
    void layoutActiveView(Rect rect, View activeView) {
        LayoutParams lp = (LayoutParams) activeView.getLayoutParams();

        int activeLeft = rect.left + lp.leftMargin;

        int activeTop = rect.top + lp.topMargin;

        int activeRightMax = rect.right - lp.rightMargin;
        int activeRight = Math.min(activeLeft + activeView.getMeasuredWidth(), activeRightMax);

        int activeBottomMax = rect.bottom - lp.bottomMargin;
        int activeBottom = Math.min(activeTop + activeView.getMeasuredHeight(), activeBottomMax);

        activeView.layout(activeLeft, activeTop, activeRight, activeBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        doDraw(canvas);
    }

    @VisibleForTesting
    void doDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(outerHorizontalPadding + getPaddingLeft(), outerVerticalPadding + getPaddingTop());
        int dyToNextStep = 0;
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            canvas.translate(0, dyToNextStep);

            canvas.save();

            int stepNumber = i + 1;
            Step step = steps.get(i);

            drawIcon(canvas, step, stepNumber);

            drawText(canvas, step);

            boolean hasMoreSteps = stepNumber < innerViewsSize;
            if (hasMoreSteps) {
                dyToNextStep = step.calculateYDistanceToNextStep();

                drawConnector(canvas, step, dyToNextStep);
            }

            canvas.restore();
        }
        canvas.translate(outerHorizontalPadding + getPaddingRight(), outerVerticalPadding + getPaddingBottom());
        canvas.restore();
    }

    @VisibleForTesting
    void drawIcon(Canvas canvas, Step step, int stepNumber) {
        canvas.save();

        drawIconBackground(canvas, step);
        drawIconText(canvas, step, stepNumber);

        canvas.restore();
    }

    @VisibleForTesting
    void drawIconBackground(Canvas canvas, Step step) {
        canvas.drawArc(step.getTempRectForIconBackground(), 0f, 360f, true, step.getIconBackground());
    }

    @VisibleForTesting
    void drawIconText(Canvas canvas, Step step, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);
        TextPaint iconTextPaint = step.getIconTextPaint();
        int iconDimension = step.getIconDimension();

        PointF center = step.getTempPointForIconTextCenter();
        ViewUtils.findTextCenterStartPoint(stepNumberString, iconDimension, iconDimension, iconTextPaint,
                step.getTempRectForIconTextBounds(), center);

        canvas.drawText(stepNumberString, center.x, center.y, iconTextPaint);
    }

    @VisibleForTesting
    void drawText(Canvas canvas, Step step) {
        canvas.save();

        canvas.translate(step.calculateStepDecoratorIconWidth(), 0);

        drawTitle(canvas, step);
        drawSummary(canvas, step);

        canvas.restore();
    }

    @VisibleForTesting
    void drawTitle(Canvas canvas, Step step) {
        TextPaint paint = step.getTitleTextPaint();
        canvas.drawText(step.getTitle(), 0, step.getTitleBaselineRelativeToStepTop(), paint);
    }

    @VisibleForTesting
    void drawSummary(Canvas canvas, Step step) {
        if (!TextUtils.isEmpty(step.getSummary()) && !step.isActive() && step.isComplete()) {
            canvas.translate(0, step.getTitleBottomRelativeToStepTop());
            canvas.drawText(step.getSummary(), 0,
                    step.getSummaryBaselineRelativeToTitleBottom(), step.getSummaryTextPaint());
        }
        // TODO Handle optional case
    }

    @VisibleForTesting
    void drawConnector(Canvas canvas, Step step, int yDistanceToNextStep) {
        canvas.save();

        int iconDimension = step.getIconDimension();
        int iconMarginVertical = step.getIconMarginVertical();
        Paint connectorPaint = step.getConnectorPaint();
        float connectorWidth = connectorPaint.getStrokeWidth();

        canvas.translate(ViewUtils.findCenterStartX(connectorWidth, iconDimension), 0);
        float startY = iconDimension + iconMarginVertical;
        float stopY = yDistanceToNextStep - iconMarginVertical;
        canvas.drawLine(0, startY, 0, stopY, connectorPaint);

        canvas.restore();
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

        private static final String EMPTY_TITLE = " ";
        private String title;
        private String summary;

        LayoutParams(Context c, AttributeSet attrs) {
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
        }

        LayoutParams(int width, int height) {
            super(width, height);
            title = EMPTY_TITLE;
        }

        LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            title = EMPTY_TITLE;
        }

        String getTitle() {
            return title;
        }

        String getSummary() {
            return summary;
        }
    }

    @VisibleForTesting
    static class InternalTouchView extends View {
        public InternalTouchView(Context context) {
            super(context);
        }
    }
}
