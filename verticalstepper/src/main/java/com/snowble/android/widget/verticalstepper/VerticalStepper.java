package com.snowble.android.widget.verticalstepper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
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

        commonStepValues = new Step.Common(context, iconActiveColor, iconInactiveColor);
        steps = new ArrayList<>();
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
        initStepViews();
    }

    @VisibleForTesting
    void initStepViews() {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, continueButtonStyle);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            initStepView(new Step(getChildAt(i), new InternalTouchView(context),
                    new AppCompatButton(contextWrapper, null, 0), commonStepValues));
        }

        for (Step v : steps) {
            initTouchView(v);
            initNavButtons(v);
        }
    }

    @VisibleForTesting
    void initStepView(Step step) {
        steps.add(step);
        step.getInnerView().setVisibility(View.GONE);
    }

    @VisibleForTesting
    void initTouchView(final Step step) {
        InternalTouchView touchView = step.getTouchView();
        touchView.setBackgroundResource(commonStepValues.getTouchViewBackground());
        touchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStepExpandedState(step);
            }
        });
        addView(touchView);
    }

    @VisibleForTesting
    void initNavButtons(Step step) {
        AppCompatButton continueButton = step.getContinueButton();
        continueButton.setVisibility(GONE);
        continueButton.setText(R.string.continue_button);
        continueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO collapse current view and expand next view
            }
        });
        addView(continueButton);
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
        measureChildViews(widthMeasureSpec, heightMeasureSpec);
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
    void measureChildViews(int widthMeasureSpec, int heightMeasureSpec) {
        int stepperHorizontalPadding = calculateHorizontalPadding();
        int currentHeight = calculateVerticalPadding();
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            Step step = steps.get(i);
            View innerView = step.getInnerView();

            currentHeight += step.getDecoratorHeight();

            int usedWidthFromPadding = stepperHorizontalPadding + step.calculateInnerViewHorizontalUsedSpace();
            int innerViewVerticalPadding = step.calculateInnerViewVerticalUsedSpace();
            int usedHeight = innerViewVerticalPadding + currentHeight;
            measureInnerView(widthMeasureSpec, heightMeasureSpec, innerView, usedWidthFromPadding, usedHeight);

            int childrenHeight = 0;
            if (step.isActive()) {
                childrenHeight += innerView.getMeasuredHeight() + innerViewVerticalPadding;
            }
            currentHeight += childrenHeight;

            AppCompatButton continueButton = step.getContinueButton();
            measureNavButton(widthMeasureSpec, heightMeasureSpec, continueButton, usedWidthFromPadding, currentHeight);

            if (step.isActive()) {
                childrenHeight += continueButton.getMeasuredHeight();
            }
            step.setChildrenVisibleHeight(childrenHeight);

            currentHeight += step.getBottomMarginHeight();
        }
    }

    private void measureInnerView(int widthMeasureSpec, int heightMeasureSpec, View innerView,
                                  int usedWidthFromPadding, int usedHeight) {
        LayoutParams lp = getInternalLayoutParams(innerView);
        int innerWms =
                getChildMeasureSpec(widthMeasureSpec, usedWidthFromPadding, lp.width);
        int innerHms = getChildMeasureSpec(heightMeasureSpec, usedHeight, lp.height);
        innerView.measure(innerWms, innerHms);
    }

    private void measureNavButton(int widthMeasureSpec, int heightMeasureSpec, AppCompatButton continueButton,
                                  int usedWidth, int usedHeight) {
        // TODO Add margins for buttons
        // TODO Add proper dimensions
        int navButtonsWms = getChildMeasureSpec(widthMeasureSpec, usedWidth, LayoutParams.WRAP_CONTENT);
        int navButtonsHms = getChildMeasureSpec(heightMeasureSpec, usedHeight, LayoutParams.WRAP_CONTENT);
        continueButton.measure(navButtonsWms, navButtonsHms);
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
            View innerView = step.getInnerView();

            width = Math.max(width, step.calculateStepDecoratorWidth());

            int innerViewHorizontalPadding = step.calculateInnerViewHorizontalUsedSpace();

            width = Math.max(width, innerView.getMeasuredWidth() + innerViewHorizontalPadding);

            AppCompatButton continueButton = step.getContinueButton();
            width = Math.max(width, continueButton.getMeasuredWidth() + innerViewHorizontalPadding);
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

    private void measureTouchViews(int width) {
        for (Step v : steps) {
            measureTouchView(width, commonStepValues.getTouchViewHeight(), v.getTouchView());
        }
    }

    @VisibleForTesting
    void measureTouchView(int width, int height, InternalTouchView view) {
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
        int currentTop = top;
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            Step step = steps.get(i);
            boolean isFirstStep = i == 0;
            boolean isLastStep = i == innerViewsSize - 1;

            if (isFirstStep) {
                currentTop += getPaddingTop() + outerVerticalPadding;
            }

            layoutTouchView(left, currentTop, right, bottom, step.getTouchView(), isLastStep);

            if (step.isActive()) {
                layoutInnerView(left, currentTop, right, bottom, step, isLastStep);

                int buttonsTop = currentTop + step.calculateYDistanceToButtons();

                layoutNavButtons(left, buttonsTop, right, bottom, step, isLastStep);
            }
            if (!isLastStep) {
                currentTop += step.calculateYDistanceToNextStep();
            }
        }
    }

    private void layoutTouchView(int left, int topAdjustedForPadding, int right, int bottom,
                                 InternalTouchView touchView, boolean isLastStep) {
        int touchLeft = left + getPaddingLeft();

        // The touch view isn't clipped to the outer padding for the first step so offset touchTop to account for it.
        // Also offset touchTop for the other steps as well so the touch view has a consistent placement.
        int touchTop = topAdjustedForPadding - outerVerticalPadding;

        int touchRight = right - left - getPaddingRight();

        int touchBottomMax;
        if (isLastStep) {
            touchBottomMax = bottom - getPaddingBottom();
        } else {
            touchBottomMax = bottom;
        }
        int touchBottom = Math.min(touchTop + touchView.getMeasuredHeight(), touchBottomMax);

        touchView.layout(touchLeft, touchTop, touchRight, touchBottom);
    }

    private void layoutInnerView(int left, int topAdjustedForPadding, int right, int bottom,
                                 Step step, boolean isLastStep) {
        View innerView = step.getInnerView();
        LayoutParams lp = getInternalLayoutParams(innerView);
        int innerLeft = left + outerHorizontalPadding + getPaddingLeft() + lp.leftMargin
                + step.calculateStepDecoratorIconWidth();

        int innerTop = (int) (topAdjustedForPadding + lp.topMargin + step.getTitleBottomRelativeToStepTop()
                + commonStepValues.getTitleMarginBottomToInnerView());

        int innerRightMax = right - outerHorizontalPadding - getPaddingRight() - lp.rightMargin;
        int innerRight = Math.min(innerLeft + innerView.getMeasuredWidth(), innerRightMax);

        int innerBottomMax;
        if (isLastStep) {
            innerBottomMax = bottom - outerVerticalPadding - getPaddingBottom() - lp.bottomMargin;
        } else {
            innerBottomMax = bottom;
        }
        int innerBottom = Math.min(innerTop + innerView.getMeasuredHeight(), innerBottomMax);

        innerView.layout(innerLeft, innerTop, innerRight, innerBottom);
    }

    private void layoutNavButtons(int left, int currentTop, int right, int bottom,
                                  Step step, boolean isLastStep) {
        // TODO There's quite a bit of common code between this and layoutInnerView. See if it can be consolidated.
        LayoutParams innerViewLp = getInternalLayoutParams(step.getInnerView());
        AppCompatButton button = step.getContinueButton();

        int buttonLeft = left + outerHorizontalPadding + getPaddingLeft() + innerViewLp.leftMargin
                + step.calculateStepDecoratorIconWidth();

        // TODO Add button margins
        int buttonTop = currentTop;

        int buttonRightMax = right - outerHorizontalPadding - getPaddingRight() - innerViewLp.rightMargin;
        int buttonRight = Math.min(buttonLeft + button.getMeasuredWidth(), buttonRightMax);

        int buttonBottomMax;
        if (isLastStep) {
            buttonBottomMax = bottom - outerVerticalPadding - getPaddingBottom() - innerViewLp.bottomMargin;
        } else {
            buttonBottomMax = bottom;
        }
        int buttonBottom = Math.min(buttonTop + button.getMeasuredHeight(), buttonBottomMax);

        button.layout(buttonLeft, buttonTop, buttonRight, buttonBottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(outerHorizontalPadding + getPaddingLeft(), outerVerticalPadding + getPaddingTop());
        int dyToNextStep = 0;
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            canvas.translate(0, dyToNextStep);
            canvas.save();

            int stepNumber = i + 1;
            Step step = steps.get(i);

            canvas.save();
            drawIcon(canvas, step, stepNumber);
            canvas.restore();

            canvas.save();
            drawText(canvas, step);
            canvas.restore();

            boolean hasMoreSteps = stepNumber < innerViewsSize;
            if (hasMoreSteps) {
                dyToNextStep = step.calculateYDistanceToNextStep();

                canvas.save();
                drawConnector(canvas, dyToNextStep);
                canvas.restore();
            }

            canvas.restore();
        }
        canvas.translate(outerHorizontalPadding + getPaddingRight(), outerVerticalPadding + getPaddingBottom());
        canvas.restore();
    }

    private void drawIcon(Canvas canvas, Step step, int stepNumber) {
        drawIconBackground(canvas, step);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas, Step step) {
        canvas.drawArc(commonStepValues.getTempRectIconBackground(), 0f, 360f, true, step.getIconColor());
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);
        TextPaint iconTextPaint = commonStepValues.getIconTextPaint();
        int iconDimension = commonStepValues.getIconDimension();

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

        Rect tmpRectIconTextBounds = commonStepValues.getTempRectIconTextBounds();
        iconTextPaint.getTextBounds(stepNumberString, 0, 1, tmpRectIconTextBounds);
        float centeredTextY = (iconDimension / 2) + (tmpRectIconTextBounds.height() / 2);

        canvas.drawText(stepNumberString, centeredTextX, centeredTextY, iconTextPaint);
    }

    private void drawText(Canvas canvas, Step step) {
        canvas.translate(step.calculateStepDecoratorIconWidth(), 0);

        TextPaint paint = step.getTitleTextPaint();
        canvas.drawText(step.getTitle(), 0, step.getTitleBaselineRelativeToStepTop(), paint);

        if (!TextUtils.isEmpty(step.getSummary()) && !step.isActive()) {
            canvas.translate(0, step.getTitleBottomRelativeToStepTop());
            canvas.drawText(step.getSummary(), 0,
                    step.getSummaryBaselineRelativeToTitleBottom(), commonStepValues.getSummaryTextPaint());
        }
        // TODO Handle optional case
    }

    private void drawConnector(Canvas canvas, int yDistanceToNextStep) {
        int iconDimension = commonStepValues.getIconDimension();
        int iconMarginVertical = commonStepValues.getIconMarginVertical();
        int connectorWidth = commonStepValues.getConnectorWidth();
        Paint connectorPaint = commonStepValues.getConnectorPaint();

        canvas.translate((iconDimension - connectorWidth) / 2, 0);
        float startY = iconDimension + iconMarginVertical;
        float stopY = yDistanceToNextStep - iconMarginVertical;
        canvas.drawLine(0, startY, 0, stopY, connectorPaint);
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
