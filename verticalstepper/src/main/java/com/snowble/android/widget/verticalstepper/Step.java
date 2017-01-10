package com.snowble.android.widget.verticalstepper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

class Step {
    @NonNull
    private final VerticalStepper.InternalTouchView touchView;
    @NonNull
    private final AppCompatButton continueButton;
    @NonNull
    private final View innerView;
    @SuppressWarnings("NullableProblems") // validateTitle() will ensure it's non-null
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

    Step(@NonNull View innerView, @NonNull VerticalStepper.InternalTouchView touchView,
         @NonNull AppCompatButton continueButton, @NonNull Common common) {
        this.innerView = innerView;
        this.touchView = touchView;
        this.continueButton = continueButton;
        this.active = false;
        this.common = common;
        initTextValues((VerticalStepper.LayoutParams) innerView.getLayoutParams());
    }

    @VisibleForTesting
    void initTextValues(@NonNull VerticalStepper.LayoutParams lp) {
        this.title = lp.getTitle();
        validateTitle();
        this.summary = lp.getSummary();
    }

    @VisibleForTesting
    void validateTitle() {
        if (TextUtils.isEmpty(title)) {
            throw new IllegalArgumentException("step_title cannot be empty.");
        }
    }

    @NonNull
    View getInnerView() {
        return innerView;
    }

    @NonNull
    VerticalStepper.InternalTouchView getTouchView() {
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

    int getBottomMarginHeight() {
        return bottomMarginHeight;
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
        measureTitleBaseline(heightToCenterIn);
        titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + getTitleTextPaint().getFontMetrics().bottom;
    }

    private void measureTitleBaseline(int heightToCenterIn) {
        Rect tempRect = common.getTempRectForTitleTextBounds();
        getTitleTextPaint().getTextBounds(title, 0, 1, tempRect);
        titleBaselineRelativeToStepTop = (heightToCenterIn / 2) + (tempRect.height() / 2);
    }

    void measureSummaryVerticalDimensions() {
        measureSummaryBaseline();
        summaryBottomRelativeToTitleBottom =
                summaryBaselineRelativeToTitleBottom + common.getSummaryTextPaint().getFontMetrics().bottom;
    }

    private void measureSummaryBaseline() {
        summaryBaselineRelativeToTitleBottom = -common.getSummaryTextPaint().getFontMetrics().ascent;
    }

    TextPaint getTitleTextPaint() {
        return active ? common.getTitleActiveTextPaint() : common.getTitleInactiveTextPaint();
    }

    void measureBottomMarginToNextStep() {
        bottomMarginHeight = getBottomMarginToNextStep();
    }

    int getBottomMarginToNextStep() {
        return active ? common.getActiveBottomMarginToNextStep() : common.getInactiveBottomMarginToNextStep();
    }

    Paint getIconColor() {
        return active ? common.getIconActiveBackgroundPaint() : common.getIconInactiveBackgroundPaint();
    }

    int calculateHorizontalUsedSpace(View view) {
        VerticalStepper.LayoutParams lp = (VerticalStepper.LayoutParams) view.getLayoutParams();
        return calculateStepDecoratorIconWidth() + lp.leftMargin + lp.rightMargin;
    }

    int calculateVerticalUsedSpace(View view) {
        VerticalStepper.LayoutParams lp = (VerticalStepper.LayoutParams) view.getLayoutParams();
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
        decoratorHeight = Math.max(iconDimension, textTotalHeight);
    }

    int calculateYDistanceToNextStep() {
        int dyToNextStep = calculateYDistanceToTextBottom();
        if (active) {
            dyToNextStep += innerView.getHeight() + continueButton.getHeight();
        }
        dyToNextStep += getBottomMarginToNextStep();
        return dyToNextStep;
    }

    int calculateYDistanceToTextBottom() {
        int dyToTextBottom = (int) getTitleBottomRelativeToStepTop();
        if (!active) {
            dyToTextBottom += getSummaryBottomRelativeToTitleBottom();
        } else {
            dyToTextBottom += common.getTitleMarginBottomToInnerView();
        }
        return dyToTextBottom;
    }

    static class Common {
        private final Resources resources;
        private final Resources.Theme theme;

        private final int iconDimension;
        private final int iconMarginRight;
        private final int iconMarginVertical;
        private final Paint iconActiveBackgroundPaint;
        private final Paint iconInactiveBackgroundPaint;
        private final TextPaint iconTextPaint;

        private final TextPaint titleActiveTextPaint;
        private final TextPaint titleInactiveTextPaint;
        private final int titleMarginBottomToInnerView;

        private final TextPaint summaryTextPaint;

        private final int touchViewHeight;
        private final int touchViewBackground;

        private final int activeBottomMargin;
        private final int inactiveBottomMargin;

        private final int connectorWidth;
        private final Paint connectorPaint;

        private final RectF tempRectForIconBackground;
        private final Rect tempRectForIconTextBounds;
        private final Rect tempRectForTitleTextBounds;
        private final Rect tempRectForLayout;

        Common(Context context, int iconActiveColor, int iconInactiveColor) {
            resources = context.getResources();
            theme = context.getTheme();

            iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
            iconMarginRight = resources.getDimensionPixelSize(R.dimen.icon_margin_right);
            iconMarginVertical = resources.getDimensionPixelSize(R.dimen.icon_margin_vertical);
            iconActiveBackgroundPaint = createPaint(iconActiveColor);
            iconInactiveBackgroundPaint = createPaint(iconInactiveColor);
            iconTextPaint = createTextPaint(R.color.white, R.dimen.icon_font_size);

            titleMarginBottomToInnerView = resources.getDimensionPixelSize(R.dimen.title_margin_bottom_to_inner_view);
            titleActiveTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
            titleActiveTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titleInactiveTextPaint = createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size);

            summaryTextPaint = createTextPaint(R.color.summary_color, R.dimen.summary_font_size);

            touchViewHeight = resources.getDimensionPixelSize(R.dimen.touch_height);
            touchViewBackground =
                    ThemeUtils.getResolvedAttributeData(context.getTheme(), R.attr.selectableItemBackground, 0);

            activeBottomMargin = resources.getDimensionPixelSize(R.dimen.inactive_bottom_margin_to_next_step);
            inactiveBottomMargin = resources.getDimensionPixelSize(R.dimen.active_bottom_margin_to_next_step);

            connectorWidth = resources.getDimensionPixelSize(R.dimen.connector_width);
            connectorPaint = createPaint(getColor(R.color.connector_color));
            connectorPaint.setStrokeWidth(getConnectorWidth());

            tempRectForIconBackground = new RectF(0, 0, getIconDimension(), getIconDimension());
            tempRectForIconTextBounds = new Rect();
            tempRectForTitleTextBounds = new Rect();
            tempRectForLayout = new Rect();
        }

        private Paint createPaint(int color) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            return paint;
        }

        private TextPaint createTextPaint(int colorRes, int fontDimenRes) {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(getColor(colorRes));
            textPaint.setAntiAlias(true);
            int titleTextSize = resources.getDimensionPixelSize(fontDimenRes);
            textPaint.setTextSize(titleTextSize);
            return textPaint;
        }

        private int getColor(int colorRes) {
            return ResourcesCompat.getColor(resources, colorRes, theme);
        }

        int getIconDimension() {
            return iconDimension;
        }

        int getIconMarginRight() {
            return iconMarginRight;
        }

        int getIconMarginVertical() {
            return iconMarginVertical;
        }

        Paint getIconActiveBackgroundPaint() {
            return iconActiveBackgroundPaint;
        }

        Paint getIconInactiveBackgroundPaint() {
            return iconInactiveBackgroundPaint;
        }

        TextPaint getIconTextPaint() {
            return iconTextPaint;
        }

        TextPaint getTitleActiveTextPaint() {
            return titleActiveTextPaint;
        }

        TextPaint getTitleInactiveTextPaint() {
            return titleInactiveTextPaint;
        }

        int getTitleMarginBottomToInnerView() {
            return titleMarginBottomToInnerView;
        }

        TextPaint getSummaryTextPaint() {
            return summaryTextPaint;
        }

        int getTouchViewHeight() {
            return touchViewHeight;
        }

        int getTouchViewBackground() {
            return touchViewBackground;
        }

        int getActiveBottomMarginToNextStep() {
            return activeBottomMargin;
        }

        int getInactiveBottomMarginToNextStep() {
            return inactiveBottomMargin;
        }

        int getConnectorWidth() {
            return connectorWidth;
        }

        Paint getConnectorPaint() {
            return connectorPaint;
        }

        RectF getTempRectForIconBackground() {
            return tempRectForIconBackground;
        }

        Rect getTempRectForIconTextBounds() {
            return tempRectForIconTextBounds;
        }

        Rect getTempRectForTitleTextBounds() {
            return tempRectForTitleTextBounds;
        }

        Rect getTempRectForLayout() {
            return tempRectForLayout;
        }
    }
}
