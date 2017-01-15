package com.snowble.android.widget.verticalstepper;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class StepTest {
    private static final Paint ICON_ACTIVE_PAINT = new Paint();
    private static final Paint ICON_INACTIVE_PAINT = new Paint();
    private static final Paint ICON_COMPLETE_PAINT = new Paint();

    public static abstract class GivenCommonValues extends GivenAnActivity {
        View innerView;
        VerticalStepper.LayoutParams innerLayoutParams;
        VerticalStepper.InternalTouchView touchView;
        AppCompatButton continueButton;
        VerticalStepper.LayoutParams continueLayoutParams;

        Step.Common common;

        String title;
        String summary;
        TextPaint titleInactivePaint;
        TextPaint summaryPaint;
        Rect titleRect;

        @Before
        public void givenCommonValues() {
            common = mock(Step.Common.class);

            when(common.getIconActiveBackgroundPaint()).thenReturn(ICON_ACTIVE_PAINT);
            when(common.getIconInactiveBackgroundPaint()).thenReturn(ICON_INACTIVE_PAINT);
            when(common.getIconCompleteBackgroundPaint()).thenReturn(ICON_COMPLETE_PAINT);

            titleInactivePaint = mock(TextPaint.class);
            titleRect = mock(Rect.class);
            when(titleInactivePaint.getFontMetrics()).thenReturn(mock(Paint.FontMetrics.class));
            when(common.getTitleInactiveTextPaint()).thenReturn(titleInactivePaint);
            when(common.getTempRectForTitleTextBounds()).thenReturn(titleRect);

            summaryPaint = mock(TextPaint.class);
            when(summaryPaint.getFontMetrics()).thenReturn(mock(Paint.FontMetrics.class));
            when(common.getSummaryTextPaint()).thenReturn(summaryPaint);
        }

        @Before
        public void givenChildViews() {
            innerView = mock(View.class);
            innerLayoutParams = createTestLayoutParams();
            title = innerLayoutParams.getTitle();
            summary = innerLayoutParams.getSummary();
            when(innerView.getLayoutParams()).thenReturn(innerLayoutParams);

            touchView = mock(VerticalStepper.InternalTouchView.class);

            continueButton = mock(AppCompatButton.class);
            continueLayoutParams = createTestLayoutParams();
            when(continueButton.getLayoutParams()).thenReturn(continueLayoutParams);
        }

        Step createStep(Step.Common common) {
            return new Step(innerView, touchView, continueButton, common);
        }
    }

    public static abstract class GivenAStep extends GivenCommonValues {
        Step step;

        @Before
        public void givenAStep() {
            step = createStep(common);
        }
    }

    public static abstract class GivenAStepSpy extends GivenAStep {
        Step stepSpy;

        @Before
        public void givenATestStep() {
            stepSpy = spy(step);
        }
    }

    public static class GivenEmptyStepSpy extends GivenAStepSpy {
        @Test
        public void measureTitleHorizontalDimensions_MeasuresUsingTitlePaint() {
            TextPaint paint = mock(TextPaint.class);
            doReturn(paint).when(stepSpy).getTitleTextPaint();

            stepSpy.measureTitleHorizontalDimensions();

            verify(paint).measureText(title);
        }

        @Test
        public void measureSubtitleHorizontalDimensions_MeasuresUsingSubtitlePaint() {
            stepSpy.markComplete();
            TextPaint paint = mock(TextPaint.class);
            doReturn(paint).when(stepSpy).getSubtitleTextPaint();

            stepSpy.measureSubtitleHorizontalDimensions();

            verify(paint).measureText(summary);
        }

        @Test
        public void measureTitleVerticalDimensions_MeasuresUsingTitlePaint() {
            TextPaint paint = mock(TextPaint.class);
            doReturn(mock(Paint.FontMetrics.class)).when(paint).getFontMetrics();
            doReturn(paint).when(stepSpy).getTitleTextPaint();

            stepSpy.measureTitleVerticalDimensions(0);

            // verify that the baseline is being measured using the text bounds
            verify(paint).getTextBounds(title, 0, 1, titleRect);
            // verify that the bottom is being measured using the font metrics
            verify(paint).getFontMetrics();
        }

        @Test
        public void measureSubtitleVerticalDimensions_MeasuresUsingSubtitlePaint() {
            stepSpy.markComplete();
            TextPaint paint = mock(TextPaint.class);
            doReturn(mock(Paint.FontMetrics.class)).when(paint).getFontMetrics();
            doReturn(paint).when(stepSpy).getSubtitleTextPaint();

            stepSpy.measureSubtitleVerticalDimensions();

            // verify that the baseline and bottom are measured using the font metrics
            verify(paint, times(2)).getFontMetrics();
        }

        @Test
        public void calculateHorizontalUsedSpace_ShouldReturnPaddingPlusIconLeftAdjustment() {
            int iconWidth = 40;
            when(stepSpy.calculateStepDecoratorIconWidth()).thenReturn(iconWidth);
            int leftMargin = 20;
            int rightMargin = 10;
            innerLayoutParams.leftMargin = leftMargin;
            innerLayoutParams.rightMargin = rightMargin;

            int horizontalPadding = stepSpy.calculateHorizontalUsedSpace(innerView);

            assertThat(horizontalPadding)
                    .isEqualTo(leftMargin + rightMargin + iconWidth);
        }

        @Test
        public void calculateVerticalUsedSpace_ShouldReturnAllMargins() {
            int topMargin = 10;
            int bottomMargin = 20;
            innerLayoutParams.topMargin = topMargin;
            innerLayoutParams.bottomMargin = bottomMargin;

            int verticalPadding = stepSpy.calculateVerticalUsedSpace(innerView);

            assertThat(verticalPadding).isEqualTo(topMargin + bottomMargin);
        }

        @Test
        public void calculateStepDecoratorWidth_ShouldReturnIconSumPlusMaxTextWidth() {
            int iconWidth = 40;
            float textWidth = 10f;
            when(stepSpy.calculateStepDecoratorIconWidth()).thenReturn(iconWidth);
            when(stepSpy.calculateStepDecoratorTextWidth()).thenReturn(textWidth);

            int stepDecoratorWidth = stepSpy.calculateStepDecoratorWidth();

            assertThat(stepDecoratorWidth)
                    .isEqualTo(iconWidth + (int) textWidth);
        }

        @Test
        public void calculateStepDecoratorIconWidth_ShouldReturnIconWidthPlusMarginSum() {
            int iconDimension = 24;
            int iconMarginRight = 8;
            when(common.getIconDimension()).thenReturn(iconDimension);
            when(common.getIconMarginRight()).thenReturn(iconMarginRight);
            int iconWidth = stepSpy.calculateStepDecoratorIconWidth();

            assertThat(iconWidth)
                    .isEqualTo(iconDimension + iconMarginRight);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderTitle_ShouldReturnTitle() {
            final float titleWidth = 20f;
            final float subtitleWidth = 10f;
            when(stepSpy.getTitleWidth()).thenReturn(titleWidth);
            when(stepSpy.getSubtitleWidth()).thenReturn(subtitleWidth);

            float width = stepSpy.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(titleWidth);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderSubtitle_ShouldReturnSubtitle() {
            final float titleWidth = 20f;
            final float subtitleWidth = 25f;
            when(stepSpy.getTitleWidth()).thenReturn(titleWidth);
            when(stepSpy.getSubtitleWidth()).thenReturn(subtitleWidth);

            float width = stepSpy.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(subtitleWidth);
        }

        @Test
        public void measureStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
            int iconDimension = 24;
            when(stepSpy.getIconDimension()).thenReturn(iconDimension);
            float lessThanHalfIconDimen = (iconDimension - 2) / 2;
            when(stepSpy.getTitleBottomRelativeToStepTop()).thenReturn(lessThanHalfIconDimen);
            when(stepSpy.getSubtitleBottomRelativeToTitleBottom()).thenReturn(lessThanHalfIconDimen);

            stepSpy.measureStepDecoratorHeight();
            int height = stepSpy.getDecoratorHeight();

            assertThat(height).isEqualTo(iconDimension);
        }

        @Test
        public void measureStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
            int iconDimension = 24;
            when(stepSpy.getIconDimension()).thenReturn(iconDimension);
            float twiceIconHeight = iconDimension * 2;
            when(stepSpy.getTitleBottomRelativeToStepTop()).thenReturn(twiceIconHeight);
            when(stepSpy.getSubtitleBottomRelativeToTitleBottom()).thenReturn(twiceIconHeight);

            stepSpy.measureStepDecoratorHeight();
            int height = stepSpy.getDecoratorHeight();

            assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
        }
    }

    public static abstract class GivenStepSpyWithStandardHeights extends GivenAStepSpy {
        static final float STANDARD_TITLE_HEIGHT = 10f;
        static final float STANDARD_SUBTITLE_HEIGHT = 10f;
        static final int STANDARD_INNER_HEIGHT = 100;
        static final int STANDARD_CONTINUE_HEIGHT = 20;

        @Before
        public void givenTestStepWithStandardTextHeights() {
            when(stepSpy.getTitleBottomRelativeToStepTop()).thenReturn(STANDARD_TITLE_HEIGHT);
            when(stepSpy.getSubtitleBottomRelativeToTitleBottom()).thenReturn(STANDARD_SUBTITLE_HEIGHT);

            when(innerView.getHeight()).thenReturn(STANDARD_INNER_HEIGHT);
            when(continueButton.getHeight()).thenReturn(STANDARD_CONTINUE_HEIGHT);
        }
    }

    public static class GivenInactiveStepSpyWithStandardHeights extends GivenStepSpyWithStandardHeights {
        @Before
        public void givenInactiveTestStepWithStandardHeights() {
            stepSpy.setActive(false);
        }

        @Test
        public void calculateYDistanceToNextStep_ShouldReturnTotalTextHeightPlusBottomMargin() {
            int inactiveBottomMargin = 40;
            when(common.getInactiveBottomMarginToNextStep()).thenReturn(inactiveBottomMargin);

            int yDistance = stepSpy.calculateYDistanceToNextStep();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + STANDARD_SUBTITLE_HEIGHT
                            + inactiveBottomMargin));
        }
    }

    public static class GivenActiveStepSpyWithStandardHeights extends GivenStepSpyWithStandardHeights {
        @Before
        public void givenActiveTestStepWithStandardHeights() {
            stepSpy.setActive(true);
        }

        @Test
        public void
        calculateYDistanceToNextStep_ShouldReturnTitleHeightPlusTitleMarginPlusTotalInnerHeightPlusBottomMargin() {
            int titleMargin = 20;
            int activeBottomMargin = 40;
            when(common.getTitleMarginBottomToInnerView()).thenReturn(titleMargin);
            when(common.getActiveBottomMarginToNextStep()).thenReturn(activeBottomMargin);

            int yDistance = stepSpy.calculateYDistanceToNextStep();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + titleMargin
                            + STANDARD_INNER_HEIGHT + STANDARD_CONTINUE_HEIGHT
                            + activeBottomMargin));
        }
    }

    public static class GivenStepIsInactive extends GivenAStep {
        @Before
        public void givenStepIsInactive() {
            step.setActive(false);
        }

        @Test
        public void getIconBackground_ShouldReturnInactiveStepPaint() {
            Paint paint = step.getIconBackground();

            assertThat(paint).isSameAs(ICON_INACTIVE_PAINT);
        }

        @Test
        public void getIconBackground_CompleteStep_ShouldReturnCompleteStepPaint() {
            step.markComplete();

            Paint paint = step.getIconBackground();

            assertThat(paint).isSameAs(ICON_COMPLETE_PAINT);
        }

        @Test
        public void markComplete_ShouldClearError() {
            step.setError("some error");

            step.markComplete();

            assertThat(step.isComplete()).isTrue();
            assertThat(step.hasError()).isFalse();
        }

        @Test
        public void setError_ShouldMarkIncomplete() {
            step.markComplete();

            step.setError("some error");

            assertThat(step.hasError()).isTrue();
            assertThat(step.isComplete()).isFalse();
        }

        @Test
        public void hasError_ShouldReturnFalseByDefault() {
            assertThat(step.hasError()).isFalse();
        }

        @Test
        public void hasError_ShouldReturnTrueWhenThereIsAnError() {
            step.setError("some error");

            assertThat(step.hasError()).isTrue();
        }

        @Test
        public void getSubtitle_ShouldReturnEmptyByDefault() {
            String subtitle = step.getSubtitle();

            assertThat(subtitle).isEmpty();
        }

        @Test
        public void getSubtitle_CompleteStep_ShouldReturnSummary() {
            String summary = "summary";
            step.setSummary(summary);
            step.markComplete();

            String subtitle = step.getSubtitle();

            assertThat(subtitle).isEqualTo(summary);
        }

        @Test
        public void getSubtitle_HasError_ShouldReturnError() {
            String error = "some error";
            step.setError(error);

            String subtitle = step.getSubtitle();

            assertThat(subtitle).isEqualTo(error);
        }

        @Test
        public void getTitleTextPaint_ShouldReturnInactiveStepPaint() {
            TextPaint inactivePaint = mock(TextPaint.class);
            when(common.getTitleInactiveTextPaint()).thenReturn(inactivePaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(inactivePaint);
        }

        @Test
        public void getTitleTextPaint_HasError_ShouldReturnErrorStepPaint() {
            step.setError("error");
            TextPaint errorPaint = mock(TextPaint.class);
            when(common.getTitleErrorTextPaint()).thenReturn(errorPaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(errorPaint);
        }

        @Test
        public void getTitleTextPaint_CompleteStep_ShouldReturnCompleteStepPaint() {
            step.markComplete();
            TextPaint completePaint = mock(TextPaint.class);
            when(common.getTitleCompleteTextPaint()).thenReturn(completePaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(completePaint);
        }

        @Test
        public void getSubtitleTextPaint_CompleteStep_ShouldReturnSummaryPaint() {
            step.markComplete();
            TextPaint summaryPaint = mock(TextPaint.class);
            when(common.getSummaryTextPaint()).thenReturn(summaryPaint);

            TextPaint paint = step.getSubtitleTextPaint();

            assertThat(paint).isSameAs(summaryPaint);
        }

        @Test
        public void getSubtitleTextPaint_HasError_ShouldReturnErrorPaint() {
            step.setError("error");
            TextPaint errorPaint = mock(TextPaint.class);
            when(common.getSubtitleErrorTextPaint()).thenReturn(errorPaint);

            TextPaint paint = step.getSubtitleTextPaint();

            assertThat(paint).isSameAs(errorPaint);
        }

        @Test
        public void getBottomMarginToNextStep_ShouldReturnInactiveMargin() {
            int inactiveBottomMargin = 40;
            when(common.getInactiveBottomMarginToNextStep()).thenReturn(inactiveBottomMargin);

            int margin = step.getBottomMarginToNextStep();

            assertThat(margin).isEqualTo(inactiveBottomMargin);
        }

        @Test
        public void calculateConnectorStartY_ShouldAccountForIcon() {
            int iconDimension = 24;
            int iconMarginVertical = 8;
            when(common.getIconDimension()).thenReturn(iconDimension);
            when(common.getIconMarginVertical()).thenReturn(iconMarginVertical);

            int startY = step.calculateConnectorStartY();

            assertThat(startY)
                    .isEqualTo(iconDimension + iconMarginVertical);
        }

        @Test
        public void calculateConnectorStopY_ShouldStopAtIconMargin() {
            int yDistance = 300;
            int iconMarginVertical = 8;
            when(common.getIconMarginVertical()).thenReturn(iconMarginVertical);

            int startY = step.calculateConnectorStopY(yDistance);

            assertThat(startY)
                    .isEqualTo(yDistance - iconMarginVertical);
        }
    }

    public static class GivenStepIsActive extends GivenAStep {
        @Before
        public void givenStepIsActive() {
            step.setActive(true);
        }

        @Test
        public void getIconBackground_ShouldReturnActiveStepPaint() {
            Paint paint = step.getIconBackground();

            assertThat(paint).isSameAs(ICON_ACTIVE_PAINT);
        }

        @Test
        public void getIconBackground_CompleteStep_ShouldReturnActiveStepPaint() {
            step.markComplete();

            Paint paint = step.getIconBackground();

            assertThat(paint).isSameAs(ICON_ACTIVE_PAINT);
        }

        @Test
        public void getSubtitle_CompleteStep_ShouldReturnEmpty() {
            String summary = "summary";
            step.setSummary(summary);
            step.markComplete();

            String subtitle = step.getSubtitle();

            assertThat(subtitle).isEmpty();
        }

        @Test
        public void getTitleTextPaint_ShouldReturnActiveStepPaintByDefault() {
            TextPaint activePaint = mock(TextPaint.class);
            when(common.getTitleActiveTextPaint()).thenReturn(activePaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(activePaint);
        }

        @Test
        public void getTitleTextPaint_CompleteStep_ShouldReturnActiveStepPaint() {
            step.markComplete();
            TextPaint activePaint = mock(TextPaint.class);
            when(common.getTitleActiveTextPaint()).thenReturn(activePaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(activePaint);
        }

        @Test
        public void getTitleTextPaint_HasError_ShouldReturnErrorStepPaint() {
            step.setError("some error");
            TextPaint errorPaint = mock(TextPaint.class);
            when(common.getTitleErrorTextPaint()).thenReturn(errorPaint);

            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(errorPaint);
        }

        @Test
        public void getBottomMarginToNextStep_ShouldReturnActiveMargin() {
            int activeBottomMargin = 40;
            when(common.getActiveBottomMarginToNextStep()).thenReturn(activeBottomMargin);

            int margin = step.getBottomMarginToNextStep();

            assertThat(margin).isEqualTo(activeBottomMargin);
        }
    }
}