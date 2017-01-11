package com.snowble.android.widget.verticalstepper;

import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextPaint;

public class ViewUtils {
    public static void findTextCenter(String text, int totalWidth, int totalHeight,
                                      TextPaint textPaint, Rect rectForMeasuringBounds, PointF center) {
        center.x = findTextCenterX(text, totalWidth, textPaint);
        center.y = findTextCenterY(text, totalHeight, textPaint, rectForMeasuringBounds);
    }

    public static float findTextCenterX(String text, int totalWidth, TextPaint textPaint) {
        float textWidth = textPaint.measureText(text);
        return ((totalWidth / 2) - (textWidth / 2));
    }

    public static float findTextCenterY(String text, int totalHeight,
                                       TextPaint textPaint, Rect rectForMeasuringBounds) {
        textPaint.getTextBounds(text, 0, 1, rectForMeasuringBounds);
        return (totalHeight / 2) + (rectForMeasuringBounds.height() / 2);
    }

}
