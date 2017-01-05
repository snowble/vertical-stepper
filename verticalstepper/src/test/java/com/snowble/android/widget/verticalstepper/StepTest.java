package com.snowble.android.widget.verticalstepper;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class StepTest {
    private static final TextPaint TITLE_ACTIVE_PAINT = new TextPaint();
    private static final TextPaint TITLE_INACTIVE_PAINT = new TextPaint();
    private static final Paint ICON_ACTIVE_PAINT = new TextPaint();
    private static final Paint ICON_INACTIVE_PAINT = new TextPaint();
    private static final int ICON_DIMENSION = 24;
    private static final int ICON_MARGIN_RIGHT = 12;
    private static final int ACTIVE_BOTTOM_MARGIN = 48;
    private static final int INACTIVE_BOTTOM_MARGIN = 40;

    @RunWith(RobolectricTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
    public static abstract class GivenCommonValues {
        protected Step.Common common;

        @Before
        public void givenCommonValues() {
            common = new Step.Common();
            common.setTitleActiveTextPaint(TITLE_ACTIVE_PAINT)
                    .setTitleInactiveTextPaint(TITLE_INACTIVE_PAINT)
                    .setIconDimension(ICON_DIMENSION)
                    .setIconMarginRight(ICON_MARGIN_RIGHT)
                    .setIconActiveBackgroundPaint(ICON_ACTIVE_PAINT)
                    .setIconInactiveBackgroundPaint(ICON_INACTIVE_PAINT)
                    .setActiveBottomMarginToNextStep(ACTIVE_BOTTOM_MARGIN)
                    .setInactiveBottomMarginToNextStep(INACTIVE_BOTTOM_MARGIN);
        }
    }

    public static abstract class GivenATestStep extends GivenCommonValues {
        protected View innerView;
        protected VerticalStepper.InternalTouchView touchView;
        protected AppCompatButton continueButton;

        protected TestStep step;

        protected class TestStep extends Step {
            private float titleWidth;
            private float titleHeight;
            private float summaryWidth;
            private float summaryHeight;

            TestStep() {
                super(innerView, touchView, continueButton, common);
            }

            @Override
            void initTextValues(@NonNull VerticalStepper.LayoutParams lp) {
                // Do nothing. Don't attempt to extract it from null layout params
            }

            @Override
            void validateTitle() {
                // Do nothing. We don't care about title validation
            }

            @Override
            void measureTitleHorizontalDimensions() {
                // Do nothing. The dimensions will be explicitly set
            }

            @Override
            void measureSummaryHorizontalDimensions() {
                // Do nothing. The dimensions will be explicitly set
            }

            @Override
            void measureTitleVerticalDimensions(int heightToCenterIn) {
                // Do nothing. The dimensions will be explicitly set
            }

            @Override
            void measureSummaryVerticalDimensions() {
                // Do nothing. The dimensions will be explicitly set
            }

            public TestStep setTestStepTitleWidth(float width) {
                titleWidth = width;
                return this;
            }

            @Override
            public float getTitleWidth() {
                return titleWidth;
            }

            public TestStep setTestStepTitleHeight(float height) {
                titleHeight = height;
                return this;
            }

            @Override
            float getTitleBottomRelativeToStepTop() {
                return titleHeight;
            }

            public TestStep setTestStepSummaryWidth(float width) {
                summaryWidth = width;
                return this;
            }

            @Override
            public float getSummaryWidth() {
                return summaryWidth;
            }

            public TestStep setTestStepSummaryHeight(float height) {
                summaryHeight = height;
                return this;
            }

            @Override
            float getSummaryBottomRelativeToTitleBottom() {
                return summaryHeight;
            }
        }

        @Before
        public void givenATestStep() {
            innerView = mock(View.class);
            touchView = mock(VerticalStepper.InternalTouchView.class);
            continueButton = mock(AppCompatButton.class);
            step = new TestStep();
        }
    }

    public static class GivenEmptyTestStep extends GivenATestStep {
        @Test
        public void calculateStepDecoratorWidth_ShouldReturnIconSumPlusMaxTextWidth() {
            int iconWidth = common.getIconDimension() + common.getIconMarginRight();
            final float textWidth = 10f;
            step.setTestStepTitleWidth(textWidth)
                    .setTestStepSummaryWidth(textWidth);

            int stepDecoratorWidth = step.calculateStepDecoratorWidth();

            assertThat(stepDecoratorWidth)
                    .isEqualTo(iconWidth + (int) textWidth);
        }

        @Test
        public void calculateStepDecoratorIconWidth_ShouldReturnIconWidthPlusMarginSum() {
            int iconWidth = step.calculateStepDecoratorIconWidth();

            assertThat(iconWidth)
                    .isEqualTo(ICON_DIMENSION + ICON_MARGIN_RIGHT);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderTitle_ShouldReturnTitle() {
            final float titleWidth = 20f;
            final float summaryWidth = 10f;
            step.setTestStepTitleWidth(titleWidth)
                    .setTestStepSummaryWidth(summaryWidth);

            float width = step.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(titleWidth);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderSummary_ShouldReturnSummary() {
            final float titleWidth = 20f;
            final float summaryWidth = 25f;
            step.setTestStepTitleWidth(titleWidth)
                    .setTestStepSummaryWidth(summaryWidth);

            float width = step.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(summaryWidth);
        }

        @Test
        public void measureStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
            int iconDimension = common.getIconDimension();
            float lessThanHalfIconHeight = (iconDimension - 2) / 2;
            step.setTestStepTitleHeight(lessThanHalfIconHeight)
                    .setTestStepSummaryHeight(lessThanHalfIconHeight);

            step.measureStepDecoratorHeight();
            int height = step.getDecoratorHeight();

            assertThat(height).isEqualTo(iconDimension);
        }

        @Test
        public void measureStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
            float twiceIconHeight = common.getIconDimension() * 2;
            step.setTestStepTitleHeight(twiceIconHeight)
                    .setTestStepSummaryHeight(twiceIconHeight);

            step.measureStepDecoratorHeight();
            int height = step.getDecoratorHeight();

            assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
        }
    }

    public static abstract class GivenTestStepWithStandardHeights extends GivenATestStep {
        protected static final float STANDARD_TITLE_HEIGHT = 10f;
        protected static final float STANDARD_SUMMARY_HEIGHT = 10f;
        protected static final int STANDARD_INNER_HEIGHT = 100;
        protected static final int STANDARD_CONTINUE_HEIGHT = 20;

        @Before
        public void givenTestStepWithStandardTextHeights() {
            step.setTestStepTitleHeight(STANDARD_TITLE_HEIGHT)
                    .setTestStepSummaryHeight(STANDARD_SUMMARY_HEIGHT);

            when(innerView.getHeight()).thenReturn(STANDARD_INNER_HEIGHT);
            when(continueButton.getHeight()).thenReturn(STANDARD_CONTINUE_HEIGHT);
        }
    }

    public static class GivenInactiveTestStepWithStandardHeights extends GivenTestStepWithStandardHeights {
        @Before
        public void givenInactiveTestStepWithStandardHeights() {
            step.setActive(false);
        }

        @Test
        public void calculateYDistanceToTextBottom_ShouldReturnTotalTextHeight(){
            int yDistance = step.calculateYDistanceToTextBottom();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + STANDARD_SUMMARY_HEIGHT));
        }

        @Test
        public void calculateYDistanceToNextStep_ShouldReturnTotalTextHeightPlusBottomMargin() {
            int yDistance = step.calculateYDistanceToNextStep();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + STANDARD_SUMMARY_HEIGHT + INACTIVE_BOTTOM_MARGIN));
        }
    }

    public static class GivenActiveTestStepWithStandardHeights extends GivenTestStepWithStandardHeights {
        @Before
        public void givenActiveTestStepWithStandardHeights() {
            step.setActive(true);
        }

        @Test
        public void calculateYDistanceToTextBottom_ShouldReturnTitleHeight(){
            int yDistance = step.calculateYDistanceToTextBottom();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT));
        }

        @Test
        public void calculateYDistanceToButtons_ShouldReturnTitleHeightPlusTitleMarginPlusInnerHeight() {
            int titleMargin = 20;
            common.setTitleMarginBottomToInnerView(titleMargin);

            int yDistance = step.calculateYDistanceToButtons();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + titleMargin + STANDARD_INNER_HEIGHT));
        }

        @Test
        public void
        calculateYDistanceToNextStep_ShouldReturnTitleHeightPlusTitleMarginPlusTotalInnerHeightPlusBottomMargin() {
            int titleMargin = 20;
            common.setTitleMarginBottomToInnerView(titleMargin);
            int yDistance = step.calculateYDistanceToNextStep();

            assertThat(yDistance)
                    .isEqualTo((int) (STANDARD_TITLE_HEIGHT + titleMargin + STANDARD_INNER_HEIGHT
                            + STANDARD_CONTINUE_HEIGHT + ACTIVE_BOTTOM_MARGIN));
        }
    }

    public static abstract class GivenAStep extends GivenCommonValues {
        protected Step step;
        protected VerticalStepper.LayoutParams layoutParams;

        @Before
        public void givenAStep() {
            ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
            Activity activity = activityController.create().get();
            View innerView = mock(View.class);
            layoutParams = RobolectricTestUtils.createTestLayoutParams(activity);
            when(innerView.getLayoutParams()).thenReturn(layoutParams);

            step = new Step(innerView, new VerticalStepper.InternalTouchView(activity),
                    new AppCompatButton(activity), common);
        }
    }

    public static class GivenStepIsTheLastOne extends GivenAStep {
        @Test
        public void getBottomMarginToNextStep_ShouldReturnZeroSizedMargin() {
            int margin = step.getBottomMarginToNextStep(true);

            assertThat(margin).isEqualTo(Step.ZERO_SIZE_MARGIN);
        }
    }

    public static class GivenStepIsInactive extends GivenAStep {
        @Before
        public void givenStepIsInactive() {
            step.setActive(false);
        }

        @Test
        public void getIconColor_ShouldReturnInactiveStepPaint() {
            Paint paint = step.getIconColor();

            assertThat(paint).isSameAs(ICON_INACTIVE_PAINT);
        }

        @Test
        public void getTitleTextPaint_ShouldReturnInactiveStepPaint() {
            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(TITLE_INACTIVE_PAINT);
        }

        @Test
        public void calculateInnerViewHorizontalUsedSpace_ShouldReturnPaddingPlusIconLeftAdjustment() {
            int leftMargin = 20;
            int rightMargin = 10;
            layoutParams.leftMargin = leftMargin;
            layoutParams.rightMargin = rightMargin;
            int horizontalPadding = step.calculateInnerViewHorizontalUsedSpace();

            assertThat(horizontalPadding)
                    .isEqualTo(leftMargin + rightMargin + step.calculateStepDecoratorIconWidth());
        }

        @Test
        public void calculateInnerViewVerticalUsedSpace_ShouldReturnAllMargins() {
            int topMargin = 10;
            int bottomMargin = 20;
            layoutParams.topMargin = topMargin;
            layoutParams.bottomMargin = bottomMargin;
            int verticalPadding = step.calculateInnerViewVerticalUsedSpace();

            assertThat(verticalPadding).isEqualTo(topMargin + bottomMargin);
        }
    }

    public static class GivenStepIsInactiveAndInTheMiddle extends GivenStepIsInactive {
        @Test
        public void getBottomMarginToNextStep_ShouldReturnInactiveMargin() {
            int margin = step.getBottomMarginToNextStep(false);

            assertThat(margin).isEqualTo(INACTIVE_BOTTOM_MARGIN);
        }
    }

    public static class GivenStepIsActive extends GivenAStep {
        @Before
        public void givenStepIsActive() {
            step.setActive(true);
        }

        @Test
        public void getIconColor_ShouldReturnInactiveStepPaint() {
            Paint paint = step.getIconColor();

            assertThat(paint).isSameAs(ICON_ACTIVE_PAINT);
        }

        @Test
        public void getTitleTextPaint_ShouldReturnActiveStepPaint() {
            TextPaint paint = step.getTitleTextPaint();

            assertThat(paint).isSameAs(TITLE_ACTIVE_PAINT);
        }
    }

    public static class GivenStepIsActiveAndInTheMiddle extends GivenStepIsActive {
        @Test
        public void getBottomMarginToNextStep_ShouldReturnActiveMargin() {
            int margin = step.getBottomMarginToNextStep(false);

            assertThat(margin).isEqualTo(ACTIVE_BOTTOM_MARGIN);
        }
    }
}