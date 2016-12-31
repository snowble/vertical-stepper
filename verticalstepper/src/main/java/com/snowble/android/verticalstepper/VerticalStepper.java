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

    @VisibleForTesting
    static final int ZERO_SIZE_MARGIN = 0;

    private Context context;
    private Resources resources;

    @VisibleForTesting
    List<StepView> stepViews;

    @VisibleForTesting
    int outerHorizontalPadding;
    @VisibleForTesting
    int outerVerticalPadding;

    @VisibleForTesting
    int inactiveBottomMarginToNextStep;
    @VisibleForTesting
    int activeBottomMarginToNextStep;

    @VisibleForTesting
    int iconDimension;
    @VisibleForTesting
    int iconMarginRight;
    private int iconMarginVertical;
    @VisibleForTesting
    int iconActiveColor;
    @VisibleForTesting
    int iconInactiveColor;
    private Paint iconActiveBackgroundPaint;
    private Paint iconInactiveBackgroundPaint;
    private RectF tmpRectIconBackground;
    private TextPaint iconTextPaint;
    private Rect tmpRectIconTextBounds;

    @VisibleForTesting
    TextPaint titleActiveTextPaint;
    @VisibleForTesting
    TextPaint titleInactiveTextPaint;
    private TextPaint summaryTextPaint;
    private int titleMarginBottomToInnerView;

    @VisibleForTesting
    int touchViewHeight;
    private int touchViewBackground;

    @VisibleForTesting
    int continueButtonStyle;
    private ContextThemeWrapper continueButtonContextWrapper;

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

        initPropertiesFromAttrs(attrs, defStyleAttr, defStyleRes);
        initPadding();
        initIconProperties();
        initTitleProperties();
        initSummaryProperties();
        initTouchViewProperties();
        initConnectorProperties();

        stepViews = new ArrayList<>();
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
        iconActiveColor = a.getColor(R.styleable.VerticalStepper_iconColorActive,
                ResourcesCompat.getColor(resources, defaultActiveColor, context.getTheme()));
        iconInactiveColor = a.getColor(R.styleable.VerticalStepper_iconColorInactive,
                ResourcesCompat.getColor(resources, R.color.bg_inactive_icon, context.getTheme()));
    }

    @SuppressLint("PrivateResource") // https://code.google.com/p/android/issues/detail?id=230985
    private void initNavButtonPropertiesFromAttrs(TypedArray a) {
        continueButtonStyle = a.getResourceId(
                R.styleable.VerticalStepper_continueButtonStyle, R.style.Widget_AppCompat_Button_Colored);
        continueButtonContextWrapper = new ContextThemeWrapper(context, continueButtonStyle);
    }

    private void initPadding() {
        outerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_horizontal);
        outerVerticalPadding = resources.getDimensionPixelSize(R.dimen.outer_padding_vertical);
        inactiveBottomMarginToNextStep = resources.getDimensionPixelSize(R.dimen.inactive_bottom_margin_to_next_step);
        activeBottomMarginToNextStep = resources.getDimensionPixelSize(R.dimen.active_bottom_margin_to_next_step);
    }

    private void initIconProperties() {
        initIconDimension();
        initIconMargins();
        initIconBackground();
        initIconTextPaint();
        initIconTmpObjects();
    }

    private void initIconDimension() {
        iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
    }

    private void initIconMargins() {
        iconMarginRight = resources.getDimensionPixelSize(R.dimen.icon_margin_right);
        iconMarginVertical = resources.getDimensionPixelSize(R.dimen.icon_margin_vertical);
    }

    private void initIconBackground() {
        iconActiveBackgroundPaint = createIconBackground(iconActiveColor);
        iconInactiveBackgroundPaint = createIconBackground(iconInactiveColor);
    }

    private Paint createIconBackground(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }

    private void initIconTextPaint() {
        iconTextPaint = createTextPaint(R.color.white, R.dimen.icon_font_size);
    }

    private void initIconTmpObjects() {
        tmpRectIconBackground = new RectF(0, 0, iconDimension, iconDimension);
        tmpRectIconTextBounds = new Rect();
    }

    private void initTitleProperties() {
        initTitleDimensions();
        initTitleTextPaint();
    }

    private void initTitleDimensions() {
        titleMarginBottomToInnerView = resources.getDimensionPixelSize(R.dimen.title_margin_bottom_to_inner_view);
    }

    private void initTitleTextPaint() {
        titleActiveTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
        titleActiveTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        titleInactiveTextPaint = createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size);
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
        touchViewBackground = getResolvedAttributeData(R.attr.selectableItemBackground, 0);
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
        initStepViews();
    }

    @VisibleForTesting
    void initStepViews() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            initStepView(new StepView(getChildAt(i), new InternalTouchView(context),
                    new AppCompatButton(continueButtonContextWrapper, null, 0)));
        }

        for (StepView v : stepViews) {
            initTouchView(v);
            initNavButtons(v);
        }
    }

    @VisibleForTesting
    void initStepView(StepView stepView) {
        stepViews.add(stepView);
        stepView.getInnerView().setVisibility(View.GONE);
    }

    @VisibleForTesting
    void initTouchView(final StepView stepView) {
        InternalTouchView touchView = stepView.getTouchView();
        touchView.setBackgroundResource(touchViewBackground);
        touchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStepExpandedState(stepView);
            }
        });
        addView(touchView);
    }

    @VisibleForTesting
    void initNavButtons(StepView stepView) {
        AppCompatButton continueButton = stepView.getContinueButton();
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
    void toggleStepExpandedState(StepView stepView) {
        toggleActiveState(stepView);
        toggleViewVisibility(stepView.getInnerView());
        toggleViewVisibility(stepView.getContinueButton());
    }

    private void toggleActiveState(StepView stepView) {
        stepView.setActive(!stepView.isActive());
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
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            StepView stepView = stepViews.get(i);
            LayoutParams lp = getInternalLayoutParams(stepView.getInnerView());
            stepView.setDecoratorHeight(calculateStepDecoratorHeight(lp, stepView));
        }
    }

    @VisibleForTesting
    void measureStepBottomMarginHeights() {
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            StepView stepView = stepViews.get(i);
            stepView.setBottomMarginHeight(getBottomMarginToNextStep(stepView, i == innerViewsSize - 1));
        }
    }

    @VisibleForTesting
    void measureChildViews(int widthMeasureSpec, int heightMeasureSpec) {
        int stepperHorizontalPadding = calculateHorizontalPadding();
        int currentHeight = calculateVerticalPadding();
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            StepView stepView = stepViews.get(i);

            currentHeight += stepView.getDecoratorHeight();

            View innerView = stepView.getInnerView();
            LayoutParams lp = getInternalLayoutParams(innerView);

            int usedWidthFromPadding = stepperHorizontalPadding + calculateInnerViewHorizontalUsedSpace(lp);
            int innerViewVerticalPadding = calculateInnerViewVerticalUsedSpace(lp);
            int usedHeight = innerViewVerticalPadding + currentHeight;
            measureInnerView(widthMeasureSpec, heightMeasureSpec, innerView, usedWidthFromPadding, usedHeight);

            int childrenHeight = 0;
            if (stepView.isActive()) {
                childrenHeight += innerView.getMeasuredHeight() + innerViewVerticalPadding;
            }
            currentHeight += childrenHeight;

            AppCompatButton continueButton = stepView.getContinueButton();
            measureNavButton(widthMeasureSpec, heightMeasureSpec, continueButton, usedWidthFromPadding, currentHeight);

            if (stepView.isActive()) {
                childrenHeight += continueButton.getMeasuredHeight();
            }
            stepView.setChildrenVisibleHeight(childrenHeight);

            currentHeight += stepView.getBottomMarginHeight();
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
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            StepView stepView = stepViews.get(i);
            View innerView = stepView.getInnerView();

            LayoutParams lp = getInternalLayoutParams(innerView);
            int innerViewHorizontalPadding = calculateInnerViewHorizontalUsedSpace(lp);

            width = Math.max(width, calculateStepDecoratorWidth(lp, stepView));

            width = Math.max(width, innerView.getMeasuredWidth() + innerViewHorizontalPadding);

            AppCompatButton continueButton = stepView.getContinueButton();
            width = Math.max(width, continueButton.getMeasuredWidth() + innerViewHorizontalPadding);
        }
        return width;
    }

    @VisibleForTesting
    int calculateHeight() {
        int height = calculateVerticalPadding();
        for (StepView stepView : stepViews) {
            height += stepView.getDecoratorHeight();
            height += stepView.getChildrenVisibleHeight();
            height += stepView.getBottomMarginHeight();
        }
        return height;
    }

    private void measureTouchViews(int width) {
        for (StepView v : stepViews) {
            measureTouchView(width, v.getTouchView());
        }
    }

    @VisibleForTesting
    void measureTouchView(int width, InternalTouchView view) {
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(touchViewHeight, MeasureSpec.EXACTLY);
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

    @VisibleForTesting
    int calculateInnerViewHorizontalUsedSpace(LayoutParams lp) {
        return iconDimension + iconMarginRight + lp.leftMargin + lp.rightMargin;
    }

    @VisibleForTesting
    int calculateInnerViewVerticalUsedSpace(LayoutParams lp) {
        return lp.topMargin + lp.bottomMargin;
    }

    @VisibleForTesting
    int calculateStepDecoratorWidth(LayoutParams lp, StepView stepView) {
        return calculateStepDecoratorIconWidth() + (int) calculateStepDecoratorTextWidth(lp, stepView);
    }

    @VisibleForTesting
    int calculateStepDecoratorIconWidth() {
        return iconDimension + iconMarginRight;
    }

    @VisibleForTesting
    float calculateStepDecoratorTextWidth(LayoutParams lp, StepView stepView) {
        lp.measureTitleHorizontalDimensions(getTitleTextPaint(stepView));
        lp.measureSummaryHorizontalDimensions(summaryTextPaint);
        return Math.max(lp.getTitleWidth(), lp.getSummaryWidth());
    }

    @VisibleForTesting
    int calculateStepDecoratorHeight(LayoutParams lp, StepView stepView) {
        lp.measureTitleVerticalDimensions(getTitleTextPaint(stepView), iconDimension);
        lp.measureSummaryVerticalDimensions(summaryTextPaint);
        int textTotalHeight = (int) (lp.getTitleBottomRelativeToStepTop() + lp.getSummaryBottomRelativeToTitleBottom());
        return Math.max(iconDimension, textTotalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int currentTop = top;
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            StepView stepView = stepViews.get(i);
            View innerView = stepView.getInnerView();
            boolean isFirstStep = i == 0;
            boolean isLastStep = i == innerViewsSize - 1;

            if (isFirstStep) {
                currentTop += getPaddingTop() + outerVerticalPadding;
            }

            layoutTouchView(left, currentTop, right, bottom, stepView.getTouchView(), isLastStep);

            LayoutParams lp = getInternalLayoutParams(innerView);
            if (stepView.isActive()) {
                layoutInnerView(left, currentTop, right, bottom, innerView, isLastStep);

                int buttonsTop = currentTop + calculateYDistanceToButtons(stepView, lp);

                layoutNavButtons(left, buttonsTop, right, bottom, stepView, isLastStep);
            }
            currentTop += calculateYDistanceToNextStep(stepView, lp, isLastStep);
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
                                 View innerView, boolean isLastStep) {
        LayoutParams lp = getInternalLayoutParams(innerView);
        int innerLeft = left + outerHorizontalPadding + getPaddingLeft() + lp.leftMargin
                + iconDimension + iconMarginRight;

        int innerTop = (int) (topAdjustedForPadding + lp.topMargin + lp.getTitleBottomRelativeToStepTop()
                + titleMarginBottomToInnerView);

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
                                  StepView stepView, boolean isLastStep) {
        // TODO There's quite a bit of common code between this and layoutInnerView. See if it can be consolidated.
        LayoutParams innerViewLp = getInternalLayoutParams(stepView.getInnerView());
        AppCompatButton button = stepView.getContinueButton();

        int buttonLeft = left + outerHorizontalPadding + getPaddingLeft() + innerViewLp.leftMargin
                + iconDimension + iconMarginRight;

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
        for (int i = 0, innerViewsSize = stepViews.size(); i < innerViewsSize; i++) {
            canvas.save();

            int stepNumber = i + 1;
            StepView stepView = stepViews.get(i);
            View innerView = stepView.getInnerView();
            LayoutParams lp = getInternalLayoutParams(innerView);

            canvas.save();
            drawIcon(canvas, stepView, stepNumber);
            canvas.restore();

            canvas.save();
            drawText(canvas, lp, stepView);
            canvas.restore();

            int dyToNextStep = calculateYDistanceToNextStep(stepView, lp, i == innerViewsSize - 1);
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

    private int calculateYDistanceToNextStep(StepView stepView, LayoutParams lp, boolean isLastStep) {
        int dyToNextStep = calculateYDistanceToButtons(stepView, lp);
        if (stepView.isActive()) {
            dyToNextStep += stepView.getContinueButton().getHeight();
        }
        dyToNextStep += getBottomMarginToNextStep(stepView, isLastStep);
        return dyToNextStep;
    }

    private int calculateYDistanceToButtons(StepView stepView, LayoutParams lp) {
        int dyToButtons = calculateYDistanceToTextBottom(stepView, lp);
        if (stepView.isActive()) {
            dyToButtons += stepView.getInnerView().getHeight() + titleMarginBottomToInnerView;
        }
        return dyToButtons;
    }

    private int calculateYDistanceToTextBottom(StepView stepView, LayoutParams lp) {
        int dyToTextBottom = (int) lp.getTitleBaselineRelativeToStepTop();
        if (!stepView.isActive()) {
            dyToTextBottom += lp.getSummaryBottomRelativeToTitleBottom();
        }
        return dyToTextBottom;
    }

    private void drawIcon(Canvas canvas, StepView stepView, int stepNumber) {
        drawIconBackground(canvas, stepView);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas, StepView stepView) {
        canvas.drawArc(tmpRectIconBackground, 0f, 360f, true, getIconColor(stepView));
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

        iconTextPaint.getTextBounds(stepNumberString, 0, 1, tmpRectIconTextBounds);
        float centeredTextY = (iconDimension / 2) + (tmpRectIconTextBounds.height() / 2);

        canvas.drawText(stepNumberString, centeredTextX, centeredTextY, iconTextPaint);
    }

    private void drawText(Canvas canvas, LayoutParams lp, StepView stepView) {
        canvas.translate(calculateStepDecoratorIconWidth(), 0);

        TextPaint paint = getTitleTextPaint(stepView);
        canvas.drawText(lp.getTitle(), 0, lp.getTitleBaselineRelativeToStepTop(), paint);

        if (!TextUtils.isEmpty(lp.getSummary()) && !stepView.isActive()) {
            canvas.translate(0, lp.getTitleBottomRelativeToStepTop());
            canvas.drawText(lp.getSummary(), 0, lp.getSummaryBaselineRelativeToTitleBottom(), summaryTextPaint);
        }
        // TODO Handle optional case
    }

    private void drawConnector(Canvas canvas, int yDistanceToNextStep) {
        canvas.translate((iconDimension - connectorWidth) / 2, 0);
        float startY = iconDimension + iconMarginVertical;
        float stopY = yDistanceToNextStep - iconMarginVertical;
        canvas.drawLine(0, startY, 0, stopY, connectorPaint);
    }

    @VisibleForTesting
    TextPaint getTitleTextPaint(StepView stepView) {
        return stepView.isActive() ? titleActiveTextPaint : titleInactiveTextPaint;
    }

    @VisibleForTesting
    int getBottomMarginToNextStep(StepView stepView, boolean isLastStep) {
        if (isLastStep) {
            return ZERO_SIZE_MARGIN;
        } else {
            return stepView.isActive() ? activeBottomMarginToNextStep : inactiveBottomMarginToNextStep;
        }
    }

    private Paint getIconColor(StepView stepView) {
        return stepView.isActive() ? iconActiveBackgroundPaint : iconInactiveBackgroundPaint;
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
    static class StepView {
        private InternalTouchView touchView;
        private AppCompatButton continueButton;
        private View innerView;
        private boolean active;

        private int decoratorHeight;
        private int bottomMarginHeight;
        private int childrenVisibleHeight;

        StepView(View innerView, InternalTouchView touchView, AppCompatButton continueButton) {
            this.innerView = innerView;
            this.touchView = touchView;
            this.continueButton = continueButton;
            this.active = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StepView stepView = (StepView) o;

            if (decoratorHeight != stepView.decoratorHeight) return false;
            if (bottomMarginHeight != stepView.bottomMarginHeight) return false;
            if (childrenVisibleHeight != stepView.childrenVisibleHeight) return false;
            if (touchView != null ? !touchView.equals(stepView.touchView) : stepView.touchView != null) return false;
            if (continueButton != null ? !continueButton.equals(stepView.continueButton)
                    : stepView.continueButton != null)
                return false;
            return innerView != null ? innerView.equals(stepView.innerView) : stepView.innerView == null;
        }

        @Override
        public int hashCode() {
            int result = touchView != null ? touchView.hashCode() : 0;
            result = 31 * result + (continueButton != null ? continueButton.hashCode() : 0);
            result = 31 * result + (innerView != null ? innerView.hashCode() : 0);
            result = 31 * result + decoratorHeight;
            result = 31 * result + bottomMarginHeight;
            result = 31 * result + childrenVisibleHeight;
            return result;
        }

        View getInnerView() {
            return innerView;
        }

        InternalTouchView getTouchView() {
            return touchView;
        }

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
    }

    public static class LayoutParams extends MarginLayoutParams {

        private static Rect tmpRectTitleTextBounds = new Rect();

        @SuppressWarnings("NullableProblems")
        @NonNull
        private String title;
        private float titleWidth;
        private float titleBaselineRelativeToStepTop;
        private float titleBottomRelativeToStepTop;

        @Nullable
        private String summary;
        private float summaryWidth;
        private float summaryBaselineRelativeToTitleBottom;
        private float summaryBottomRelativeToTitleBottom;

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
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
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

        float getTitleBaselineRelativeToStepTop() {
            return titleBaselineRelativeToStepTop;
        }

        float getTitleBottomRelativeToStepTop() {
            return titleBottomRelativeToStepTop;
        }

        @Nullable
        String getSummary() {
            return summary;
        }

        float getSummaryWidth() {
            return summaryWidth;
        }

        float getSummaryBaselineRelativeToTitleBottom() {
            return summaryBaselineRelativeToTitleBottom;
        }

        float getSummaryBottomRelativeToTitleBottom() {
            return summaryBottomRelativeToTitleBottom;
        }

        void measureTitleHorizontalDimensions(TextPaint titlePaint) {
            float width = 0f;
            if (!TextUtils.isEmpty(title)) {
                width = titlePaint.measureText(title);
            }
            titleWidth = width;
        }

        void measureSummaryHorizontalDimensions(TextPaint summaryPaint) {
            float width = 0f;
            if (!TextUtils.isEmpty(summary)) {
                width = summaryPaint.measureText(summary);
            }
            summaryWidth = width;
        }

        void measureTitleVerticalDimensions(TextPaint titlePaint, int heightToCenterIn) {
            measureTitleBaseline(titlePaint, heightToCenterIn);
            titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + titlePaint.getFontMetrics().bottom;
        }

        private void measureTitleBaseline(TextPaint titlePaint, int heightToCenterIn) {
            titlePaint.getTextBounds(title, 0, 1, tmpRectTitleTextBounds);
            titleBaselineRelativeToStepTop = (heightToCenterIn / 2) + (tmpRectTitleTextBounds.height() / 2);
        }

        void measureSummaryVerticalDimensions(TextPaint summaryPaint) {
            measureSummaryBaseline(summaryPaint);
            summaryBottomRelativeToTitleBottom =
                    summaryBaselineRelativeToTitleBottom + summaryPaint.getFontMetrics().bottom;
        }

        private void measureSummaryBaseline(TextPaint summaryPaint) {
            summaryBaselineRelativeToTitleBottom = -summaryPaint.getFontMetrics().ascent;
        }
    }

    @VisibleForTesting
    static class InternalTouchView extends View {
        public InternalTouchView(Context context) {
            super(context);
        }
    }
}
