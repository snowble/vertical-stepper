package com.snowble.android.widget.verticalstepper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
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
    private boolean complete;

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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
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

    void setActiveViewsHeight(int childrenVisibleHeight) {
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
            width = getSummaryTextPaint().measureText(summary);
        }
        summaryWidth = width;
    }

    void measureTitleVerticalDimensions(int heightToCenterIn) {
        measureTitleBaseline(heightToCenterIn);
        titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + getTitleTextPaint().getFontMetrics().bottom;
    }

    private void measureTitleBaseline(int heightToCenterIn) {
        titleBaselineRelativeToStepTop = ViewUtils.findTextCenterStartY(
                title, heightToCenterIn, getTitleTextPaint(), getTempRectForTitleTextBounds());
    }

    void measureSummaryVerticalDimensions() {
        measureSummaryBaseline();
        summaryBottomRelativeToTitleBottom =
                summaryBaselineRelativeToTitleBottom + getSummaryTextPaint().getFontMetrics().bottom;
    }

    private void measureSummaryBaseline() {
        summaryBaselineRelativeToTitleBottom = -getSummaryTextPaint().getFontMetrics().ascent;
    }

    TextPaint getTitleTextPaint() {
        if (active) {
            return common.getTitleActiveTextPaint();
        } else  {
            return complete ? common.getTitleCompleteTextPaint() : common.getTitleInactiveTextPaint();
        }
    }

    TextPaint getSummaryTextPaint() {
        return common.getSummaryTextPaint();
    }

    void measureBottomMarginToNextStep() {
        bottomMarginHeight = getBottomMarginToNextStep();
    }

    int getBottomMarginToNextStep() {
        return active ? common.getActiveBottomMarginToNextStep() : common.getInactiveBottomMarginToNextStep();
    }

    Paint getIconBackground() {
        if (active) {
            return common.getIconActiveBackgroundPaint();
        } else  {
            return complete ? common.getIconCompleteBackgroundPaint() : common.getIconInactiveBackgroundPaint();
        }
    }

    TextPaint getIconTextPaint() {
        return common.getIconTextPaint();
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
        return getIconDimension() + getIconMarginRight();
    }

    int getIconDimension() {
        return common.getIconDimension();
    }

    int getIconMarginRight() {
        return common.getIconMarginRight();
    }

    int getIconMarginVertical() {
        return common.getIconMarginVertical();
    }

    int getTouchViewHeight() {
        return common.getTouchViewHeight();
    }

    int getTouchViewBackgroundResource() {
        return common.getTouchViewBackgroundResource();
    }

    int getNavButtonHeight() {
        return common.getNavButtonHeight();
    }

    int getNavButtonTopMargin() {
        return common.getNavButtonTopMargin();
    }

    Paint getConnectorPaint() {
        return common.getConnectorPaint();
    }

    int calculateConnectorStartY() {
        return getIconDimension() + getIconMarginVertical();
    }

    int calculateConnectorStopY(int yDistanceToNextStep) {
        return yDistanceToNextStep - getIconMarginVertical();
    }

    float calculateStepDecoratorTextWidth() {
        measureTitleHorizontalDimensions();
        measureSummaryHorizontalDimensions();
        return Math.max(getTitleWidth(), getSummaryWidth());
    }

    void measureStepDecoratorHeight() {
        int iconDimension = getIconDimension();
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

    RectF getTempRectForIconBackground() {
        return common.getTempRectForIconBackground();
    }

    Rect getTempRectForIconTextBounds() {
        return common.getTempRectForIconTextBounds();
    }

    PointF getTempPointForIconTextCenter() {
        return common.getTempPointForIconTextCenter();
    }

    Rect getTempRectForTitleTextBounds() {
        return common.getTempRectForTitleTextBounds();
    }

    Rect getTempRectForLayout() {
        return common.getTempRectForLayout();
    }

    static class Common {
        private final Resources resources;
        private final Resources.Theme theme;

        private final int iconDimension;
        private final int iconMarginRight;
        private final int iconMarginVertical;
        private final Paint iconActiveBackgroundPaint;
        private final Paint iconInactiveBackgroundPaint;
        private final Paint iconCompleteBackgroundPaint;
        private final TextPaint iconTextPaint;

        private final TextPaint titleActiveTextPaint;
        private final TextPaint titleInactiveTextPaint;
        private final TextPaint titleCompleteTextPaint;
        private final int titleMarginBottomToInnerView;

        private final TextPaint summaryTextPaint;

        private final int touchViewHeight;
        private final int touchViewBackground;

        private final int navButtonTopMargin;
        private final int navButtonHeight;

        private final int activeBottomMargin;
        private final int inactiveBottomMargin;

        private final int connectorWidth;
        private final Paint connectorPaint;

        private final RectF tempRectForIconBackground;
        private final Rect tempRectForIconTextBounds;
        private final PointF tempPointForIconTextCenter;
        private final Rect tempRectForTitleTextBounds;
        private final Rect tempRectForLayout;

        Common(Context context, int iconActiveColor, int iconInactiveColor, int iconCompleteColor) {
            resources = context.getResources();
            theme = context.getTheme();

            iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
            iconMarginRight = resources.getDimensionPixelSize(R.dimen.icon_margin_right);
            iconMarginVertical = resources.getDimensionPixelSize(R.dimen.icon_margin_vertical);
            iconActiveBackgroundPaint = createPaint(iconActiveColor);
            iconInactiveBackgroundPaint = createPaint(iconInactiveColor);
            iconCompleteBackgroundPaint = createPaint(iconCompleteColor);
            iconTextPaint = createTextPaint(R.color.white, R.dimen.icon_font_size);

            titleMarginBottomToInnerView = resources.getDimensionPixelSize(R.dimen.title_margin_bottom_to_inner_view);
            titleActiveTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
            titleActiveTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titleInactiveTextPaint = createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size);
            titleCompleteTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);

            summaryTextPaint = createTextPaint(R.color.summary_color, R.dimen.summary_font_size);

            touchViewHeight = resources.getDimensionPixelSize(R.dimen.touch_height);
            touchViewBackground = ThemeUtils.getResolvedAttributeData(theme, R.attr.selectableItemBackground, 0);

            navButtonHeight = resources.getDimensionPixelSize(R.dimen.nav_btn_height);
            navButtonTopMargin = resources.getDimensionPixelSize(R.dimen.nav_btn_margin_top);
            activeBottomMargin = resources.getDimensionPixelSize(R.dimen.inactive_bottom_margin_to_next_step);
            inactiveBottomMargin = resources.getDimensionPixelSize(R.dimen.active_bottom_margin_to_next_step);

            connectorWidth = resources.getDimensionPixelSize(R.dimen.connector_width);
            connectorPaint = createPaint(getColor(R.color.connector_color));
            connectorPaint.setStrokeWidth(connectorWidth);

            tempRectForIconBackground = new RectF(0, 0, iconDimension, iconDimension);
            tempRectForIconTextBounds = new Rect();
            tempPointForIconTextCenter = new PointF();
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

        @VisibleForTesting
        int getIconDimension() {
            return iconDimension;
        }

        @VisibleForTesting
        int getIconMarginRight() {
            return iconMarginRight;
        }

        @VisibleForTesting
        int getIconMarginVertical() {
            return iconMarginVertical;
        }

        @VisibleForTesting
        Paint getIconActiveBackgroundPaint() {
            return iconActiveBackgroundPaint;
        }

        @VisibleForTesting
        Paint getIconInactiveBackgroundPaint() {
            return iconInactiveBackgroundPaint;
        }

        @VisibleForTesting
        Paint getIconCompleteBackgroundPaint() {
            return iconCompleteBackgroundPaint;
        }

        private TextPaint getIconTextPaint() {
            return iconTextPaint;
        }

        @VisibleForTesting
        TextPaint getTitleActiveTextPaint() {
            return titleActiveTextPaint;
        }

        @VisibleForTesting
        TextPaint getTitleInactiveTextPaint() {
            return titleInactiveTextPaint;
        }

        @VisibleForTesting
        TextPaint getTitleCompleteTextPaint() {
            return titleCompleteTextPaint;
        }

        @VisibleForTesting
        int getTitleMarginBottomToInnerView() {
            return titleMarginBottomToInnerView;
        }

        @VisibleForTesting
        TextPaint getSummaryTextPaint() {
            return summaryTextPaint;
        }

        private int getTouchViewHeight() {
            return touchViewHeight;
        }

        private int getTouchViewBackgroundResource() {
            return touchViewBackground;
        }

        private int getNavButtonHeight() {
            return navButtonHeight;
        }

        private int getNavButtonTopMargin() {
            return navButtonTopMargin;
        }

        @VisibleForTesting
        int getActiveBottomMarginToNextStep() {
            return activeBottomMargin;
        }

        @VisibleForTesting
        int getInactiveBottomMarginToNextStep() {
            return inactiveBottomMargin;
        }

        private Paint getConnectorPaint() {
            return connectorPaint;
        }

        private RectF getTempRectForIconBackground() {
            return tempRectForIconBackground;
        }

        private Rect getTempRectForIconTextBounds() {
            return tempRectForIconTextBounds;
        }

        private PointF getTempPointForIconTextCenter() {
            return tempPointForIconTextCenter;
        }

        @VisibleForTesting
        Rect getTempRectForTitleTextBounds() {
            return tempRectForTitleTextBounds;
        }

        private Rect getTempRectForLayout() {
            return tempRectForLayout;
        }
    }
}
