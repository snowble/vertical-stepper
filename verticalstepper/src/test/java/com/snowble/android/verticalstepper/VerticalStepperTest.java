package com.snowble.android.verticalstepper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class VerticalStepperTest {

    @RunWith(RobolectricTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
    public abstract static class StepperContext {
        protected Activity activity;
        protected VerticalStepper stepper;

        protected ArgumentMatcher<Integer> isGreaterThanZero;

        @Before
        public void beforeStepperContext() {
            ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
            activity = activityController.create().get();
            stepper = new VerticalStepper(activity);

            isGreaterThanZero = new ArgumentMatcher<Integer>() {
                @Override
                public boolean matches(Integer arg) {
                    return arg > 0;
                }
            };
        }
    }

    public static class ZeroStepsTests extends StepperContext {
        @SuppressLint("PrivateResource") // https://code.google.com/p/android/issues/detail?id=230985
        @Test
        public void initPropertiesFromAttrs_NoAttrsSet_ShouldUseDefaults() {
            stepper.initPropertiesFromAttrs(null, 0, 0);

            assertThat(stepper.iconActiveColor).isEqualTo(getColor(R.color.bg_active_icon));
            assertThat(stepper.iconInactiveColor).isEqualTo(getColor(R.color.bg_inactive_icon));
            assertThat(stepper.continueButtonStyle)
                    .isEqualTo(android.support.v7.appcompat.R.style.Widget_AppCompat_Button_Colored);
        }

        @SuppressLint("PrivateResource") // https://code.google.com/p/android/issues/detail?id=230985
        @Test
        public void initPropertiesFromAttrs_AttrsSet_ShouldUseAttrs() {
            Robolectric.AttributeSetBuilder builder = Robolectric.buildAttributeSet();
            builder.addAttribute(R.attr.iconColorActive, "@android:color/black");
            builder.addAttribute(R.attr.iconColorInactive, "@android:color/darker_gray");
            builder.addAttribute(R.attr.continueButtonStyle, "@style/Widget.AppCompat.Button.Borderless");

            stepper.initPropertiesFromAttrs(builder.build(), 0, 0);

            assertThat(stepper.iconActiveColor).isEqualTo(getColor(android.R.color.black));
            assertThat(stepper.iconInactiveColor).isEqualTo(getColor(android.R.color.darker_gray));
            assertThat(stepper.continueButtonStyle)
                    .isEqualTo(android.support.v7.appcompat.R.style.Widget_AppCompat_Button_Borderless);
        }

        private int getColor(int colorRes) {
            return ResourcesCompat.getColor(activity.getResources(), colorRes, activity.getTheme());
        }

        @Test
        public void getResolvedAttributeData_MissingAttr_ShouldReturnDefault() {
            int defaultData = 2;

            int data = stepper.getResolvedAttributeData(R.attr.colorPrimary, defaultData);

            assertThat(data).isEqualTo(defaultData);
        }

        @Test
        public void initChildViews_ShouldHaveEmptyInnerViews() {
            stepper.initStepViews();

            assertThat(stepper.steps).isEmpty();
        }

        @Test
        public void calculateWidth_ShouldReturnHorizontalPadding() {
            int width = stepper.calculateWidth();

            assertThat(width)
                    .isEqualTo(stepper.calculateHorizontalPadding());
        }

        @Test
        public void doMeasurement_UnspecifiedSpecs_ShouldMeasurePadding() {
            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            stepper.doMeasurement(ms, ms);

            assertThat(stepper.getMeasuredHeight()).isEqualTo(stepper.calculateVerticalPadding());
            assertThat(stepper.getMeasuredWidth()).isEqualTo(stepper.calculateHorizontalPadding());
        }

        @Test
        public void doMeasurement_AtMostSpecsRequiresClipping_ShouldMeasureToAtMostValues() {
            int width = stepper.calculateHorizontalPadding() / 2;
            int height = stepper.calculateVerticalPadding() / 2;
            int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
            int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);

            stepper.doMeasurement(wms, hms);

            assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
            assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
        }

        @Test
        public void doMeasurement_ExactlySpecsRequiresClipping_ShouldMeasureToExactValues() {
            int width = stepper.calculateHorizontalPadding() / 2;
            int height = stepper.calculateVerticalPadding() / 2;
            int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

            stepper.doMeasurement(wms, hms);

            assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
            assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
        }

        @Test
        public void doMeasurement_ExactlySpecsRequiresExpanding_ShouldMeasureToExactValues() {
            int width = stepper.calculateHorizontalPadding() * 2;
            int height = stepper.calculateVerticalPadding() * 2;
            int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

            stepper.doMeasurement(wms, hms);

            assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
            assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
        }

        @Test
        public void calculateHeight_ShouldReturnVerticalPadding() {
            int width = stepper.calculateHeight();

            assertThat(width)
                    .isEqualTo(stepper.calculateVerticalPadding());
        }

        @Test
        public void calculateHorizontalPadding_ShouldReturnAllPadding() {
            int horizontalPadding = stepper.calculateHorizontalPadding();

            assertThat(horizontalPadding)
                    .isEqualTo((stepper.outerHorizontalPadding * 2) +
                            stepper.getPaddingLeft() + stepper.getPaddingRight());
        }

        @Test
        public void calculateVerticalPadding_ShouldReturnAllPadding() {
            int verticalPadding = stepper.calculateVerticalPadding();

            assertThat(verticalPadding)
                    .isEqualTo((stepper.outerVerticalPadding * 2) +
                            stepper.getPaddingTop() + stepper.getPaddingBottom());
        }

        @Test
        public void calculateInnerViewHorizontalUsedSpace_ShouldReturnPaddingAndIconLeftAdjustment() {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity, 20, 0, 10, 0);

            int horizontalPadding = stepper.calculateInnerViewHorizontalUsedSpace(lp);

            assertThat(horizontalPadding)
                    .isEqualTo(lp.leftMargin + lp.rightMargin + stepper.iconDimension + stepper.iconMarginRight);
        }

        @Test
        public void calculateInnerViewVerticalUsedSpace_ShouldReturnAllMargins() {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity, 0, 10, 0, 20);

            int verticalPadding = stepper.calculateInnerViewVerticalUsedSpace(lp);

            assertThat(verticalPadding).isEqualTo(lp.topMargin + lp.bottomMargin);
        }
    }

    public abstract static class SingleStepContext extends StepperContext {
        protected View mockInnerView1;
        protected VerticalStepper.InternalTouchView mockTouchView1;
        protected AppCompatButton mockContinueButton1;
        protected VerticalStepper.LayoutParams mockLayoutParams1;
        protected VerticalStepper.Step mockedStep1;

        @Before
        public void beforeSingleStepContext() {
            mockInnerView1 = mock(View.class);
            mockLayoutParams1 = mock(VerticalStepper.LayoutParams.class);
            when(mockInnerView1.getLayoutParams()).thenReturn(mockLayoutParams1);
            mockContinueButton1 = mock(AppCompatButton.class);
            mockTouchView1 = mock(VerticalStepper.InternalTouchView.class);
            mockedStep1 = mock(VerticalStepper.Step.class);
            when(mockedStep1.getInnerView()).thenReturn(mockInnerView1);
            when(mockedStep1.getTouchView()).thenReturn(mockTouchView1);
            when(mockedStep1.getContinueButton()).thenReturn(mockContinueButton1);

            stepper.initStepView(mockedStep1);

            clearInvocations(mockInnerView1);
            clearInvocations(mockLayoutParams1);
            clearInvocations(mockContinueButton1);
            clearInvocations(mockTouchView1);
            clearInvocations(mockedStep1);
        }

        protected void assertExpectedStep1MeasureSpecs(VerticalStepper.LayoutParams lp,
                                                     int maxWidth, int maxHeight,
                                                     int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep1MeasureSpecs(), lp, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        protected List<Integer> captureStep1MeasureSpecs() {
            return captureStepMeasureSpecs(mockInnerView1, mockContinueButton1);
        }

        protected List<Integer> captureStepMeasureSpecs(View innerView, View continueButton) {
            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(innerView).measure(captor.capture(), captor.capture());
            verify(continueButton).measure(captor.capture(), captor.capture());
            return captor.getAllValues();
        }

        protected void assertExpectedStepMeasureSpecs(List<Integer> measureSpecs, VerticalStepper.LayoutParams lp,
                                                      int maxWidth, int maxHeight,
                                                      int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            int innerWms = measureSpecs.get(0);
            assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms);
            int innerHms = measureSpecs.get(1);
            assertExpectedHeightMeasureSpec(maxHeight, innerHms, additionalInnerUsedSpace);

            int continueWms = measureSpecs.get(2);
            assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms);
            int continueHms = measureSpecs.get(3);
            assertExpectedHeightMeasureSpec(maxHeight, continueHms, additionalContinueUsedSpace);
        }

        protected void assertExpectedHeightMeasureSpec(int maxHeight, int heightMeasureSpec,
                                                       int additionalUsedSpace) {
            int verticalUsedSpace =
                    stepper.calculateVerticalPadding() + additionalUsedSpace;
            assertThat(View.MeasureSpec.getSize(heightMeasureSpec))
                    .isEqualTo(maxHeight - verticalUsedSpace);
        }

        protected void assertExpectedWidthMeasureSpec(VerticalStepper.LayoutParams lp,
                                                      int maxWidth, int widthMeasureSpec) {
            int horizontalUsedSpace =
                    stepper.calculateInnerViewHorizontalUsedSpace(lp) + stepper.calculateHorizontalPadding();
            assertThat(View.MeasureSpec.getSize(widthMeasureSpec))
                    .isEqualTo(maxWidth - horizontalUsedSpace);
        }

        protected void mockLayoutParamsWidths(float titleWidth, float summaryWidth) {
            when(mockedStep1.getTitleWidth()).thenReturn(titleWidth);
            when(mockedStep1.getSummaryWidth()).thenReturn(summaryWidth);
        }

        protected void mockLayoutParamsHeights(float titleBottom, float summaryBottom) {
            when(mockedStep1.getTitleBottomRelativeToStepTop()).thenReturn(titleBottom);
            when(mockedStep1.getSummaryBottomRelativeToTitleBottom()).thenReturn(summaryBottom);
        }

        protected void measureChildViews(int maxWidth, int maxHeight) {
            int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
            int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
            stepper.measureChildViews(wms, hms);
        }
    }

    public static class SingleStepTests extends SingleStepContext {
        @Test
        public void initChildViews_ShouldHaveInnerViewsWithSingleElement() {
            assertThat(stepper.steps)
                    .hasSize(1)
                    .doesNotContainNull();
        }

        @Test
        public void initInnerView_ShouldSetVisibilityToGone() {
            stepper.initStepView(mockedStep1);

            verify(mockInnerView1).setVisibility(View.GONE);
        }

        @Test
        public void initInnerView_ShouldInitializeStepViews() {
            assertThat(stepper.steps)
                    .hasSize(1)
                    .doesNotContainNull();

            VerticalStepper.Step step = stepper.steps.get(0);
            assertThat(step.getTouchView())
                    .isNotNull();
            assertThat(step.getContinueButton())
                    .isNotNull();
        }

        @Test
        public void initTouchView_ShouldSetClickListener() {
            stepper.initTouchView(mockedStep1);

            verify(mockTouchView1).setOnClickListener((View.OnClickListener) notNull());
        }

        @Test
        public void initTouchView_ShouldAttachToStepper() {
            stepper.initTouchView(mockedStep1);

            assertThat(stepper.getChildCount()).isEqualTo(1);
        }

        @Test
        public void initNavButtons_ShouldSetVisibilityToGone() {
            stepper.initNavButtons(mockedStep1);

            verify(mockContinueButton1).setVisibility(View.GONE);
        }

        @Test
        public void initNavButtons_ShouldSetClickListener() {
            stepper.initNavButtons(mockedStep1);

            verify(mockContinueButton1).setOnClickListener((View.OnClickListener) notNull());
        }

        @Test
        public void initNavButtons_ShouldAttachToStepper() {
            stepper.initNavButtons(mockedStep1);

            assertThat(stepper.getChildCount()).isEqualTo(1);
        }

        @Test
        public void toggleStepExpandedState_Inactive_ShouldBecomeActiveAndExpanded() {
            testStepToggle(false, View.GONE, true, View.VISIBLE);
        }

        @Test
        public void toggleStepExpandedState_Active_ShouldBecomeInactiveAndCollapsed() {
            testStepToggle(true, View.VISIBLE, false, View.GONE);
        }

        private void testStepToggle(boolean initialActivateState, int initialVisibility,
                                    boolean finalExpectedActiveState, int finalExpectedVisibility) {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockedStep1.isActive()).thenReturn(initialActivateState);

            when(mockContinueButton1.getVisibility()).thenReturn(initialVisibility);

            when(mockInnerView1.getVisibility()).thenReturn(initialVisibility);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);

            stepper.toggleStepExpandedState(mockedStep1);

            verify(mockedStep1).setActive(finalExpectedActiveState);
            verify(mockInnerView1).setVisibility(finalExpectedVisibility);
            verify(mockContinueButton1).setVisibility(finalExpectedVisibility);
        }

        @Test
        public void measureBottomMarginHeights_ShouldHaveMarginHeightsWithSingleElement() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1).setBottomMarginHeight(anyInt());
        }

        @Test
        public void measureChildViews_Active_ShouldHaveChildrenVisibleHeightsWithActualHeight() {
            final int innerViewHeight = 100;
            final int buttonHeight = 50;
            when(mockInnerView1.getMeasuredHeight()).thenReturn(innerViewHeight);
            when(mockContinueButton1.getMeasuredHeight()).thenReturn(buttonHeight);
            when(mockedStep1.isActive()).thenReturn(true);

            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureChildViews(ms, ms);

            verify(mockedStep1).setChildrenVisibleHeight(innerViewHeight + buttonHeight);
        }

        @Test
        public void measureChildViews_InactiveNoMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(lp, maxWidth, maxHeight, stepper.calculateInnerViewVerticalUsedSpace(lp), 0);
        }

        @Test
        public void measureChildViews_InactiveHasMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            int horizontalMargin = 10;
            int verticalMargin = 20;
            VerticalStepper.LayoutParams lp =
                    RobolectricTestUtils.createTestLayoutParams(activity,
                            horizontalMargin / 2, verticalMargin / 2, horizontalMargin / 2, verticalMargin / 2);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(lp, maxWidth, maxHeight, stepper.calculateInnerViewVerticalUsedSpace(lp), 0);
        }

        @Test
        public void measureChildViews_Active_ShouldMeasureNavButtonsAccountingForInnerView() {
            when(mockedStep1.isActive()).thenReturn(true);
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);
            int innerHeight = 200;
            when(mockInnerView1.getMeasuredHeight()).thenReturn(innerHeight);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(lp, maxWidth, maxHeight,
                    stepper.calculateInnerViewVerticalUsedSpace(lp), innerHeight);
        }

        @Test
        public void measureChildViews_Inactive_ShouldMeasureChildrenAccountingForDecorator() {
            int decoratorHeight = 100;
            when(mockedStep1.getDecoratorHeight()).thenReturn(decoratorHeight);
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(lp, maxWidth, maxHeight,
                    stepper.calculateInnerViewVerticalUsedSpace(lp) + decoratorHeight, decoratorHeight);
        }

        @Test
        public void measureTouchView_ShouldMeasureWidthAndHeightExactly() {
            ArgumentCaptor<Integer> wmsCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> hmsCaptor = ArgumentCaptor.forClass(Integer.class);
            int width = 20;

            stepper.measureTouchView(width, mockTouchView1);

            verify(mockTouchView1).measure(wmsCaptor.capture(), hmsCaptor.capture());

            int actualWms = wmsCaptor.getValue();
            assertThat(View.MeasureSpec.getMode(actualWms)).isEqualTo(View.MeasureSpec.EXACTLY);
            assertThat(View.MeasureSpec.getSize(actualWms)).isEqualTo(width);

            int actualHms = hmsCaptor.getValue();
            assertThat(View.MeasureSpec.getMode(actualHms)).isEqualTo(View.MeasureSpec.EXACTLY);
            assertThat(View.MeasureSpec.getSize(actualHms)).isEqualTo(stepper.touchViewHeight);
        }

        @Test
        public void calculateWidth_ShouldReturnHorizontalPaddingAndStepWidth() {
            int innerWidth = stepper.calculateStepDecoratorWidth(mockedStep1) * 2;
            when(mockInnerView1.getMeasuredWidth()).thenReturn(innerWidth);
            when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

            int width = stepper.calculateWidth();

            assertThat(width)
                    .isEqualTo(stepper.calculateHorizontalPadding()
                            + innerWidth + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
        }

        @Test
        public void calculateMaxStepWidth_DecoratorsHaveMaxWidth_ShouldReturnDecoratorsWidth() {
            when(mockInnerView1.getMeasuredWidth()).thenReturn(0);
            when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(stepper.calculateStepDecoratorWidth(mockedStep1));
        }

        @Test
        public void calculateMaxStepWidth_InnerViewHasMaxWidth_ShouldReturnInnerViewWidth() {
            int width = stepper.calculateStepDecoratorWidth(mockedStep1) * 2;
            when(mockInnerView1.getMeasuredWidth()).thenReturn(width);
            when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(width + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
        }

        @Test
        public void calculateMaxStepWidth_NavButtonsHaveMaxWidth_ShouldReturnNavButtonsWidth() {
            int width = stepper.calculateStepDecoratorWidth(mockedStep1) * 2;
            when(mockContinueButton1.getMeasuredWidth()).thenReturn(width);
            when(mockInnerView1.getMeasuredWidth()).thenReturn(0);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(width + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
        }

        @Test
        public void calculateStepDecoratorWidth_ShouldReturnIconAndTextSum() {
            float textWidth = 10f;
            mockLayoutParamsWidths(textWidth, textWidth);

            int iconWidth = stepper.iconDimension + stepper.iconMarginRight;

            assertThat(stepper.calculateStepDecoratorWidth(mockedStep1))
                    .isEqualTo(iconWidth + (int) textWidth);
        }

        @Test
        public void calculateStepDecoratorIconWidth_ShouldReturnIconWidthAndMarginSum() {
            int iconWidth = stepper.calculateStepDecoratorIconWidth();

            assertThat(iconWidth)
                    .isEqualTo(stepper.iconDimension + stepper.iconMarginRight);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderTitle_ShouldReturnTitle() {
            mockLayoutParamsWidths(20f, 10f);

            float width = stepper.calculateStepDecoratorTextWidth(mockedStep1);

            assertThat(width).isEqualTo(20f);
        }

        @Test
        public void calculateStepDecoratorTextWidth_WiderSummary_ShouldReturnSummary() {
            mockLayoutParamsWidths(20f, 25f);

            float width = stepper.calculateStepDecoratorTextWidth(mockedStep1);

            assertThat(width).isEqualTo(25f);
        }

        @Test
        public void calculateStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
            float lessThanHalfIconHeight = (stepper.iconDimension - 2) / 2;
            mockLayoutParamsHeights(lessThanHalfIconHeight, lessThanHalfIconHeight);

            int height = stepper.calculateStepDecoratorHeight(mockedStep1);

            assertThat(height).isEqualTo(stepper.iconDimension);
        }

        @Test
        public void calculateStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
            float twiceIconHeight = stepper.iconDimension * 2;
            mockLayoutParamsHeights(twiceIconHeight, twiceIconHeight);

            int height = stepper.calculateStepDecoratorHeight(mockedStep1);

            assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
        }
    }

    public static abstract class TwoStepsContext extends SingleStepContext {
        protected View mockInnerView2;
        protected VerticalStepper.InternalTouchView mockTouchView2;
        protected AppCompatButton mockContinueButton2;
        protected VerticalStepper.LayoutParams mockLayoutParams2;
        protected VerticalStepper.Step mockedStep2;

        @Before
        public void beforeTwoStepsContext() {
            mockInnerView2 = mock(View.class);
            mockLayoutParams2 = mock(VerticalStepper.LayoutParams.class);
            when(mockInnerView2.getLayoutParams()).thenReturn(mockLayoutParams2);
            mockContinueButton2 = mock(AppCompatButton.class);
            mockTouchView2 = mock(VerticalStepper.InternalTouchView.class);
            mockedStep2 = mock(VerticalStepper.Step.class);
            when(mockedStep2.getInnerView()).thenReturn(mockInnerView2);
            when(mockedStep2.getTouchView()).thenReturn(mockTouchView2);
            when(mockedStep2.getContinueButton()).thenReturn(mockContinueButton2);

            stepper.initStepView(mockedStep2);

            clearInvocations(mockInnerView2);
            clearInvocations(mockLayoutParams2);
            clearInvocations(mockContinueButton2);
            clearInvocations(mockTouchView2);
            clearInvocations(mockedStep2);
        }

        protected void assertExpectedStep2MeasureSpecs(VerticalStepper.LayoutParams lp,
                                                     int maxWidth, int maxHeight,
                                                     int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep2MeasureSpecs(), lp, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        protected List<Integer> captureStep2MeasureSpecs() {
            return captureStepMeasureSpecs(mockInnerView2, mockContinueButton2);
        }
    }

    public static class TwoStepTests extends TwoStepsContext {
        @Test
        public void initChildViews_ShouldHaveInnerViewsWithTwoElements() {
            assertThat(stepper.steps)
                    .hasSize(2)
                    .doesNotContainNull();
        }

        @Test
        public void measureStepDecoratorHeights_ShouldHaveDecoratorHeightsWithTwoElements() {
            stepper.measureStepDecoratorHeights();

            verify(mockedStep1).setDecoratorHeight(intThat(isGreaterThanZero));
            verify(mockedStep2).setDecoratorHeight(intThat(isGreaterThanZero));
        }

        @Test
        public void measureBottomMarginHeights_ShouldHaveMarginHeightsWithTwoElements() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1).setBottomMarginHeight(anyInt());
            verify(mockedStep2).setBottomMarginHeight(anyInt());
        }

        @Test
        public void measureChildViews_ShouldMeasureViews() {
            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureChildViews(ms, ms);

            verify(mockInnerView1).measure(anyInt(), anyInt());
            verify(mockInnerView2).measure(anyInt(), anyInt());
            verify(mockContinueButton1).measure(anyInt(), anyInt());
            verify(mockContinueButton1).measure(anyInt(), anyInt());
        }

        @Test
        public void measureChildViews_ShouldMeasureChildrenAccountingForBottomMargin() {
            int decoratorHeight = 100;
            int bottomMargin = 30;
            when(mockedStep1.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep2.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep1.getBottomMarginHeight()).thenReturn(bottomMargin);
            when(mockedStep2.getBottomMarginHeight()).thenReturn(0);

            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);
            VerticalStepper.LayoutParams lp2 = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView2.getLayoutParams()).thenReturn(lp2);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(lp, maxWidth, maxHeight,
                    stepper.calculateInnerViewVerticalUsedSpace(lp) + decoratorHeight, decoratorHeight);

            assertExpectedStep2MeasureSpecs(lp2, maxWidth, maxHeight,
                    stepper.calculateInnerViewVerticalUsedSpace(lp2) + decoratorHeight * 2 + bottomMargin,
                    decoratorHeight * 2 + bottomMargin);
        }

        @Test
        public void calculateMaxStepWidth_ShouldReturnLargerStepWidth() {
            int width1 = stepper.calculateStepDecoratorWidth(mockedStep1) * 2;
            when(mockInnerView1.getMeasuredWidth()).thenReturn(width1);
            when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

            int width2 = stepper.calculateStepDecoratorWidth(mockedStep2) * 3;
            when(mockInnerView2.getMeasuredWidth()).thenReturn(width2);
            when(mockContinueButton2.getMeasuredWidth()).thenReturn(0);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isNotEqualTo(width1 + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1))
                    .isEqualTo(width2 + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams2));
        }
    }
}
