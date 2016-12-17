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

import java.util.Locale;

public class VerticalStepper extends ViewGroup {

    private Context context;
    private Resources resources;

    private int outerHorizontalMargin;
    private int outerVerticalMargin;

    private int iconDimension;
    private Paint iconBackgroundPaint;
    private RectF iconBackgroundRect;
    private TextPaint iconTextPaint;
    private Rect iconTextRect;


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
        initIconProperties();
    }

    private void initMargins() {
        outerHorizontalMargin = resources.getDimensionPixelSize(R.dimen.outer_margin_horizontal);
        outerVerticalMargin = resources.getDimensionPixelSize(R.dimen.outer_margin_vertical);
    }

    private void initIconProperties() {
        initIconDimension();
        initIconBackground();
        initIconTextPaint();
        initIconRectsForReuse();
    }

    private void initIconDimension() {
        iconDimension = resources.getDimensionPixelSize(R.dimen.icon_diameter);
    }

    private void initIconBackground() {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        int iconBackground;
        if (value.type != TypedValue.TYPE_NULL) {
            iconBackground = value.data;
        } else {
            //noinspection deprecation
            iconBackground = resources.getColor(R.color.bg_icon);
        }
        iconBackgroundPaint = new Paint();
        iconBackgroundPaint.setColor(iconBackground);
        iconBackgroundPaint.setAntiAlias(true);
    }

    private void initIconTextPaint() {
        iconTextPaint = new TextPaint();
        iconTextPaint.setColor(Color.WHITE);
        iconTextPaint.setAntiAlias(true);
        int iconTextSize = resources.getDimensionPixelSize(R.dimen.icon_font_size);
        iconTextPaint.setTextSize(iconTextSize);
    }

    private void initIconRectsForReuse() {
        iconBackgroundRect = new RectF(0, 0, iconDimension, iconDimension);
        iconTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TODO respect measure specs
        int width = outerHorizontalMargin;
        int height = outerVerticalMargin;

        width += iconDimension;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            height += iconDimension;
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
            int stepNumber = i + 1;
            drawIcon(canvas, stepNumber);
        }
    }

    private void drawIcon(Canvas canvas, int stepNumber) {
        drawIconBackground(canvas);
        drawIconText(canvas, stepNumber);
    }

    private void drawIconBackground(Canvas canvas) {
        canvas.drawArc(iconBackgroundRect, 0f, 360f, true, iconBackgroundPaint);
    }

    private void drawIconText(Canvas canvas, int stepNumber) {
        String stepNumberString = String.format(Locale.getDefault(), "%d", stepNumber);

        float width = iconTextPaint.measureText(stepNumberString);
        float centeredTextX = (iconDimension / 2) - (width / 2);

        iconTextPaint.getTextBounds(stepNumberString, 0, 1, iconTextRect);
        float centeredTextY = (iconDimension / 2) + (iconTextRect.height() / 2);

        canvas.drawText(stepNumberString, centeredTextX, centeredTextY, iconTextPaint);
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
