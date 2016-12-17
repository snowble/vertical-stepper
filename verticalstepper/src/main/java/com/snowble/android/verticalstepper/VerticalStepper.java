package com.snowble.android.verticalstepper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

public class VerticalStepper extends ViewGroup {

    private Context context;
    private Resources resources;

    private int outerHorizontalMargin;
    private int outerVerticalMargin;

    private int stepIconDimension;
    private Paint stepIconBackgroundPaint;
    private RectF stepIconRect;
    private TextPaint stepIconTextPaint;
    private int stepIconTextHeight;


    public VerticalStepper(Context context) {
        super(context);
        init();
    }

    public VerticalStepper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalStepper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        context = getContext();
        resources = getResources();

        initMargins();
        initStepIconProperties();
    }

    private void initMargins() {
        outerHorizontalMargin = resources.getDimensionPixelSize(R.dimen.stepper_margin_horizontal);
        outerVerticalMargin = resources.getDimensionPixelSize(R.dimen.stepper_margin_vertical);
    }

    private void initStepIconProperties() {
        initStepIconDimension();
        initStepIconBackground();
        initStepIconTextPaint();
    }

    private void initStepIconDimension() {
        stepIconDimension = resources.getDimensionPixelSize(R.dimen.step_icon_diameter);
        stepIconRect = new RectF(0, 0, stepIconDimension, stepIconDimension);
    }

    private void initStepIconBackground() {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        int stepIconBackground;
        if (value.type != TypedValue.TYPE_NULL) {
            stepIconBackground = value.data;
        } else {
            //noinspection deprecation
            stepIconBackground = resources.getColor(R.color.bg_step_icon);
        }
        stepIconBackgroundPaint = new Paint();
        stepIconBackgroundPaint.setColor(stepIconBackground);
        stepIconBackgroundPaint.setAntiAlias(true);
    }

    private void initStepIconTextPaint() {
        stepIconTextPaint = new TextPaint();
        stepIconTextPaint.setColor(Color.WHITE);
        stepIconTextPaint.setAntiAlias(true);
        int stepIconTextSize = resources.getDimensionPixelSize(R.dimen.step_icon_font_size);
        stepIconTextPaint.setTextSize(stepIconTextSize);

        final Rect bounds = new Rect();
        stepIconTextPaint.getTextBounds("1", 0, 1, bounds);
        // TODO This height needs to be updated for each child
        stepIconTextHeight = bounds.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TODO respect measure specs
        int width = outerHorizontalMargin;
        int height = outerVerticalMargin;

        width += stepIconDimension;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            height += stepIconDimension;
            // TODO Measure child and add that to our height
        }

        int xPadding = getPaddingLeft() + getPaddingRight();
        int yPadding = getPaddingTop() + getPaddingBottom();
        width += xPadding;
        height += yPadding;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            // TODO Update l,t,r,b based on translations
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            boolean isFirstChild = i == 0;
            if (isFirstChild) {
                canvas.translate(outerHorizontalMargin, outerVerticalMargin);
            }
            String iconNumber = "1";
            drawStepIcon(canvas, iconNumber);
        }
    }

    private void drawStepIcon(Canvas canvas, String iconNumber) {
        canvas.drawArc(stepIconRect, 0f, 360f, true, stepIconBackgroundPaint);
        float width = stepIconTextPaint.measureText(iconNumber);
        float centeredTextX = stepIconDimension / 2 - (width / 2);
        int centeredTextY = stepIconDimension / 2 + stepIconTextHeight / 2;
        canvas.drawText(iconNumber, centeredTextX, centeredTextY, stepIconTextPaint);
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
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
