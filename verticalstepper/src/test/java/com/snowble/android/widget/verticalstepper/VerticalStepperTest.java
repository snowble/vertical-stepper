package com.snowble.android.widget.verticalstepper;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class VerticalStepperTest {

    private static class MockedStep {
        View innerView;
        VerticalStepper.LayoutParams innerLayoutParams;
        VerticalStepper.InternalTouchView touchView;
        AppCompatButton continueButton;
        VerticalStepper.LayoutParams continueLayoutParams;
        Step step;

        MockedStep() {
            innerView = mock(View.class);
            innerLayoutParams = mock(VerticalStepper.LayoutParams.class);
            when(innerView.getLayoutParams()).thenReturn(innerLayoutParams);

            continueButton = mock(AppCompatButton.class);
            continueLayoutParams = mock(VerticalStepper.LayoutParams.class);
            when(continueButton.getLayoutParams()).thenReturn(continueLayoutParams);

            touchView = mock(VerticalStepper.InternalTouchView.class);

            step = mock(Step.class);
            when(step.getInnerView()).thenReturn(innerView);
            when(step.getTouchView()).thenReturn(touchView);
            when(step.getContinueButton()).thenReturn(continueButton);
        }
    }

    public abstract static class GivenAStepper extends GivenAnActivity {
        VerticalStepper stepper;

        @Before
        public void givenAStepper() {
            stepper = new VerticalStepper(activity);
        }
    }

    public static class GivenZeroSteps extends GivenAStepper {
        private int getColor(int colorRes) {
            return ResourcesCompat.getColor(activity.getResources(), colorRes, activity.getTheme());
        }

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

        @Test
        public void initSteps_ShouldHaveEmptyInnerViews() {
            stepper.initSteps();

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
        public void layoutTouchView_WhenNotEnoughSpace_ShouldClip() {
            int leftPadding = 20;
            int topPadding = 4;
            int rightPadding = 10;
            int bottomPadding = 2;
            stepper.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int left = 0;
            int top = 0;
            int right = 300;
            int bottom = 500;

            int adjustedLeft = left + stepper.outerHorizontalPadding + leftPadding;
            int adjustedTop = top + stepper.outerVerticalPadding + topPadding;
            int adjustedRight = right - stepper.outerHorizontalPadding - rightPadding;
            int adjustedBottom = bottom - stepper.outerVerticalPadding - bottomPadding;

            VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);
            when(touchView.getMeasuredHeight()).thenReturn(bottom * 2);

            stepper.layoutTouchView(new Rect(adjustedLeft, adjustedTop, adjustedRight, adjustedBottom), touchView);

            verify(touchView).layout(eq(left + leftPadding), eq(top + topPadding),
                    eq(right - left - rightPadding), eq(bottom - top - bottomPadding));
        }

        @Test
        public void layoutTouchView_WhenEnoughSpace_ShouldUseFullWidthAndMeasuredHeight() {
            int left = 0;
            int top = 0;
            int right = 300;
            int bottom = 500;

            int adjustedLeft = left + stepper.outerHorizontalPadding;
            int adjustedTop = top + stepper.outerVerticalPadding;
            int adjustedRight = right - stepper.outerHorizontalPadding;
            int adjustedBottom = bottom - stepper.outerVerticalPadding;

            VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);
            int touchMeasuredHeight = bottom / 2;
            when(touchView.getMeasuredHeight()).thenReturn(touchMeasuredHeight);

            stepper.layoutTouchView(new Rect(adjustedLeft, adjustedTop, adjustedRight, adjustedBottom), touchView);

            verify(touchView).layout(eq(left), eq(top), eq(right - left), eq(top + touchMeasuredHeight));
        }

        @Test
        public void layoutActiveView_WhenNotEnoughSpace_ShouldClip() {
            int left = 0;
            int top = 0;
            int right = 300;
            int bottom = 500;

            int leftMargin = 5;
            int topMargin = 20;
            int rightMargin = 10;
            int bottomMargin = 15;

            View activeView = mock(View.class);
            when(activeView.getMeasuredWidth()).thenReturn(right * 2);
            when(activeView.getMeasuredHeight()).thenReturn(bottom * 2);
            when(activeView.getLayoutParams()).thenReturn(
                    createTestLayoutParams(leftMargin, topMargin, rightMargin, bottomMargin));

            stepper.layoutActiveView(new Rect(left, top, right, bottom), activeView);

            verify(activeView).layout(eq(left + leftMargin), eq(top + topMargin),
                    eq(right - rightMargin), eq(bottom - bottomMargin));
        }

        @Test
        public void layoutActiveView_WhenEnoughSpace_ShouldUseFullWidthAndMeasuredHeight() {
            int left = 0;
            int top = 0;
            int right = 300;
            int bottom = 500;

            View activeView = mock(View.class);
            int measuredWidth = right / 2;
            when(activeView.getMeasuredWidth()).thenReturn(measuredWidth);
            int measuredHeight = bottom / 2;
            when(activeView.getMeasuredHeight()).thenReturn(measuredHeight);
            when(activeView.getLayoutParams()).thenReturn(mock(VerticalStepper.LayoutParams.class));

            stepper.layoutActiveView(new Rect(left, top, right, bottom), activeView);

            verify(activeView).layout(eq(left), eq(top), eq(left + measuredWidth), eq(top + measuredHeight));
        }
    }

    public abstract static class GivenOneStep extends GivenAStepper {
        MockedStep mockedStep1;

        @Before
        public void givenOneStep() {
            mockedStep1 = new MockedStep();

            stepper.initStep(mockedStep1.step);

            clearInvocations(mockedStep1.innerView);
            clearInvocations(mockedStep1.innerLayoutParams);
            clearInvocations(mockedStep1.continueButton);
            clearInvocations(mockedStep1.continueLayoutParams);
            clearInvocations(mockedStep1.touchView);
            clearInvocations(mockedStep1.step);
        }

        void mockStep1Widths(int decoratorWidth, int innerWidth,
                             int continueWidth) {
            mockStepWidths(mockedStep1, decoratorWidth, innerWidth, continueWidth);
        }

        void mockStepWidths(MockedStep mockedStep, int decoratorWidth,
                            int innerWidth, int continueWidth) {
            when(mockedStep.step.calculateStepDecoratorWidth()).thenReturn(decoratorWidth);
            when(mockedStep.innerView.getMeasuredWidth()).thenReturn(innerWidth);
            when(mockedStep.continueButton.getMeasuredWidth()).thenReturn(continueWidth);
        }

        void mockStepHeights(int decoratorHeight, int childrenVisibleHeight, int bottomMarginHeight, Step step) {
            when(step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(step.getChildrenVisibleHeight()).thenReturn(childrenVisibleHeight);
            when(step.getBottomMarginHeight()).thenReturn(bottomMarginHeight);
        }

        void assertExpectedStep1MeasureSpecs(int maxWidth, int maxHeight,
                                             int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep1MeasureSpecs(), mockedStep1.step, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        List<Integer> captureStep1MeasureSpecs() {
            return captureStepMeasureSpecs(mockedStep1.innerView, mockedStep1.continueButton);
        }

        List<Integer> captureStepMeasureSpecs(View innerView, View continueButton) {
            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(innerView).measure(captor.capture(), captor.capture());
            verify(continueButton).measure(captor.capture(), captor.capture());
            return captor.getAllValues();
        }

        void assertExpectedStepMeasureSpecs(List<Integer> measureSpecs, Step step,
                                            int maxWidth, int maxHeight,
                                            int additionalInnerVerticalUsedSpace,
                                            int additionalContinueVerticalUsedSpace) {
            int innerWms = measureSpecs.get(0);
            assertExpectedWidthMeasureSpec(maxWidth, innerWms,
                    stepper.calculateHorizontalUsedSpace(step.getInnerView()));
            int innerHms = measureSpecs.get(1);
            assertExpectedHeightMeasureSpec(maxHeight, innerHms, additionalInnerVerticalUsedSpace);

            int continueWms = measureSpecs.get(2);
            assertExpectedWidthMeasureSpec(maxWidth, continueWms,
                    stepper.calculateHorizontalUsedSpace(step.getContinueButton()));
            int continueHms = measureSpecs.get(3);
            assertExpectedHeightMeasureSpec(maxHeight, continueHms, additionalContinueVerticalUsedSpace);
        }

        void assertExpectedHeightMeasureSpec(int maxHeight, int heightMeasureSpec, int additionalUsedSpace) {
            int verticalUsedSpace =
                    stepper.calculateVerticalPadding() + additionalUsedSpace;
            assertThat(View.MeasureSpec.getSize(heightMeasureSpec))
                    .isEqualTo(maxHeight - verticalUsedSpace);
        }

        void assertExpectedWidthMeasureSpec(int maxWidth, int widthMeasureSpec, int additionalUsedSpace) {
            int horizontalUsedSpace =
                    stepper.calculateHorizontalPadding() + additionalUsedSpace;
            assertThat(View.MeasureSpec.getSize(widthMeasureSpec))
                    .isEqualTo(maxWidth - horizontalUsedSpace);
        }

        void measureActiveViews(int maxWidth, int maxHeight) {
            int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
            int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
            stepper.measureActiveViews(wms, hms);
        }

        void mockActiveState(boolean isActive) {
            when(mockedStep1.step.isActive()).thenReturn(isActive);
            int visibility = isActive ? View.VISIBLE : View.GONE;
            when(mockedStep1.innerView.getVisibility()).thenReturn(visibility);
            when(mockedStep1.continueButton.getVisibility()).thenReturn(visibility);
        }

        void assertActiveState(boolean expectedActiveState) {
            verify(mockedStep1.step).setActive(expectedActiveState);
            int visibility = expectedActiveState ? View.VISIBLE : View.GONE;
            when(mockedStep1.innerView.getVisibility()).thenReturn(visibility);
            when(mockedStep1.continueButton.getVisibility()).thenReturn(visibility);
        }
    }

    public static class GivenExactlyOneStep extends GivenOneStep {
        @Test
        public void initSteps_ShouldHaveInnerViewsWithSingleElement() {
            assertThat(stepper.steps)
                    .hasSize(1)
                    .doesNotContainNull();
        }

        @Test
        public void initInnerView_ShouldSetVisibilityToGone() {
            stepper.initStep(mockedStep1.step);

            verify(mockedStep1.innerView).setVisibility(View.GONE);
        }

        @Test
        public void initInnerView_ShouldInitializeStepViews() {
            assertThat(stepper.steps)
                    .hasSize(1)
                    .doesNotContainNull();

            Step step = stepper.steps.get(0);
            assertThat(step.getTouchView())
                    .isNotNull();
            assertThat(step.getContinueButton())
                    .isNotNull();
        }

        @Test
        public void initTouchView_ShouldSetClickListener() {
            stepper.initTouchView(mockedStep1.step);

            verify(mockedStep1.touchView).setOnClickListener((View.OnClickListener) notNull());
        }

        @Test
        public void initTouchView_ShouldAttachToStepper() {
            stepper.initTouchView(mockedStep1.step);

            assertThat(stepper.getChildCount()).isEqualTo(1);
        }

        @Test
        public void initNavButtons_ShouldSetVisibilityToGone() {
            stepper.initNavButtons(mockedStep1.step);

            verify(mockedStep1.continueButton).setVisibility(View.GONE);
        }

        @Test
        public void initNavButtons_ShouldSetClickListener() {
            stepper.initNavButtons(mockedStep1.step);

            verify(mockedStep1.continueButton).setOnClickListener((View.OnClickListener) notNull());
        }

        @Test
        public void initNavButtons_ShouldAttachToStepper() {
            stepper.initNavButtons(mockedStep1.step);

            assertThat(stepper.getChildCount()).isEqualTo(1);
        }

        @Test
        public void measureBottomMarginHeights_ShouldNotMeasureBottomMarginToNextStep() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1.step, never()).measureBottomMarginToNextStep();
        }

        @Test
        public void calculateHorizontalUsedSpace_ShouldReturnPaddingPlusIconLeftAdjustment() {
            int leftMargin = 20;
            int rightMargin = 10;
            mockedStep1.innerLayoutParams.leftMargin = leftMargin;
            mockedStep1.innerLayoutParams.rightMargin = rightMargin;

            int horizontalPadding = stepper.calculateHorizontalUsedSpace(mockedStep1.innerView);

            assertThat(horizontalPadding)
                    .isEqualTo(leftMargin + rightMargin
                            + stepper.getCommonStepValues().calculateStepDecoratorIconWidth());
        }

        @Test
        public void calculateVerticalUsedSpace_ShouldReturnAllMargins() {
            int topMargin = 10;
            int bottomMargin = 20;
            mockedStep1.innerLayoutParams.topMargin = topMargin;
            mockedStep1.innerLayoutParams.bottomMargin = bottomMargin;

            int verticalPadding = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);

            assertThat(verticalPadding).isEqualTo(topMargin + bottomMargin);
        }

        @Test
        public void calculateWidth_ShouldReturnHorizontalPaddingAndStepWidth() {
            int decoratorWidth = 20;
            int innerWidth = decoratorWidth * 4;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerWidth, continueWidth);

            int width = stepper.calculateWidth();

            int innerUsedSpace = stepper.calculateHorizontalUsedSpace(mockedStep1.innerView);
            assertThat(width)
                    .isEqualTo(stepper.calculateHorizontalPadding()
                            + innerWidth + innerUsedSpace);
        }

        @Test
        public void calculateMaxStepWidth_DecoratorsHaveMaxWidth_ShouldReturnDecoratorsWidth() {
            int decoratorWidth = stepper.getCommonStepValues().calculateStepDecoratorIconWidth() * 2;
            int innerWidth = 0;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(decoratorWidth);
        }

        @Test
        public void calculateMaxStepWidth_InnerViewHasMaxWidth_ShouldReturnInnerViewWidth() {
            int decoratorWidth = 20;
            int innerWidth = decoratorWidth * 4;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            int innerUsedSpace = stepper.calculateHorizontalUsedSpace(mockedStep1.innerView);
            assertThat(maxWidth)
                    .isEqualTo(innerWidth + innerUsedSpace);
        }

        @Test
        public void calculateMaxStepWidth_NavButtonsHaveMaxWidth_ShouldReturnNavButtonsWidth() {
            int decoratorWidth = 20;
            int innerWidth = 0;
            int continueWidth = decoratorWidth * 4;
            mockStep1Widths(decoratorWidth, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            int continueUsedSpace = stepper.calculateHorizontalUsedSpace(mockedStep1.continueButton);
            assertThat(maxWidth)
                    .isEqualTo(continueWidth + continueUsedSpace);
        }

        @Test
        public void calculateHeight_ShouldReturnVerticalPaddingPlusTotalStepHeight() {
            int decoratorHeight = 100;
            int childrenVisibleHeight = 400;
            int bottomMarginHeight = 48;
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep1.step);

            int width = stepper.calculateHeight();

            assertThat(width)
                    .isEqualTo(stepper.calculateVerticalPadding()
                            + decoratorHeight + childrenVisibleHeight + bottomMarginHeight);
        }

        @Test
        public void layoutActiveViews_ShouldNotModifyInputRect() {
            Rect rect = new Rect(1, 2, 3, 4);

            stepper.layoutActiveViews(rect, mockedStep1.step);

            assertThat(rect).isEqualTo(new Rect(1, 2, 3, 4));
        }

    }

    public static class GivenExactlyOneActiveStep extends GivenOneStep {

        @Before
        public void givenExactlyOneActiveStep() {
            mockActiveState(true);
        }

        @Test
        public void toggleStepExpandedState_ShouldBecomeInactiveAndCollapsed() {
            stepper.toggleStepExpandedState(mockedStep1.step);

            assertActiveState(false);
        }

        @Test
        public void measureActiveViews_ShouldHaveActiveViewsHeightsWithActualHeight() {
            final int innerViewHeight = 100;
            final int buttonHeight = 50;
            when(mockedStep1.innerView.getMeasuredHeight()).thenReturn(innerViewHeight);
            when(mockedStep1.continueButton.getMeasuredHeight()).thenReturn(buttonHeight);

            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureActiveViews(ms, ms);

            verify(mockedStep1.step).setActiveViewsHeight(innerViewHeight + buttonHeight);
        }

        @Test
        public void measureActiveViews_ShouldMeasureNavButtonsAccountingForInnerView() {
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            int innerHeight = 200;
            when(mockedStep1.innerView.getMeasuredHeight()).thenReturn(innerHeight);

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureActiveViews(maxWidth, maxHeight);

            int innerVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);
            int continueVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.continueButton);
            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUsedSpace,
                    innerHeight + innerVerticalUsedSpace + continueVerticalUsedSpace);
        }
    }

    public static class GivenExactlyOneInactiveStep extends GivenOneStep {

        @Before
        public void givenExactlyOneInactiveStep() {
            mockActiveState(false);
        }

        @Test
        public void toggleStepExpandedState_ShouldBecomeActiveAndExpanded() {
            stepper.toggleStepExpandedState(mockedStep1.step);

            assertActiveState(true);
        }

        @Test
        public void measureActiveViews_NoMargins_ShouldMeasureActiveViewsAccountingForUsedSpace() {
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureActiveViews(maxWidth, maxHeight);

            int innerVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);
            int continueVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.continueButton);
            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUsedSpace, continueVerticalUsedSpace);
        }

        @Test
        public void measureActiveViews_HasMargins_ShouldMeasureActiveViewsAccountingForUsedSpace() {
            VerticalStepper.LayoutParams innerLp = createTestLayoutParams(5, 10, 5, 10);
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(innerLp);
            VerticalStepper.LayoutParams continueLp = createTestLayoutParams(10, 20, 10, 20);
            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(continueLp);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureActiveViews(maxWidth, maxHeight);

            int innerVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);
            int continueVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.continueButton);
            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUsedSpace, continueVerticalUsedSpace);
        }

        @Test
        public void measureActiveViews_ShouldMeasureActiveViewsAccountingForDecorator() {
            int decoratorHeight = 100;
            when(mockedStep1.step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureActiveViews(maxWidth, maxHeight);

            int innerVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);
            int continueVerticalUsedSpace = stepper.calculateVerticalUsedSpace(mockedStep1.continueButton);
            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUsedSpace + decoratorHeight, continueVerticalUsedSpace + decoratorHeight);
        }
    }

    public static abstract class GivenTwoSteps extends GivenOneStep {
        MockedStep mockedStep2;

        @Before
        public void givenTwoSteps() {
            mockedStep2 = new MockedStep();

            stepper.initStep(mockedStep2.step);

            clearInvocations(mockedStep2.innerView);
            clearInvocations(mockedStep2.innerLayoutParams);
            clearInvocations(mockedStep2.continueButton);
            clearInvocations(mockedStep2.continueLayoutParams);
            clearInvocations(mockedStep2.touchView);
            clearInvocations(mockedStep2.step);
        }

        void assertExpectedStep2MeasureSpecs(int maxWidth, int maxHeight,
                                             int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep2MeasureSpecs(), mockedStep2.step, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        List<Integer> captureStep2MeasureSpecs() {
            return captureStepMeasureSpecs(mockedStep2.innerView, mockedStep2.continueButton);
        }

        void mockStep2Widths(int decoratorWidth, int innerWidth,
                             int continueWidth) {
            mockStepWidths(mockedStep2, decoratorWidth, innerWidth, continueWidth);
        }
    }

    public static class GivenExactlyTwoSteps extends GivenTwoSteps {
        @Test
        public void initSteps_ShouldHaveInnerViewsWithTwoElements() {
            assertThat(stepper.steps)
                    .hasSize(2)
                    .doesNotContainNull();
        }

        @Test
        public void measureStepDecoratorHeights_ShouldMeasureStepDecoratorHeightTwice() {
            stepper.measureStepDecoratorHeights();

            verify(mockedStep1.step).measureStepDecoratorHeight();
            verify(mockedStep2.step).measureStepDecoratorHeight();
        }

        @Test
        public void measureBottomMarginHeights_ShouldMeasureBottomMarginToNextStepOnce() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1.step).measureBottomMarginToNextStep();
        }

        @Test
        public void measureActiveViews_ShouldMeasureViews() {
            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureActiveViews(ms, ms);

            verify(mockedStep1.innerView).measure(anyInt(), anyInt());
            verify(mockedStep2.innerView).measure(anyInt(), anyInt());
            verify(mockedStep1.continueButton).measure(anyInt(), anyInt());
            verify(mockedStep1.continueButton).measure(anyInt(), anyInt());
        }

        @Test
        public void measureActiveViews_ShouldMeasureActiveViewsAccountingForBottomMargin() {
            int decoratorHeight = 100;
            when(mockedStep1.step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep2.step.getDecoratorHeight()).thenReturn(decoratorHeight);

            int bottomMargin = 30;
            when(mockedStep1.step.getBottomMarginHeight()).thenReturn(bottomMargin);
            when(mockedStep2.step.getBottomMarginHeight()).thenReturn(0);

            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            when(mockedStep2.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            when(mockedStep2.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureActiveViews(maxWidth, maxHeight);

            int innerVerticalUsedSpace1 = stepper.calculateVerticalUsedSpace(mockedStep1.innerView);
            int continueVerticalUsedSpace1 = stepper.calculateVerticalUsedSpace(mockedStep1.continueButton);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUsedSpace1 + decoratorHeight,
                    continueVerticalUsedSpace1 + decoratorHeight);

            int innerVerticalUsedSpace2 = stepper.calculateVerticalUsedSpace(mockedStep2.innerView);
            int continueVerticalUsedSpace2 = stepper.calculateVerticalUsedSpace(mockedStep2.continueButton);

            assertExpectedStep2MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUsedSpace2 + decoratorHeight * 2 + bottomMargin,
                    continueVerticalUsedSpace2 + decoratorHeight * 2 + bottomMargin);
        }

        @Test
        public void calculateMaxStepWidth_ShouldReturnLargerStepWidth() {
            int decoratorWidth = 20;
            int continueWidth = 0;

            int inner1Width = decoratorWidth * 2;
            mockStep1Widths(decoratorWidth, inner1Width, continueWidth);

            int inner2Width = decoratorWidth * 3;
            mockStep2Widths(decoratorWidth, inner2Width, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            int innerVerticalUsedSpace1 = stepper.calculateHorizontalUsedSpace(mockedStep1.innerView);
            int innerVerticalUsedSpace2 = stepper.calculateHorizontalUsedSpace(mockedStep2.innerView);

            assertThat(maxWidth)
                    .isNotEqualTo(inner1Width + innerVerticalUsedSpace1)
                    .isEqualTo(inner2Width + innerVerticalUsedSpace2);
        }

        @Test
        public void calculateHeight_ShouldReturnVerticalPaddingPlusTotalStepHeight() {
            int decoratorHeight = 100;
            int childrenVisibleHeight = 400;
            int bottomMarginHeight = 48;
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep1.step);
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep2.step);

            int height = stepper.calculateHeight();

            assertThat(height)
                    .isEqualTo(stepper.calculateVerticalPadding()
                            + (2 * (decoratorHeight + childrenVisibleHeight + bottomMarginHeight)));
        }

        @Test
        public void measureTouchViews_ShouldMeasureAllWidthsAndHeightsExactly() {
            int width = 20;
            int height = 80;

            stepper.measureTouchViews(width, height);

            ArgumentCaptor<Integer> wmsCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> hmsCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(mockedStep1.touchView).measure(wmsCaptor.capture(), hmsCaptor.capture());
            verify(mockedStep2.touchView).measure(wmsCaptor.capture(), hmsCaptor.capture());

            for (int actualWms : wmsCaptor.getAllValues()) {
                assertThat(View.MeasureSpec.getMode(actualWms)).isEqualTo(View.MeasureSpec.EXACTLY);
                assertThat(View.MeasureSpec.getSize(actualWms)).isEqualTo(width);
            }

            for (int actualHms : hmsCaptor.getAllValues()) {
                assertThat(View.MeasureSpec.getMode(actualHms)).isEqualTo(View.MeasureSpec.EXACTLY);
                assertThat(View.MeasureSpec.getSize(actualHms)).isEqualTo(height);
            }
        }
    }

    public static abstract class GivenStepperSpy extends GivenAStepper {
        VerticalStepper stepperSpy;

        @Before
        public void givenStepperSpy() {
            stepperSpy = spy(stepper);
        }
    }

    public static class GivenEmptyStepperSpy extends GivenStepperSpy {
        @Test
        public void onAttachedToWindow_ShouldInitSteps() {
            doNothing().when(stepperSpy).initSteps();

            stepperSpy.onAttachedToWindow();

            verify(stepperSpy).initSteps();
        }

        @Test
        public void initSteps_ShouldInitStepsAndChildViews() {
            View child1 = mock(View.class);
            View child2 = mock(View.class);
            VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
            when(lp.getTitle()).thenReturn("title");
            when(child1.getLayoutParams()).thenReturn(lp);
            when(child2.getLayoutParams()).thenReturn(lp);

            // For some reason, calling addView() doesn't update the children properly with the stepperSpy.
            // So explicitly set child count and children
            doReturn(2).when(stepperSpy).getChildCount();
            doReturn(child1).when(stepperSpy).getChildAt(0);
            doReturn(child2).when(stepperSpy).getChildAt(1);

            doNothing().when(stepperSpy).initTouchView(any(Step.class));
            doNothing().when(stepperSpy).initNavButtons(any(Step.class));

            stepperSpy.initSteps();

            verify(stepperSpy, times(2)).initStep(any(Step.class));
            verify(stepperSpy, times(2)).initTouchView(any(Step.class));
            verify(stepperSpy, times(2)).initNavButtons(any(Step.class));
        }

        @SuppressLint("WrongCall") // Explicitly testing onMeasure
        @Test
        public void onMeasure_ShouldCallDoMeasurement() {
            doNothing().when(stepperSpy).doMeasurement(anyInt(), anyInt());

            stepperSpy.onMeasure(0, 0);

            verify(stepperSpy).doMeasurement(eq(0), eq(0));
        }

        @SuppressLint("WrongCall") // Explicitly testing onDraw
        @Test
        public void onDraw_ShouldCallDoDraw() {
            Canvas canvas = mock(Canvas.class);
            doNothing().when(stepperSpy).doDraw(same(canvas));

            stepperSpy.onDraw(canvas);

            verify(stepperSpy).doDraw(canvas);
        }
    }

    public static abstract class GivenStepperSpyWithTwoSteps extends GivenStepperSpy {
        MockedStep mockedStep1;
        MockedStep mockedStep2;

        @Before
        public void givenStepperSpyWithTwoSteps() {
            mockedStep1 = new MockedStep();
            mockedStep2 = new MockedStep();
            stepperSpy.steps.add(mockedStep1.step);
            stepperSpy.steps.add(mockedStep2.step);
        }
    }

    public static abstract class GivenStepperSpyWithTwoStepsAndStubbedLayoutMethods extends GivenStepperSpyWithTwoSteps {
        static class CaptureRectAnswer implements Answer<Void> {
            private final Rect rectToCaptureArg;

            CaptureRectAnswer(Rect rectToCaptureArg) {
                this.rectToCaptureArg = rectToCaptureArg;
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = invocation.getArgument(0);
                rectToCaptureArg.set(rect);
                return null;
            }
        }

        @Before
        public void givenStepperSpyWithTwoStepsAndStubbedLayoutMethods() {
            doNothing().when(stepperSpy).layoutTouchView(any(Rect.class), any(VerticalStepper.InternalTouchView.class));
            doNothing().when(stepperSpy).layoutInnerView(any(Rect.class), any(Step.class));
            doNothing().when(stepperSpy).layoutNavButtons(any(Rect.class), any(Step.class));
        }
    }

    @SuppressLint("WrongCall") // Explicitly testing onLayout
    public static class GivenStepperSpyWithTwoInactiveStepsAndStubbedLayoutMethods
            extends GivenStepperSpyWithTwoStepsAndStubbedLayoutMethods {
        @Before
        public void givenStepperSpyWithTwoInactiveStepsAndStubbedLayoutMethods() {
            when(mockedStep1.step.isActive()).thenReturn(false);
            when(mockedStep2.step.isActive()).thenReturn(false);
        }

        @Test
        public void onLayout_ShouldNotCallLayoutInnerViewOrLayoutNavButtons() {
            stepperSpy.onLayout(true, 0, 0, 0, 0);

            verify(stepperSpy, times(2)).layoutTouchView(any(Rect.class), any(VerticalStepper.InternalTouchView.class));

            verify(stepperSpy, never()).layoutInnerView(any(Rect.class), any(Step.class));
            verify(stepperSpy, never()).layoutNavButtons(any(Rect.class), any(Step.class));
        }

        @Test
        public void onLayout_ShouldAdjustTouchForPadding() {
            int leftPadding = 8;
            int topPadding = 20;
            int rightPadding = 4;
            int bottomPadding = 10;
            stepperSpy.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int left = 0;
            int top = 0;
            int right = 400;
            int bottom = 200;

            stepperSpy.onLayout(true, left, top, right, bottom);

            ArgumentCaptor<Rect> rectCaptor = ArgumentCaptor.forClass(Rect.class);
            verify(stepperSpy).layoutTouchView(rectCaptor.capture(), same(mockedStep1.touchView));
            Rect touchRect = rectCaptor.getValue();

            assertThat(touchRect.left).isEqualTo(stepperSpy.outerHorizontalPadding + leftPadding);
            assertThat(touchRect.top).isEqualTo(stepperSpy.outerVerticalPadding + topPadding);
            assertThat(touchRect.right).isEqualTo(right - stepperSpy.outerHorizontalPadding - rightPadding);
            assertThat(touchRect.bottom).isEqualTo(bottom - stepperSpy.outerVerticalPadding - bottomPadding);
        }

        @Test
        public void onLayout_ShouldAdjustNextTopForPreviousStepHeight() {
            InOrder order = inOrder(stepperSpy);
            int distanceToNextStep = 400;
            when(mockedStep1.step.calculateYDistanceToNextStep()).thenReturn(distanceToNextStep);

            final Rect firstRect = new Rect();
            final Rect secondRect = new Rect();
            doAnswer(new CaptureRectAnswer(firstRect))
                    .when(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));
            doAnswer(new CaptureRectAnswer(secondRect))
                    .when(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep2.touchView));

            stepperSpy.onLayout(true, 0, 0, 0, 0);

            order.verify(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));
            order.verify(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep2.touchView));

            int firstStepTop = firstRect.top;
            int secondStepTop = secondRect.top;
            assertThat(secondStepTop).isEqualTo(firstStepTop + distanceToNextStep);
        }

        @Test
        public void onLayout_NonZeroLeft_ShouldAdjustForLeftOffset() {
            int left = 50;
            int right = 300;

            final Rect touchRect = new Rect();
            doAnswer(new CaptureRectAnswer(touchRect))
                    .when(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));

            stepperSpy.onLayout(true, left, 0, right, 0);

            verify(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));

            assertThat(touchRect.left).isEqualTo(stepperSpy.outerHorizontalPadding);
            assertThat(touchRect.right).isEqualTo(right - left - stepperSpy.outerHorizontalPadding);
        }

        @Test
        public void onLayout_NonZeroTop_ShouldAdjustForTopOffset() {
            int top = 50;
            int bottom = 300;

            final Rect touchRect = new Rect();
            doAnswer(new CaptureRectAnswer(touchRect))
                    .when(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));

            stepperSpy.onLayout(true, 0, top, 0, bottom);

            verify(stepperSpy).layoutTouchView(any(Rect.class), same(mockedStep1.touchView));

            assertThat(touchRect.top).isEqualTo(stepperSpy.outerVerticalPadding);
            assertThat(touchRect.bottom).isEqualTo(bottom - top - stepperSpy.outerVerticalPadding);
        }
    }

    @SuppressLint("WrongCall") // Explicitly testing onLayout
    public static class GivenStepperSpyWithTwoStepsOneActiveAndStubbedLayoutMethods
            extends GivenStepperSpyWithTwoStepsAndStubbedLayoutMethods {
        @Before
        public void givenStepperSpyWithTwoStepsOneActiveAndStubbedLayoutMethods() {
            when(mockedStep1.step.isActive()).thenReturn(true);
            when(mockedStep2.step.isActive()).thenReturn(false);
        }

        @Test
        public void onLayout_ShouldCallLayoutInnerViewAndLayoutNavButtons() {
            stepperSpy.onLayout(true, 0, 0, 0, 0);

            verify(stepperSpy, times(2)).layoutTouchView(any(Rect.class), any(VerticalStepper.InternalTouchView.class));

            verify(stepperSpy).layoutInnerView(any(Rect.class), any(Step.class));
            verify(stepperSpy).layoutNavButtons(any(Rect.class), any(Step.class));
        }

        @Test
        public void onLayout_ShouldAdjustInnerViewForPaddingAndStepDecorators() {
            when(mockedStep1.step.isActive()).thenReturn(true);
            int distanceToTextBottom = 80;
            when(mockedStep1.step.calculateYDistanceToTextBottom()).thenReturn(distanceToTextBottom);

            int leftPadding = 8;
            int topPadding = 20;
            int rightPadding = 4;
            int bottomPadding = 10;
            stepperSpy.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            final Rect innerRect = new Rect();
            doAnswer(new CaptureRectAnswer(innerRect))
                    .when(stepperSpy).layoutInnerView(any(Rect.class), same(mockedStep1.step));

            int left = 0;
            int top = 0;
            int right = 400;
            int bottom = 200;

            stepperSpy.onLayout(true, left, top, right, bottom);

            verify(stepperSpy).layoutInnerView(any(Rect.class), same(mockedStep1.step));

            assertThat(innerRect.left)
                    .isEqualTo(leftPadding + stepperSpy.outerHorizontalPadding
                            + stepperSpy.getCommonStepValues().calculateStepDecoratorIconWidth());
            assertThat(innerRect.top)
                    .isEqualTo(stepperSpy.outerVerticalPadding + topPadding + distanceToTextBottom);
            assertThat(innerRect.right)
                    .isEqualTo(right - left - stepperSpy.outerHorizontalPadding - rightPadding);
            assertThat(innerRect.bottom)
                    .isEqualTo(bottom - top - stepperSpy.outerVerticalPadding - bottomPadding);
        }

        @Test
        public void onLayout_ShouldAdjustButtonsTopForInnerViewHeight() {
            InOrder order = inOrder(stepperSpy);
            when(mockedStep1.step.isActive()).thenReturn(true);
            int innerHeight = 400;
            when(mockedStep1.innerView.getHeight()).thenReturn(innerHeight);

            final Rect innerRect = new Rect();
            final Rect navRect = new Rect();
            doAnswer(new CaptureRectAnswer(innerRect))
                    .when(stepperSpy).layoutInnerView(any(Rect.class), same(mockedStep1.step));
            doAnswer(new CaptureRectAnswer(navRect))
                    .when(stepperSpy).layoutNavButtons(any(Rect.class), same(mockedStep1.step));

            stepperSpy.onLayout(true, 0, 0, 0, 0);

            order.verify(stepperSpy).layoutInnerView(any(Rect.class), same(mockedStep1.step));
            order.verify(stepperSpy).layoutNavButtons(any(Rect.class), same(mockedStep1.step));

            int innerTop = innerRect.top;
            int buttonsTop = navRect.top;

            assertThat(buttonsTop).isEqualTo(innerTop + innerHeight);
        }
    }

    public static class GivenStepperSpyWithTwoStepsAndStubbedLayoutActiveViewMethod
            extends GivenStepperSpyWithTwoSteps {
        private Rect rect;

        @Before
        public void givenStepperSpyWithTwoStepsAndStubbedLayoutActiveViewMethod() {
            rect = mock(Rect.class);
            doNothing().when(stepperSpy).layoutActiveView(same(rect), any(View.class));
        }

        @Test
        public void layoutInnerView_ShouldCallLayoutActiveViewWithInnerView() {
            stepperSpy.layoutInnerView(rect, mockedStep1.step);

            verify(stepperSpy).layoutActiveView(rect, mockedStep1.innerView);
        }

        @Test
        public void layoutNavButtons_ShouldCallLayoutActiveViewWithContinueButton() {
            stepperSpy.layoutNavButtons(rect, mockedStep1.step);

            verify(stepperSpy).layoutActiveView(rect, mockedStep1.continueButton);
        }
    }

    public static class GivenStepperSpyWithTwoStepsAndStubbedDrawMethods extends GivenStepperSpyWithTwoSteps {
        private Canvas canvas;

        @Before
        public void givenStepperSpyWithTwoStepsAndStubbedDrawMethods() {
            canvas = mock(Canvas.class);

            doNothing().when(stepperSpy).drawIcon(same(canvas), any(Step.class), anyInt());
            doNothing().when(stepperSpy).drawText(same(canvas), any(Step.class));
            doNothing().when(stepperSpy).drawConnector(same(canvas), anyInt());
        }

        @Test
        public void doDraw_ShouldCallDrawIconTwice() {
            InOrder order = inOrder(stepperSpy);

            stepperSpy.doDraw(canvas);

            order.verify(stepperSpy).drawIcon(canvas, mockedStep1.step, 1);
            order.verify(stepperSpy).drawIcon(canvas, mockedStep2.step, 2);
        }

        @Test
        public void doDraw_ShouldCallDrawTextTwice() {
            InOrder order = inOrder(stepperSpy);

            stepperSpy.doDraw(canvas);

            order.verify(stepperSpy).drawText(canvas, mockedStep1.step);
            order.verify(stepperSpy).drawText(canvas, mockedStep2.step);
        }

        @Test
        public void doDraw_ShouldCallDrawConnectorOnce() {
            int distanceToNextStep = 300;
            when(mockedStep1.step.calculateYDistanceToNextStep()).thenReturn(distanceToNextStep);

            stepperSpy.doDraw(canvas);

            verify(stepperSpy).drawConnector(canvas, distanceToNextStep);
        }

        @Test
        public void doDraw_ShouldTranslateByDistanceToNextStep() {
            InOrder order = inOrder(canvas);

            int leftPadding = 10;
            int topPadding = 20;
            int rightPadding = 5;
            int bottomPadding = 15;
            stepperSpy.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int distanceToNextStep = 300;
            when(mockedStep1.step.calculateYDistanceToNextStep()).thenReturn(distanceToNextStep);

            stepperSpy.doDraw(canvas);

            // first translate for the left and top padding
            order.verify(canvas).translate(stepperSpy.outerHorizontalPadding + leftPadding,
                    stepperSpy.outerVerticalPadding + topPadding);

            // translate for the first step
            order.verify(canvas).translate(0, 0);

            // translate for the second step
            order.verify(canvas).translate(0, distanceToNextStep);

            // finally translate for the right and bottom padding
            order.verify(canvas).translate(stepperSpy.outerHorizontalPadding + rightPadding,
                    stepperSpy.outerVerticalPadding + bottomPadding);
        }

        @Test
        public void doDraw_ShouldSaveAndRestoreForEachChild() {
            InOrder order = inOrder(canvas);

            stepperSpy.doDraw(canvas);

            // for all of doDraw
            order.verify(canvas).save();

            // first step
            order.verify(canvas).save();
            order.verify(canvas).restore();

            // second step
            order.verify(canvas).save();
            order.verify(canvas).restore();

            // for all of doDraw
            order.verify(canvas).restore();
        }
    }

    public static class GivenStepperSpyWithTwoStepsAndStubbedDrawIconMethods extends GivenStepperSpyWithTwoSteps {
        private Canvas canvas;

        @Before
        public void givenStepperSpyWithTwoStepsAndStubbedDrawIconMethods() {
            canvas = mock(Canvas.class);

            doNothing().when(stepperSpy).drawIconBackground(same(canvas), any(Step.class));
            doNothing().when(stepperSpy).drawIconText(same(canvas), anyInt());
        }

        @Test
        public void drawIcon_ShouldCallDrawIconBackgroundAndDrawIconText() {
            stepperSpy.drawIcon(canvas, mockedStep1.step, 1);

            verify(stepperSpy).drawIconBackground(canvas, mockedStep1.step);
            verify(stepperSpy).drawIconText(canvas, 1);
        }

        @Test
        public void drawIcon_ShouldCallSaveAndRestore() {
            stepperSpy.drawIcon(canvas, mockedStep1.step, 1);

            verify(canvas).save();
            verify(canvas).restore();
        }
    }

    public static class GivenStepperSpyWithExactlyTwoSteps extends GivenStepperSpyWithTwoSteps {
        private Canvas canvas;

        @Before
        public void givenStepperSpyWithExactlyTwoSteps() {
            canvas = mock(Canvas.class);
        }

        @Test
        public void drawIconBackground_ShouldDrawCircleWithIconColor() {
            Paint color = mock(Paint.class);
            when(mockedStep1.step.getIconColor()).thenReturn(color);

            stepperSpy.drawIconBackground(canvas, mockedStep1.step);

            verify(canvas).drawArc(any(RectF.class), eq(0f), eq(360f), eq(true), same(color));
        }
    }
}
