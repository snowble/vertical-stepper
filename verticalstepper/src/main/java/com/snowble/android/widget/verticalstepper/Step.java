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
        measureTitleBaseline(heightToCenterIn);
        titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + getTitleTextPaint().getFontMetrics().bottom;
    }

    private void measureTitleBaseline(int heightToCenterIn) {
        Rect tempRect = common.getTempRectTitleTextBounds();
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

    int calculateInnerViewHorizontalUsedSpace() {
        VerticalStepper.LayoutParams lp = (VerticalStepper.LayoutParams) innerView.getLayoutParams();
        return calculateStepDecoratorIconWidth() + lp.leftMargin + lp.rightMargin;
    }

    int calculateInnerViewVerticalUsedSpace() {
        VerticalStepper.LayoutParams lp = (VerticalStepper.LayoutParams) innerView.getLayoutParams();
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

    int calculateYDistanceToNextStep() {
        int dyToNextStep;
        if (!active) {
            dyToNextStep = calculateYDistanceToTextBottom();
        } else {
            dyToNextStep = calculateYDistanceToButtons();
            dyToNextStep += continueButton.getHeight();
        }
        dyToNextStep += getBottomMarginToNextStep();
        return dyToNextStep;
    }

    int calculateYDistanceToButtons() {
        int dyToButtons = calculateYDistanceToTextBottom();
        dyToButtons += innerView.getHeight() + common.getTitleMarginBottomToInnerView();
        return dyToButtons;
    }

    int calculateYDistanceToTextBottom() {
        int dyToTextBottom = (int) getTitleBottomRelativeToStepTop();
        if (!active) {
            dyToTextBottom += getSummaryBottomRelativeToTitleBottom();
        }
        return dyToTextBottom;
    }

    static class Common {
        private final Context context;

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

        private final RectF tempRectIconBackground;
        private final Rect tempRectIconTextBounds;
        private final Rect tempRectTitleTextBounds;

        public Common(Context context, int iconActiveColor, int iconInactiveColor) {
            this.context = context;
            Resources resources = context.getResources();

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

            tempRectIconBackground = new RectF(0, 0, getIconDimension(), getIconDimension());
            tempRectIconTextBounds = new Rect();
            tempRectTitleTextBounds = new Rect();
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
            int titleTextSize = context.getResources().getDimensionPixelSize(fontDimenRes);
            textPaint.setTextSize(titleTextSize);
            return textPaint;
        }

        private int getColor(int colorRes) {
            return ResourcesCompat.getColor(context.getResources(), colorRes, context.getTheme());
        }

        public int getIconDimension() {
            return iconDimension;
        }

        public int getIconMarginRight() {
            return iconMarginRight;
        }

        public int getIconMarginVertical() {
            return iconMarginVertical;
        }

        public Paint getIconActiveBackgroundPaint() {
            return iconActiveBackgroundPaint;
        }

        public Paint getIconInactiveBackgroundPaint() {
            return iconInactiveBackgroundPaint;
        }

        public TextPaint getIconTextPaint() {
            return iconTextPaint;
        }

        public TextPaint getTitleActiveTextPaint() {
            return titleActiveTextPaint;
        }

        public TextPaint getTitleInactiveTextPaint() {
            return titleInactiveTextPaint;
        }

        public int getTitleMarginBottomToInnerView() {
            return titleMarginBottomToInnerView;
        }

        public TextPaint getSummaryTextPaint() {
            return summaryTextPaint;
        }

        public int getTouchViewHeight() {
            return touchViewHeight;
        }

        public int getTouchViewBackground() {
            return touchViewBackground;
        }

        public int getActiveBottomMarginToNextStep() {
            return activeBottomMargin;
        }

        public int getInactiveBottomMarginToNextStep() {
            return inactiveBottomMargin;
        }

        public int getConnectorWidth() {
            return connectorWidth;
        }

        public Paint getConnectorPaint() {
            return connectorPaint;
        }

        public RectF getTempRectIconBackground() {
            return tempRectIconBackground;
        }

        public Rect getTempRectIconTextBounds() {
            return tempRectIconTextBounds;
        }

        public Rect getTempRectTitleTextBounds() {
            return tempRectTitleTextBounds;
        }
    }
}
