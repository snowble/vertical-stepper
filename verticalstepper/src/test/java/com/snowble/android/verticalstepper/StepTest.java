package com.snowble.android.verticalstepper;

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
        protected VerticalStepper.Step.Common common;

        @Before
        public void givenCommonValues() {
            common = new VerticalStepper.Step.Common();
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

    public static class GivenATestStep extends GivenCommonValues {
        private static class TestStep extends VerticalStepper.Step {
            private final float titleWidth;
            private final float titleHeight;
            private final float summaryWidth;
            private final float summaryHeight;

            TestStep(Common common, float titleWidth, float titleHeight, float summaryWidth, float summaryHeight) {
                super(mock(View.class),
                        mock(VerticalStepper.InternalTouchView.class), mock(AppCompatButton.class), common);
                this.titleWidth = titleWidth;
                this.titleHeight = titleHeight;
                this.summaryWidth = summaryWidth;
                this.summaryHeight = summaryHeight;
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

            @Override
            public float getTitleWidth() {
                return titleWidth;
            }

            @Override
            float getTitleBottomRelativeToStepTop() {
                return titleHeight;
            }

            @Override
            public float getSummaryWidth() {
                return summaryWidth;
            }

            @Override
            float getSummaryBottomRelativeToTitleBottom() {
                return summaryHeight;
            }
        }

        @Test
        public void calculateStepDecoratorWidth_ShouldReturnSumOfIconSumAndMaxTextWidth() {
            int iconWidth = common.getIconDimension() + common.getIconMarginRight();
            final float textWidth = 10f;
            VerticalStepper.Step step = new TestStep(common, textWidth, 0, textWidth, 0);

            int stepDecoratorWidth = step.calculateStepDecoratorWidth();

            assertThat(stepDecoratorWidth)
                    .isEqualTo(iconWidth + (int) textWidth);
        }

        @Test
        public void calculateStepDecoratorIconWidth_ShouldReturnIconWidthAndMarginSum() {
            VerticalStepper.Step step = new TestStep(common, 0, 0, 0, 0);
            int iconWidth = step.calculateStepDecoratorIconWidth();

            assertThat(iconWidth)
                    .isEqualTo(common.getIconDimension() + common.getIconMarginRight());
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderTitle_ShouldReturnTitle() {
            final float titleWidth = 20f;
            final float summaryWidth = 10f;
            VerticalStepper.Step step = new TestStep(common, titleWidth, 0, summaryWidth, 0);

            float width = step.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(titleWidth);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderSummary_ShouldReturnSummary() {
            final float titleWidth = 20f;
            final float summaryWidth = 25f;
            VerticalStepper.Step step = new TestStep(common, titleWidth, 0, summaryWidth, 0);

            float width = step.calculateStepDecoratorTextWidth();

            assertThat(width).isEqualTo(summaryWidth);
        }

        @Test
        public void calculateStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
            int iconDimension = common.getIconDimension();
            float lessThanHalfIconHeight = (iconDimension - 2) / 2;
            VerticalStepper.Step step = new TestStep(common, 0, lessThanHalfIconHeight, 0, lessThanHalfIconHeight);

            step.measureStepDecoratorHeight();
            int height = step.getDecoratorHeight();

            assertThat(height).isEqualTo(iconDimension);
        }

        @Test
        public void calculateStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
            float twiceIconHeight = common.getIconDimension() * 2;
            VerticalStepper.Step step = new TestStep(common, 0, twiceIconHeight, 0, twiceIconHeight);

            step.measureStepDecoratorHeight();
            int height = step.getDecoratorHeight();

            assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
        }
    }

    public static abstract class GivenAStep extends GivenCommonValues {
        protected VerticalStepper.Step step;
        protected VerticalStepper.LayoutParams layoutParams;

        @Before
        public void givenAStep() {
            ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
            Activity activity = activityController.create().get();
            View innerView = mock(View.class);
            layoutParams = RobolectricTestUtils.createTestLayoutParams(activity);
            when(innerView.getLayoutParams()).thenReturn(layoutParams);

            step = new VerticalStepper.Step(innerView, new VerticalStepper.InternalTouchView(activity),
                    new AppCompatButton(activity), common);
        }
    }

    public static class GivenStepIsTheLastOne extends GivenAStep {
        @Test
        public void getBottomMarginToNextStep_ShouldReturnZeroSizedMargin() {
            int margin = step.getBottomMarginToNextStep(true);

            assertThat(margin).isEqualTo(VerticalStepper.Step.ZERO_SIZE_MARGIN);
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
        public void calculateInnerViewHorizontalUsedSpace_ShouldReturnPaddingAndIconLeftAdjustment() {
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