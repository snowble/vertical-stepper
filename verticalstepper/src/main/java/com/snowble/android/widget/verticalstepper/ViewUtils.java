package com.snowble.android.widget.verticalstepper;

import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextPaint;

class ViewUtils {
    static void findTextCenterStartPoint(String text, int totalWidth, int totalHeight,
                                         TextPaint textPaint, Rect rectForMeasuringBounds, PointF center) {
        center.x = findTextCenterStartX(text, totalWidth, textPaint);
        center.y = findTextCenterStartY(text, totalHeight, textPaint, rectForMeasuringBounds);
    }

    static float findTextCenterStartX(String text, int totalWidth, TextPaint textPaint) {
        float textWidth = textPaint.measureText(text);
        return ((totalWidth / 2) - (textWidth / 2));
    }

    static float findTextCenterStartY(String text, int totalHeight,
                                      TextPaint textPaint, Rect rectForMeasuringBounds) {
        textPaint.getTextBounds(text, 0, 1, rectForMeasuringBounds);
        return (totalHeight / 2) + (rectForMeasuringBounds.height() / 2);
    }
}
