package com.snowble.android.widget.verticalstepper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
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
    @Nullable
    private String error;
    private boolean active;
    private boolean complete;

    @NonNull
    private final Common common;

    private int decoratorHeight;
    private int bottomMarginHeight;
    private int childrenVisibleHeight;

    private float titleWidth;
    private float titleBaselineRelativeToStepTop;
    private float titleBottomRelativeToStepTop;

    private float subtitleWidth;
    private float subtitleBaselineRelativeToTitleBottom;
    private float subtitleBottomRelativeToTitleBottom;

    Step(@NonNull View innerView, @NonNull VerticalStepper.InternalTouchView touchView,
         @NonNull AppCompatButton continueButton, @NonNull Common common, @Nullable State initialState) {
        this.innerView = innerView;
        this.touchView = touchView;
        this.continueButton = continueButton;
        this.active = false;
        this.common = common;
        initTextValues((VerticalStepper.LayoutParams) innerView.getLayoutParams());
        setState(initialState);
    }

    private void initTextValues(@NonNull VerticalStepper.LayoutParams lp) {
        this.title = lp.getTitle();
        validateTitle();
        this.summary = lp.getSummary();
    }

    private void validateTitle() {
        if (TextUtils.isEmpty(title)) {
            throw new IllegalArgumentException("step_title cannot be empty.");
        }
    }

    private void setState(@Nullable State state) {
        if (state != null) {
            active = state.active;
            complete = state.complete;
            error = state.error;
            summary = state.summary;
        }
    }

    State generateState() {
        return new State(this);
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

    boolean isComplete() {
        return complete;
    }

    void markComplete() {
        error = "";
        complete = true;
    }

    boolean hasError() {
        return !TextUtils.isEmpty(error);
    }

    void setError(@Nullable String error) {
        complete = false;
        this.error = error;
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

    void setSummary(@NonNull String summary) {
        this.summary = summary;
    }

    @Nullable
    String getSubtitle() {
        if (hasError()) {
            return error;
        } else if (!active && complete) {
            return summary;
        } else {
            return "";
        }
    }

    float getSubtitleWidth() {
        return subtitleWidth;
    }

    float getTitleBaselineRelativeToStepTop() {
        return titleBaselineRelativeToStepTop;
    }

    float getTitleBottomRelativeToStepTop() {
        return titleBottomRelativeToStepTop;
    }

    float getSubtitleBaselineRelativeToTitleBottom() {
        return subtitleBaselineRelativeToTitleBottom;
    }

    float getSubtitleBottomRelativeToTitleBottom() {
        return subtitleBottomRelativeToTitleBottom;
    }

    void measureTitleHorizontalDimensions() {
        float width = 0f;
        if (!TextUtils.isEmpty(title)) {
            width = getTitleTextPaint().measureText(title);
        }
        titleWidth = width;
    }

    void measureSubtitleHorizontalDimensions() {
        float width = 0f;
        String subtitle = getSubtitle();
        if (!TextUtils.isEmpty(subtitle)) {
            width = getSubtitleTextPaint().measureText(subtitle);
        }
        subtitleWidth = width;
    }

    void measureTitleVerticalDimensions(int heightToCenterIn) {
        measureTitleBaseline(heightToCenterIn);
        titleBottomRelativeToStepTop = titleBaselineRelativeToStepTop + getTitleTextPaint().getFontMetrics().bottom;
    }

    private void measureTitleBaseline(int heightToCenterIn) {
        titleBaselineRelativeToStepTop = ViewUtils.findTextCenterStartY(
                title, heightToCenterIn, getTitleTextPaint(), getTempRectForTitleTextBounds());
    }

    void measureSubtitleVerticalDimensions() {
        measureSubtitleBaseline();
        subtitleBottomRelativeToTitleBottom =
                subtitleBaselineRelativeToTitleBottom + getSubtitleTextPaint().getFontMetrics().bottom;
    }

    private void measureSubtitleBaseline() {
        subtitleBaselineRelativeToTitleBottom = -getSubtitleTextPaint().getFontMetrics().ascent;
    }

    TextPaint getTitleTextPaint() {
        if (hasError()) {
            return common.getTitleErrorTextPaint();
        } else if (active) {
            return common.getTitleActiveTextPaint();
        } else  {
            return complete ? common.getTitleCompleteTextPaint() : common.getTitleInactiveTextPaint();
        }
    }

    TextPaint getSubtitleTextPaint() {
        if (hasError()) {
            return common.getSubtitleErrorTextPaint();
        } else {
            return common.getSummaryTextPaint();
        }
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

    @VisibleForTesting
    int getIconMarginRight() {
        return common.getIconMarginRight();
    }

    @VisibleForTesting
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
        measureSubtitleHorizontalDimensions();
        return Math.max(getTitleWidth(), getSubtitleWidth());
    }

    void measureStepDecoratorHeight() {
        int iconDimension = getIconDimension();
        measureTitleVerticalDimensions(iconDimension);
        measureSubtitleVerticalDimensions();
        int textTotalHeight = (int) (getTitleBottomRelativeToStepTop()
                + getSubtitleBottomRelativeToTitleBottom());
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
            dyToTextBottom += getSubtitleBottomRelativeToTitleBottom();
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

    private Rect getTempRectForTitleTextBounds() {
        return common.getTempRectForTitleTextBounds();
    }

    Rect getTempRectForLayout() {
        return common.getTempRectForLayout();
    }

    Bitmap getIconErrorBitmap() {
        return common.getIconErrorBitmap();
    }

    static class State implements Parcelable {
        private static final int FALSE = 0;
        private static final int TRUE = 1;

        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        @VisibleForTesting
        final boolean active;
        @VisibleForTesting
        final boolean complete;
        @VisibleForTesting
        final String error;
        @VisibleForTesting
        final String summary;

        @VisibleForTesting
        State(boolean active, boolean complete, String error, String summary) {
            this.active = active;
            this.complete = complete;
            this.error = error;
            this.summary = summary;
        }

        State(Step step) {
            active = step.active;
            complete = step.complete;
            error = step.error;
            summary = step.summary;
        }

        State(Parcel in) {
            active = in.readInt() == TRUE;
            complete = in.readInt() == TRUE;
            error = (String) in.readValue(String.class.getClassLoader());
            summary = (String) in.readValue(String.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(active ? TRUE : FALSE);
            dest.writeInt(complete ? TRUE : FALSE);
            dest.writeValue(error);
            dest.writeValue(summary);
        }

        @Override
        public int describeContents() {
            return 0;
        }

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
        private final Bitmap iconErrorBitmap;

        private final TextPaint titleActiveTextPaint;
        private final TextPaint titleInactiveTextPaint;
        private final TextPaint titleCompleteTextPaint;
        private final TextPaint titleErrorTextPaint;
        private final int titleMarginBottomToInnerView;

        private final TextPaint summaryTextPaint;
        private final TextPaint subtitleErrorTextPaint;

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
            iconErrorBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_warning_24dp);

            titleMarginBottomToInnerView = resources.getDimensionPixelSize(R.dimen.title_margin_bottom_to_inner_view);
            titleActiveTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
            titleActiveTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titleInactiveTextPaint = createTextPaint(R.color.title_inactive_color, R.dimen.title_font_size);
            titleCompleteTextPaint = createTextPaint(R.color.title_active_color, R.dimen.title_font_size);
            titleErrorTextPaint = createTextPaint(R.color.error_color, R.dimen.title_font_size);
            titleErrorTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            summaryTextPaint = createTextPaint(R.color.summary_color, R.dimen.summary_font_size);
            subtitleErrorTextPaint = createTextPaint(R.color.error_color, R.dimen.subtitle_font_size);

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
        TextPaint getTitleErrorTextPaint() {
            return titleErrorTextPaint;
        }

        @VisibleForTesting
        int getTitleMarginBottomToInnerView() {
            return titleMarginBottomToInnerView;
        }

        @VisibleForTesting
        TextPaint getSummaryTextPaint() {
            return summaryTextPaint;
        }

        @VisibleForTesting
        TextPaint getSubtitleErrorTextPaint() {
            return subtitleErrorTextPaint;
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

        Bitmap getIconErrorBitmap() {
            return iconErrorBitmap;
        }
    }
}
