package com.snowble.android.verticalstepper;

import android.annotation.SuppressLint;
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
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerticalStepper extends ViewGroup {
    private Context context;
    private Resources resources;
    @VisibleForTesting
    Step.Common commonStepValues;

    @VisibleForTesting
    List<Step> steps;

    @VisibleForTesting
    int outerHorizontalPadding;
    @VisibleForTesting
    int outerVerticalPadding;

    private RectF tmpRectIconBackground;
    private Rect tmpRectIconTextBounds;

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
        commonStepValues = new Step.Common();

        initPropertiesFromAttrs(attrs, defStyleAttr, defStyleRes);
        initPadding();
        initIconProperties();
        initTitleProperties();
        initSummaryProperties();
        initTouchViewProperties();
        initConnectorProperties();

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
        int defaultActiveColor = getResolvedAttributeData(R.attr.colorPrimary, R.color.bg_active_icon);
        int iconActiveColor = a.getColor(R.styleable.VerticalStepper_iconColorActive,
                ResourcesCompat.getColor(resources, defaultActiveColor, context.getTheme()));
        int iconInactiveColor = a.getColor(R.styleable.VerticalStepper_iconColorInactive,
                ResourcesCompat.getColor(resources, R.color.bg_inactive_icon, context.getTheme()));

        commonStepValues
                .setIconActiveBackgroundPaint(createIconBackground(iconActiveColor))
                .setIconInactiveBackgroundPaint(createIconBackground(iconInactiveColor));
    }

    @SuppressLint("PrivateResource") // https://code.google.com/p/android/issues/detail?id=230985
    private void initNavButtonPropertiesFromAttrs(TypedArray a) {
        int continueButtonStyle = a.getResourceId(
                R.styleable.VerticalStepper_continueButtonStyle, R.style.Widget_AppCompat_Button_Colored);
        commonStepValues
                .setContinueButtonStyle(continueButtonStyle)
                .setContinueButtonContextWrapper(new ContextThemeWrapper(context, continueButtonStyle));
    }

    private void initPadding() {
        outerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_horizontal);
        outerVerticalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_vertical);

        commonStepValues
                .setInactiveBottomMarginToNextStep(
                        resources.getDimensionPixelSize(R.dimen.inactive_bottom_margin_to_next_step))
                .setActiveBottomMarginToNextStep(
                        resources.getDimensionPixelSize(R.dimen.active_bottom_margin_to_next_step));
    }

    private void initIconProperties() {
        initIconDimension();
        initIconMargins();
        initIconTextPaint();
        initIconTmpObjects();
    }

    private void initIconDimension() {
        commonStepValues.setIconDimension(resources.getDimensionPixelSize(R.dimen.icon_diameter));
    }

    private void initIconMargins() {
        commonStepValues
                .setIconMarginRight(resources.getDimensionPixelSize(R.dimen.icon_margin_right))
                .setIconMarginVertical(resources.getDimensionPixelSize(R.dimen.icon_margin_vertical));
    }

    private Paint createIconBackground(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }

    private void initIconTextPaint() {
        commonStepValues.setIconTextPaint(createTextPaint(R.color.white, R.dimen.icon_font_size));
    }

    private void initIconTmpObjects() {
        int iconDimension = commonStepValues.getIconDimension();
        tmpRectIconBackground = new RectF(0, 0, iconDimension, iconDimension);
        tmpRectIconTextBounds = new Rect();
    }

    private void initTitleProperties() {
        initTitleDimensions();
        initTitleTextPaint();
    }

    private void initTitleDimensions() {
        commonStepValues.setTitleMarginBottomToInnerView(
                resources.getDimensionPixelSize(R.dimen.title_margin_bottom_to_inner_view));
    }

    private void initTitleTextPaint() {
        TextPaint paint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        commonStepValues
                .setTitleActiveTextPaint(paint)
                .setTitleInactiveTextPaint(createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size));
    }

    private void initSummaryProperties() {
        initSummaryTextPaint();
    }

    private void initSummaryTextPaint() {
        commonStepValues.setSummaryTextPaint(createTextPaint(R.color.summary_color, R.dimen.summary_font_size));
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
        commonStepValues
                .setTouchViewHeight(resources.getDimensionPixelSize(R.dimen.touch_height))
                .setTouchViewBackground(getResolvedAttributeData(R.attr.selectableItemBackground, 0));
    }

    @VisibleForTesting
    int getResolvedAttributeData(int attr, int defaultData) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, false);
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
        commonStepValues.setConnectorWidth(resources.getDimensionPixelSize(R.dimen.connector_width));
    }

    private void initConnectorPaint() {
        Paint connectorPaint = new Paint();
        setPaintColor(connectorPaint, R.color.connector_color);
        connectorPaint.setAntiAlias(true);
        connectorPaint.setStrokeWidth(commonStepValues.getConnectorWidth());
        commonStepValues.setConnectorPaint(connectorPaint);
    }

    private void setPaintColor(Paint paint, int colorRes) {
        int color = ResourcesCompat.getColor(resources, colorRes, context.getTheme());
        paint.setColor(color);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initStepViews();
    }

    @VisibleForTesting
    void initStepViews() {
        commonStepValues.validate();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            initStepView(new Step(getChildAt(i), new InternalTouchView(context),
                    new AppCompatButton(commonStepValues.getContinueButtonContextWrapper(), null, 0), commonStepValues));
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
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            steps.get(i).measureBottomMarginToNextStep(i == innerViewsSize - 1);
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

            LayoutParams lp = getInternalLayoutParams(innerView);
            int innerViewHorizontalPadding = step.calculateInnerViewHorizontalUsedSpace();

            width = Math.max(width, step.calculateStepDecoratorWidth());

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
            measureTouchView(width, v.getTouchView());
        }
    }

    @VisibleForTesting
    void measureTouchView(int width, InternalTouchView view) {
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(commonStepValues.getTouchViewHeight(), MeasureSpec.EXACTLY);
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

                int buttonsTop = currentTop + calculateYDistanceToButtons(step);

                layoutNavButtons(left, buttonsTop, right, bottom, step, isLastStep);
            }
            currentTop += calculateYDistanceToNextStep(step, isLastStep);
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
        for (int i = 0, innerViewsSize = steps.size(); i < innerViewsSize; i++) {
            canvas.save();

            int stepNumber = i + 1;
            Step step = steps.get(i);

            canvas.save();
            drawIcon(canvas, step, stepNumber);
            canvas.restore();

            canvas.save();
            drawText(canvas, step);
            canvas.restore();

            int dyToNextStep = calculateYDistanceToNextStep(step, i == innerViewsSize - 1);
            boolean hasMoreSteps = stepNumber < innerViewsSize;
            if (hasMoreSteps) {
                canvas.save();
                drawConnector(canvas, dyToNextStep);
                canvas.restore();
            }

            canvas.restore();
            if (hasMoreSteps) {
                canvas.translate(0, dyToNextStep);
            }
        }
        canvas.restore();
    }

    private int calculateYDistanceToNextStep(Step step, boolean isLastStep) {
        int dyToNextStep = calculateYDistanceToButtons(step);
        if (step.isActive()) {
            dyToNextStep += step.getContinueButton().getHeight();
        }
        dyToNextStep += step.getBottomMarginToNextStep(isLastStep);
        return dyToNextStep;
    }

    private int calculateYDistanceToButtons(Step step) {
        int dyToButtons = calculateYDistanceToTextBottom(step);
        if (step.isActive()) {
            dyToButtons += step.getInnerView().getHeight() + commonStepValues.getTitleMarginBottomToInnerView();
        }
        return dyToButtons;
    }

    private int calculateYDistanceToTextBottom(Step step) {
        int dyToTextBottom = (int) step.getTitleBottomRelativeToStepTop();
        if (!step.isActive()) {
            dyToTextBottom += step.getSummaryBottomRelativeToTitleBottom();
        }
        return dyToTextBottom;
    }

    private void drawIcon(Canvas canvas, Step step, int stepNumber) {
        drawIconBackground(canvas, step);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas, Step step) {
        canvas.drawArc(tmpRectIconBackground, 0f, 360f, true, step.getIconColor());
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);
        TextPaint iconTextPaint = commonStepValues.getIconTextPaint();
        int iconDimension = commonStepValues.getIconDimension();

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

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

    @VisibleForTesting
    static class Step {

        @VisibleForTesting
        static final int ZERO_SIZE_MARGIN = 0;

        private static final Rect TMP_RECT_TITLE_TEXT_BOUNDS = new Rect();

        @NonNull
        private final InternalTouchView touchView;
        @NonNull
        private final AppCompatButton continueButton;
        @NonNull
        private final View innerView;
        @NonNull
        private String title;
        @Nullable
        private String summary;
        private boolean active;

        @NonNull
        private final Common common;

        private int decoratorHeight;
        private int bottomMarginHeight;
        private int childrenVisibleHeight;

        private float titleWidth;
        private float titleBaselineRelativeToStepTop;
        private float titleBottomRelativeToStepTop;

        private float summaryWidth;
        private float summaryBaselineRelativeToTitleBottom;
        private float summaryBottomRelativeToTitleBottom;

        Step(@NonNull View innerView, @NonNull InternalTouchView touchView,
             @NonNull AppCompatButton continueButton, @NonNull Common common) {
            this.innerView = innerView;
            this.touchView = touchView;
            this.continueButton = continueButton;
            initTextValues((LayoutParams) innerView.getLayoutParams());
            validateTitle();
            this.active = false;
            this.common = common;
        }

        @VisibleForTesting
        void initTextValues(@NonNull  LayoutParams lp) {
            this.title = lp.getTitle();
            this.summary = lp.getSummary();
        }

        @VisibleForTesting
        void validateTitle() {
            if (TextUtils.isEmpty(title)) {
                throw new IllegalArgumentException("step_title cannot be empty.");
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Step step = (Step) o;

            if (active != step.active) return false;
            if (decoratorHeight != step.decoratorHeight) return false;
            if (bottomMarginHeight != step.bottomMarginHeight) return false;
            if (childrenVisibleHeight != step.childrenVisibleHeight) return false;
            if (Float.compare(step.titleWidth, titleWidth) != 0) return false;
            if (Float.compare(step.titleBaselineRelativeToStepTop, titleBaselineRelativeToStepTop) != 0) return false;
            if (Float.compare(step.titleBottomRelativeToStepTop, titleBottomRelativeToStepTop) != 0) return false;
            if (Float.compare(step.summaryWidth, summaryWidth) != 0) return false;
            if (Float.compare(step.summaryBaselineRelativeToTitleBottom, summaryBaselineRelativeToTitleBottom) != 0)
                return false;
            if (Float.compare(step.summaryBottomRelativeToTitleBottom, summaryBottomRelativeToTitleBottom) != 0)
                return false;
            if (!touchView.equals(step.touchView)) return false;
            if (!continueButton.equals(step.continueButton)) return false;
            if (!innerView.equals(step.innerView)) return false;
            if (!title.equals(step.title)) return false;
            if (summary != null ? !summary.equals(step.summary) : step.summary != null) return false;
            return common.equals(step.common);

        }

        @Override
        public int hashCode() {
            int result = touchView.hashCode();
            result = 31 * result + continueButton.hashCode();
            result = 31 * result + innerView.hashCode();
            result = 31 * result + title.hashCode();
            result = 31 * result + (summary != null ? summary.hashCode() : 0);
            result = 31 * result + (active ? 1 : 0);
            result = 31 * result + common.hashCode();
            result = 31 * result + decoratorHeight;
            result = 31 * result + bottomMarginHeight;
            result = 31 * result + childrenVisibleHeight;
            result = 31 * result + (titleWidth != +0.0f ? Float.floatToIntBits(titleWidth) : 0);
            result = 31 * result + (titleBaselineRelativeToStepTop != +0.0f ? Float.floatToIntBits(titleBaselineRelativeToStepTop) : 0);
            result = 31 * result + (titleBottomRelativeToStepTop != +0.0f ? Float.floatToIntBits(titleBottomRelativeToStepTop) : 0);
            result = 31 * result + (summaryWidth != +0.0f ? Float.floatToIntBits(summaryWidth) : 0);
            result = 31 * result + (summaryBaselineRelativeToTitleBottom != +0.0f ? Float.floatToIntBits(summaryBaselineRelativeToTitleBottom) : 0);
            result = 31 * result + (summaryBottomRelativeToTitleBottom != +0.0f ? Float.floatToIntBits(summaryBottomRelativeToTitleBottom) : 0);
            return result;
        }

        @NonNull
        View getInnerView() {
            return innerView;
        }

        @NonNull
        InternalTouchView getTouchView() {
            return touchView;
        }

        @NonNull
        AppCompatButton getContinueButton() {
            return continueButton;
        }

        boolean isActive() {
            return active;
        }

        void setActive(boolean active) {
            this.active = active;
        }

        int getDecoratorHeight() {
            return decoratorHeight;
        }

        void setDecoratorHeight(int decoratorHeight) {
            this.decoratorHeight = decoratorHeight;
        }

        int getBottomMarginHeight() {
            return bottomMarginHeight;
        }

        void setBottomMarginHeight(int bottomMarginHeight) {
            this.bottomMarginHeight = bottomMarginHeight;
        }

        int getChildrenVisibleHeight() {
            return childrenVisibleHeight;
        }

        void setChildrenVisibleHeight(int childrenVisibleHeight) {
            this.childrenVisibleHeight = childrenVisibleHeight;
        }

        @NonNull
        String getTitle() {
            return title;
        }

        void setTitle(@NonNull String title) {
            this.title = title;
        }

        float getTitleWidth() {
            return titleWidth;
        }

        @Nullable
        String getSummary() {
            return summary;
        }

        float getSummaryWidth() {
            return summaryWidth;
        }

        float getTitleBaselineRelativeToStepTop() {
            return titleBaselineRelativeToStepTop;
        }

        float getTitleBottomRelativeToStepTop() {
            return titleBottomRelativeToStepTop;
        }

        float getSummaryBaselineRelativeToTitleBottom() {
            return summaryBaselineRelativeToTitleBottom;
        }

        float getSummaryBottomRelativeToTitleBottom() {
            return summaryBottomRelativeToTitleBottom;
        }

        void measureTitleHorizontalDimensions() {
            float width = 0f;
            if (!TextUtils.isEmpty(title)) {
                width = getTitleTextPaint().measureText(title);
            }
            titleWidth = width;
        }

        void measureSummaryHorizontalDimensions() {
            float width = 0f;
            if (!TextUtils.isEmpty(summary)) {
                width = common.getSummaryTextPaint().measureText(summary);
            }
            summaryWidth = width;
        }

        void measureTitleVerticalDimensions(int heightToCenterIn) {
            TextPaint titlePaint = getTitleTextPaint();
            measureTitleBaseline(heightToCenterIn);
            titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + titlePaint.getFontMetrics().bottom;
        }

        private void measureTitleBaseline(int heightToCenterIn) {
            TextPaint titlePaint = getTitleTextPaint();
            titlePaint.getTextBounds(title, 0, 1, TMP_RECT_TITLE_TEXT_BOUNDS);
            titleBaselineRelativeToStepTop = (heightToCenterIn / 2) + (TMP_RECT_TITLE_TEXT_BOUNDS.height() / 2);
        }

        void measureSummaryVerticalDimensions() {
            TextPaint summaryPaint = common.getSummaryTextPaint();
            measureSummaryBaseline();
            summaryBottomRelativeToTitleBottom =
                    summaryBaselineRelativeToTitleBottom + summaryPaint.getFontMetrics().bottom;
        }

        private void measureSummaryBaseline() {
            summaryBaselineRelativeToTitleBottom = -common.getSummaryTextPaint().getFontMetrics().ascent;
        }

        TextPaint getTitleTextPaint() {
            return active ? common.getTitleActiveTextPaint() : common.getTitleInactiveTextPaint();
        }

        void measureBottomMarginToNextStep(boolean isLastStep) {
            setBottomMarginHeight(getBottomMarginToNextStep(isLastStep));
        }

        int getBottomMarginToNextStep(boolean isLastStep) {
            if (isLastStep) {
                return ZERO_SIZE_MARGIN;
            } else {
                return active ? common.getActiveBottomMarginToNextStep()
                        : common.getInactiveBottomMarginToNextStep();
            }
        }

        Paint getIconColor() {
            return active ? common.getIconActiveBackgroundPaint() : common.getIconInactiveBackgroundPaint();
        }

        int calculateInnerViewHorizontalUsedSpace() {
            LayoutParams lp = (LayoutParams) innerView.getLayoutParams();
            return calculateStepDecoratorIconWidth() + lp.leftMargin + lp.rightMargin;
        }

        int calculateInnerViewVerticalUsedSpace() {
            LayoutParams lp = (LayoutParams) innerView.getLayoutParams();
            return lp.topMargin + lp.bottomMargin;
        }

        int calculateStepDecoratorWidth() {
            return calculateStepDecoratorIconWidth() + (int) calculateStepDecoratorTextWidth();
        }

        int calculateStepDecoratorIconWidth() {
            return common.getIconDimension() + common.getIconMarginRight();
        }

        float calculateStepDecoratorTextWidth() {
            measureTitleHorizontalDimensions();
            measureSummaryHorizontalDimensions();
            return Math.max(getTitleWidth(), getSummaryWidth());
        }

        void measureStepDecoratorHeight() {
            int iconDimension = common.getIconDimension();
            measureTitleVerticalDimensions(iconDimension);
            measureSummaryVerticalDimensions();
            int textTotalHeight = (int) (getTitleBottomRelativeToStepTop()
                    + getSummaryBottomRelativeToTitleBottom());
            setDecoratorHeight(Math.max(iconDimension, textTotalHeight));
        }

        static class Common {
            private static final int INVALID_INT = -1;

            private TextPaint titleActiveTextPaint = null;
            private TextPaint titleInactiveTextPaint = null;
            private int titleMarginBottomToInnerView = INVALID_INT;

            private TextPaint summaryTextPaint = null;

            private Paint iconActiveBackgroundPaint = null;
            private Paint iconInactiveBackgroundPaint = null;
            private TextPaint iconTextPaint = null;
            private int iconDimension = INVALID_INT;
            private int iconMarginRight = INVALID_INT;
            private int iconMarginVertical = INVALID_INT;

            private int activeBottomMarginToNextStep = INVALID_INT;
            private int inactiveBottomMarginToNextStep = INVALID_INT;

            private int touchViewHeight = INVALID_INT;
            private int touchViewBackground = INVALID_INT;

            private int continueButtonStyle = INVALID_INT;
            private ContextThemeWrapper continueButtonContextWrapper = null;

            private int connectorWidth = INVALID_INT;
            private Paint connectorPaint = null;

            public TextPaint getTitleActiveTextPaint() {
                return titleActiveTextPaint;
            }

            public Common setTitleActiveTextPaint(TextPaint titleActiveTextPaint) {
                this.titleActiveTextPaint = titleActiveTextPaint;
                return this;
            }

            public TextPaint getTitleInactiveTextPaint() {
                return titleInactiveTextPaint;
            }

            public Common setTitleInactiveTextPaint(TextPaint titleInactiveTextPaint) {
                this.titleInactiveTextPaint = titleInactiveTextPaint;
                return this;
            }

            public int getTitleMarginBottomToInnerView() {
                return titleMarginBottomToInnerView;
            }

            public Common setTitleMarginBottomToInnerView(int titleMarginBottomToInnerView) {
                this.titleMarginBottomToInnerView = titleMarginBottomToInnerView;
                return this;
            }

            public TextPaint getSummaryTextPaint() {
                return summaryTextPaint;
            }

            public Common setSummaryTextPaint(TextPaint summaryTextPaint) {
                this.summaryTextPaint = summaryTextPaint;
                return this;
            }

            public Paint getIconActiveBackgroundPaint() {
                return iconActiveBackgroundPaint;
            }

            public Common setIconActiveBackgroundPaint(Paint iconActiveBackgroundPaint) {
                this.iconActiveBackgroundPaint = iconActiveBackgroundPaint;
                return this;
            }

            public Paint getIconInactiveBackgroundPaint() {
                return iconInactiveBackgroundPaint;
            }

            public Common setIconInactiveBackgroundPaint(Paint iconInactiveBackgroundPaint) {
                this.iconInactiveBackgroundPaint = iconInactiveBackgroundPaint;
                return this;
            }

            public TextPaint getIconTextPaint() {
                return iconTextPaint;
            }

            public Common setIconTextPaint(TextPaint iconTextPaint) {
                this.iconTextPaint = iconTextPaint;
                return this;
            }

            public int getIconDimension() {
                return iconDimension;
            }

            public Common setIconDimension(int iconDimension) {
                this.iconDimension = iconDimension;
                return this;
            }

            public int getIconMarginRight() {
                return iconMarginRight;
            }

            public Common setIconMarginRight(int iconMarginRight) {
                this.iconMarginRight = iconMarginRight;
                return this;
            }

            public int getIconMarginVertical() {
                return iconMarginVertical;
            }

            public Common setIconMarginVertical(int iconMarginVertical) {
                this.iconMarginVertical = iconMarginVertical;
                return this;
            }

            public int getActiveBottomMarginToNextStep() {
                return activeBottomMarginToNextStep;
            }

            public Common setActiveBottomMarginToNextStep(int activeBottomMarginToNextStep) {
                this.activeBottomMarginToNextStep = activeBottomMarginToNextStep;
                return this;
            }

            public int getInactiveBottomMarginToNextStep() {
                return inactiveBottomMarginToNextStep;
            }

            public Common setInactiveBottomMarginToNextStep(int inactiveBottomMarginToNextStep) {
                this.inactiveBottomMarginToNextStep = inactiveBottomMarginToNextStep;
                return this;
            }

            public int getTouchViewHeight() {
                return touchViewHeight;
            }

            public Common setTouchViewHeight(int touchViewHeight) {
                this.touchViewHeight = touchViewHeight;
                return this;
            }

            public int getTouchViewBackground() {
                return touchViewBackground;
            }

            public Common setTouchViewBackground(int touchViewBackground) {
                this.touchViewBackground = touchViewBackground;
                return this;
            }

            public int getContinueButtonStyle() {
                return continueButtonStyle;
            }

            public Common setContinueButtonStyle(int continueButtonStyle) {
                this.continueButtonStyle = continueButtonStyle;
                return this;
            }

            public ContextThemeWrapper getContinueButtonContextWrapper() {
                return continueButtonContextWrapper;
            }

            public Common setContinueButtonContextWrapper(ContextThemeWrapper continueButtonContextWrapper) {
                this.continueButtonContextWrapper = continueButtonContextWrapper;
                return this;
            }

            public int getConnectorWidth() {
                return connectorWidth;
            }

            public Common setConnectorWidth(int connectorWidth) {
                this.connectorWidth = connectorWidth;
                return this;
            }

            public Paint getConnectorPaint() {
                return connectorPaint;
            }

            public Common setConnectorPaint(Paint connectorPaint) {
                this.connectorPaint = connectorPaint;
                return this;
            }

            void validate() {
                validateTitleValues();
                validateSummaryValues();
                validateIconValues();
                validateTouchValues();
                validateContinueButtonValues();
                validateConnectorValues();
                validateBottomMargin();
            }

            private void validateTitleValues() {
                if (titleActiveTextPaint == null) {
                    throw new IllegalStateException("titleActiveTextPaint must be set.");
                }
                if (titleInactiveTextPaint == null) {
                    throw new IllegalStateException("titleInactiveTextPaint must be set.");
                }
                if (titleMarginBottomToInnerView == INVALID_INT) {
                    throw new IllegalStateException("titleMarginBottomToInnerView must be set.");
                }
            }

            private void validateSummaryValues() {
                if (summaryTextPaint == null) {
                    throw new IllegalStateException("summaryTextPaint must be set.");
                }
            }

            private void validateIconValues() {
                if (iconActiveBackgroundPaint == null) {
                    throw new IllegalStateException("iconActiveBackgroundPaint must be set.");
                }
                if (iconInactiveBackgroundPaint == null) {
                    throw new IllegalStateException("iconInactiveBackgroundPaint must be set.");
                }
                if (iconTextPaint == null) {
                    throw new IllegalStateException("iconTextPaint must be set.");
                }
                if (iconDimension == INVALID_INT) {
                    throw new IllegalStateException("iconDimension must be set.");
                }
                if (iconMarginRight == INVALID_INT) {
                    throw new IllegalStateException("iconMarginRight must be set.");
                }
                if (iconMarginVertical == INVALID_INT) {
                    throw new IllegalStateException("iconMarginVertical must be set.");
                }
            }

            private void validateTouchValues() {
                if (touchViewBackground == INVALID_INT) {
                    throw new IllegalStateException("touchViewBackground must be set.");
                }
                if (touchViewHeight == INVALID_INT) {
                    throw new IllegalStateException("touchViewHeight must be set.");
                }
            }

            private void validateContinueButtonValues() {
                if (continueButtonContextWrapper == null) {
                    throw new IllegalStateException("continueButtonContextWrapper must be set.");
                }
                if (continueButtonStyle == INVALID_INT) {
                    throw new IllegalStateException("continueButtonStyle must be set.");
                }
            }

            private void validateConnectorValues() {
                if (connectorPaint == null) {
                    throw new IllegalStateException("connectorPaint must be set.");
                }
                if (connectorWidth == INVALID_INT) {
                    throw new IllegalStateException("connectorWidth must be set.");
                }
            }

            private void validateBottomMargin() {
                if (activeBottomMarginToNextStep == INVALID_INT) {
                    throw new IllegalStateException("activeBottomMarginToNextStep must be set.");
                }
                if (inactiveBottomMarginToNextStep == INVALID_INT) {
                    throw new IllegalStateException("inactiveBottomMarginToNextStep must be set.");
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Common that = (Common) o;

                if (activeBottomMarginToNextStep != that.activeBottomMarginToNextStep) return false;
                if (inactiveBottomMarginToNextStep != that.inactiveBottomMarginToNextStep) return false;
                if (titleActiveTextPaint != null ? !titleActiveTextPaint.equals(that.titleActiveTextPaint) : that.titleActiveTextPaint != null)
                    return false;
                if (titleInactiveTextPaint != null ? !titleInactiveTextPaint.equals(that.titleInactiveTextPaint) : that.titleInactiveTextPaint != null)
                    return false;
                if (iconActiveBackgroundPaint != null ? !iconActiveBackgroundPaint.equals(that.iconActiveBackgroundPaint) : that.iconActiveBackgroundPaint != null)
                    return false;
                return iconInactiveBackgroundPaint != null ? iconInactiveBackgroundPaint.equals(that.iconInactiveBackgroundPaint) : that.iconInactiveBackgroundPaint == null;
            }

            @Override
            public int hashCode() {
                int result = titleActiveTextPaint != null ? titleActiveTextPaint.hashCode() : 0;
                result = 31 * result + (titleInactiveTextPaint != null ? titleInactiveTextPaint.hashCode() : 0);
                result = 31 * result + (iconActiveBackgroundPaint != null ? iconActiveBackgroundPaint.hashCode() : 0);
                result = 31 * result + (iconInactiveBackgroundPaint != null ? iconInactiveBackgroundPaint.hashCode() : 0);
                result = 31 * result + activeBottomMarginToNextStep;
                result = 31 * result + inactiveBottomMarginToNextStep;
                return result;
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {

        private static final String EMPTY_TITLE = " ";
        private String title;
        private String summary;

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
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            title = EMPTY_TITLE;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
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
