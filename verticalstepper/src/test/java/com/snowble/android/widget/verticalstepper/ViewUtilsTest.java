package com.snowble.android.widget.verticalstepper;

import android.graphics.Rect;
import android.text.TextPaint;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ViewUtilsTest {
    private String text;
    private TextPaint paint;

    @Before
    public void setUp() {
        text = "vertical stepper";
        paint = mock(TextPaint.class);
    }

    @Test
    public void findTextCenterStartX() throws Exception {
        float textWidth = 30f;
        int totalWidth = 100;
        when(paint.measureText(text)).thenReturn(textWidth);

        float centerX = ViewUtils.findTextCenterStartX(text, totalWidth, paint);

        Rect outer = new Rect(0, 0, totalWidth, 0);
        Rect inner = new Rect(0, 0, (int) textWidth, 0);
        int expectedCenter = outer.centerX() - inner.centerX();
        assertThat(centerX).isEqualTo(expectedCenter);
    }

    @Test
    public void findCenterStartX() throws Exception {
        float innerWidth = 30f;
        int totalWidth = 100;

        float centerX = ViewUtils.findCenterStartX(innerWidth, totalWidth);

        Rect outer = new Rect(0, 0, totalWidth, 0);
        Rect inner = new Rect(0, 0, (int) innerWidth, 0);
        int expectedCenter = outer.centerX() - inner.centerX();
        assertThat(centerX).isEqualTo(expectedCenter);
    }

    @Test
    public void findTextCenterStartY() throws Exception {
        int textHeight = 30;
        int totalHeight = 100;
        Rect measureRect = new Rect(0, 0, 0, textHeight);

        float centerY = ViewUtils.findTextCenterStartY(text, totalHeight, paint, measureRect);

        verify(paint).getTextBounds(text, 0, 1, measureRect);

        Rect outer = new Rect(0, 0, 0, totalHeight);
        Rect inner = new Rect(0, 0, 0, textHeight);
        int expectedCenter = outer.centerY() + inner.centerY();
        assertThat(centerY).isEqualTo(expectedCenter);
    }
}