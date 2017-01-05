package com.snowble.android.widget.verticalstepper;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;

class Step {

    @VisibleForTesting
    static final int ZERO_SIZE_MARGIN = 0;

    private static final Rect TMP_RECT_TITLE_TEXT_BOUNDS = new Rect();

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
        initTextValues((VerticalStepper.LayoutParams) innerView.getLayoutParams());
        this.active = false;
        this.common = common;
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
        dyToNextStep += getBottomMarginToNextStep(false);
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
