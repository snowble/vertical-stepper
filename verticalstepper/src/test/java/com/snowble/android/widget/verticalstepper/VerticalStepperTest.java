package com.snowble.android.widget.verticalstepper;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;

import java.util.ArrayList;
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

    public static abstract class GivenTestStepper extends GivenAnActivity {
        TestStepper stepper;

        class TestStepper extends VerticalStepper {
            private boolean initStepsCalled;
            private int initStepCallCount;
            private int initTouchViewCallCount;
            private int initNavButtonsCallCount;
            private boolean doMeasurementCalled;
            private List<Rect> layoutTouchViewArgRects = new ArrayList<>();
            private List<Rect> layoutInnerViewArgRects = new ArrayList<>();
            private List<Rect> layoutNavButtonArgRects = new ArrayList<>();

            public TestStepper() {
                super(activity);
            }

            @Override
            void initSteps() {
                super.initSteps();
                initStepsCalled = true;
            }

            boolean wasInitStepsCalled() {
                return initStepsCalled;
            }

            @Override
            void initStep(Step step) {
                super.initStep(step);
                initStepCallCount++;
            }

            int getInitStepCallCount() {
                return initStepCallCount;
            }

            @Override
            void initTouchView(Step step) {
                initTouchViewCallCount++;
            }

            public int getInitTouchViewCallCount() {
                return initTouchViewCallCount;
            }

            @Override
            void initNavButtons(Step step) {
                initNavButtonsCallCount++;
            }

            public int getInitNavButtonsCallCount() {
                return initNavButtonsCallCount;
            }

            @Override
            void doMeasurement(int widthMeasureSpec, int heightMeasureSpec) {
                doMeasurementCalled = true;
            }

            boolean wasDoMeasurementCalled() {
                return doMeasurementCalled;
            }

            @Override
            void layoutTouchView(Rect rect, InternalTouchView touchView) {
                layoutTouchViewArgRects.add(new Rect(rect));
            }

            public List<Rect> getLayoutTouchViewArgRects() {
                return layoutTouchViewArgRects;
            }

            @Override
            void layoutInnerView(Rect rect, Step step) {
                layoutInnerViewArgRects.add(new Rect(rect));
            }

            public List<Rect> getLayoutInnerViewArgRects() {
                return layoutInnerViewArgRects;
            }

            @Override
            void layoutNavButtons(Rect rect, Step step) {
                layoutNavButtonArgRects.add(new Rect(rect));
            }

            public List<Rect> getLayoutNavButtonArgRects() {
                return layoutNavButtonArgRects;
            }
        }

        @Before
        public void givenTestStepper() {
            stepper = new TestStepper();
        }

    }

    public static class GivenEmptyTestStepper extends GivenTestStepper {
        @Test
        public void onAttachedToWindow_ShouldInitSteps() {
            stepper.onAttachedToWindow();

            assertThat(stepper.wasInitStepsCalled()).isTrue();
        }

        @Test
        public void initSteps_ShouldInitStepsAndChildViews() {
            View child1 = mock(View.class);
            View child2 = mock(View.class);
            VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
            when(lp.getTitle()).thenReturn("title");
            when(child1.getLayoutParams()).thenReturn(lp);
            when(child2.getLayoutParams()).thenReturn(lp);

            stepper.addView(child1);
            stepper.addView(child2);

            stepper.initSteps();

            assertThat(stepper.getInitStepCallCount()).isEqualTo(2);
            assertThat(stepper.getInitTouchViewCallCount()).isEqualTo(2);
            assertThat(stepper.getInitNavButtonsCallCount()).isEqualTo(2);
        }

        @SuppressLint("WrongCall") // Explicitly testing onMeasure
        @Test
        public void onMeasure_ShouldCallDoMeasurement() {
            stepper.onMeasure(0, 0);

            assertThat(stepper.wasDoMeasurementCalled()).isTrue();
        }

    }

    @SuppressLint("WrongCall") // Explicitly testing onLayout
    public static class GivenTestStepperWithTwoSteps extends GivenTestStepper {

        private MockedStep mockedStep1;
        private MockedStep mockedStep2;

        @Before
        public void givenTestStepperWithTwoSteps() {
            mockedStep1 = new MockedStep();
            mockedStep2 = new MockedStep();
            stepper.steps.add(mockedStep1.step);
            stepper.steps.add(mockedStep2.step);
        }

        @Test
        public void onLayout_NoActiveSteps_ShouldNotCallLayoutInnerViewOrLayoutNavButtons() {
            stepper.onLayout(true, 0, 0, 0, 0);

            assertThat(stepper.getLayoutTouchViewArgRects()).hasSize(2);

            assertThat(stepper.getLayoutInnerViewArgRects()).isEmpty();
            assertThat(stepper.getLayoutNavButtonArgRects()).isEmpty();
        }

        @Test
        public void onLayout_ActiveStep_ShouldCallLayoutInnerViewAndLayoutNavButtons() {
            when(mockedStep1.step.isActive()).thenReturn(true);

            stepper.onLayout(true, 0, 0, 0, 0);

            assertThat(stepper.getLayoutTouchViewArgRects()).hasSize(2);

            assertThat(stepper.getLayoutInnerViewArgRects()).hasSize(1);
            assertThat(stepper.getLayoutNavButtonArgRects()).hasSize(1);
        }

        @Test
        public void onLayout_ShouldAdjustTouchForPadding() {
            int leftPadding = 8;
            int topPadding = 20;
            int rightPadding = 4;
            int bottomPadding = 10;
            stepper.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int left = 0;
            int top = 0;
            int right = 400;
            int bottom = 200;

            stepper.onLayout(true, left, top, right, bottom);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).isNotEmpty();
            Rect touchRect = layoutTouchViewArgRects.get(0);

            assertThat(touchRect.left).isEqualTo(stepper.outerHorizontalPadding + leftPadding);
            assertThat(touchRect.top).isEqualTo(stepper.outerVerticalPadding + topPadding);
            assertThat(touchRect.right).isEqualTo(right - stepper.outerHorizontalPadding - rightPadding);
            assertThat(touchRect.bottom).isEqualTo(bottom - stepper.outerVerticalPadding - bottomPadding);
        }

        @Test
        public void onLayout_ActiveStep_ShouldAdjustInnerViewForPaddingAndStepDecorators() {
            when(mockedStep1.step.isActive()).thenReturn(true);
            int stepDecoratorIconWidth = 40;
            when(mockedStep1.step.calculateStepDecoratorIconWidth()).thenReturn(stepDecoratorIconWidth);
            int distanceToTextBottom = 80;
            when(mockedStep1.step.calculateYDistanceToTextBottom()).thenReturn(distanceToTextBottom);

            int leftPadding = 8;
            int topPadding = 20;
            int rightPadding = 4;
            int bottomPadding = 10;
            stepper.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int left = 0;
            int top = 0;
            int right = 400;
            int bottom = 200;

            stepper.onLayout(true, left, top, right, bottom);

            List<Rect> layoutInnerViewArgRects = stepper.getLayoutInnerViewArgRects();
            assertThat(layoutInnerViewArgRects).isNotEmpty();

            Rect innerViewLayoutRect = layoutInnerViewArgRects.get(0);
            assertThat(innerViewLayoutRect.left)
                    .isEqualTo(leftPadding + stepper.outerHorizontalPadding + stepDecoratorIconWidth);
            assertThat(innerViewLayoutRect.top)
                    .isEqualTo(stepper.outerVerticalPadding + topPadding + distanceToTextBottom);
            assertThat(innerViewLayoutRect.right)
                    .isEqualTo(right - left - stepper.outerHorizontalPadding - rightPadding);
            assertThat(innerViewLayoutRect.bottom)
                    .isEqualTo(bottom - top - stepper.outerVerticalPadding - bottomPadding);
        }

        @Test
        public void onLayout_ActiveStep_ShouldAdjustButtonsTopForInnerViewHeight() {
            when(mockedStep1.step.isActive()).thenReturn(true);
            int innerHeight = 400;
            when(mockedStep1.innerView.getHeight()).thenReturn(innerHeight);
            int topPadding = 20;
            stepper.setPadding(0, topPadding, 0, 0);

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutInnerViewArgRects = stepper.getLayoutInnerViewArgRects();
            List<Rect> layoutNavButtonArgRects = stepper.getLayoutNavButtonArgRects();
            assertThat(layoutInnerViewArgRects).isNotEmpty();
            assertThat(layoutNavButtonArgRects).isNotEmpty();

            int innerTop = layoutInnerViewArgRects.get(0).top;
            int buttonsTop = layoutNavButtonArgRects.get(0).top;

            assertThat(buttonsTop).isEqualTo(innerTop + innerHeight);
        }

        @Test
        public void onLayout_ShouldAdjustNextTopForPreviousStepHeight() {
            int distanceToNextStep = 400;
            when(mockedStep1.step.calculateYDistanceToNextStep()).thenReturn(distanceToNextStep);

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).hasSize(2);

            int firstStepTop = layoutTouchViewArgRects.get(0).top;
            int secondStepTop = layoutTouchViewArgRects.get(1).top;
            assertThat(secondStepTop).isEqualTo(firstStepTop + distanceToNextStep);
        }

        @Test
        public void onLayout_NonZeroLeft_ShouldAdjustForLeftOffset() {
            int left = 50;
            int right = 300;

            stepper.onLayout(true, left, 0, right, 0);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).isNotEmpty();

            Rect rect = layoutTouchViewArgRects.get(0);
            assertThat(rect.left).isEqualTo(stepper.outerHorizontalPadding);
            assertThat(rect.right).isEqualTo(right - left - stepper.outerHorizontalPadding);
        }

        @Test
        public void onLayout_NonZeroTop_ShouldAdjustForTopOffset() {
            int top = 50;
            int bottom = 300;

            stepper.onLayout(true, 0, top, 0, bottom);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).isNotEmpty();

            Rect rect = layoutTouchViewArgRects.get(0);
            assertThat(rect.top).isEqualTo(stepper.outerVerticalPadding);
            assertThat(rect.bottom).isEqualTo(bottom - top - stepper.outerVerticalPadding);
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
        public void initChildViews_ShouldHaveEmptyInnerViews() {
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

        void mockStep1Widths(int decoratorWidth, int innerUsedSpace, int innerWidth,
                             int continueUsedSpace, int continueWidth) {
            mockStepWidths(mockedStep1, decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);
        }

        void mockStepWidths(MockedStep mockedStep, int decoratorWidth,
                            int innerUsedSpace, int innerWidth,
                            int continueUsedSpace, int continueWidth) {
            when(mockedStep.step.calculateStepDecoratorWidth()).thenReturn(decoratorWidth);
            when(mockedStep.step.calculateHorizontalUsedSpace(mockedStep.innerView)).thenReturn(innerUsedSpace);
            when(mockedStep.innerView.getMeasuredWidth()).thenReturn(innerWidth);
            when(mockedStep.step.calculateHorizontalUsedSpace(mockedStep.continueButton)).thenReturn(continueUsedSpace);
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
            assertExpectedWidthMeasureSpec(maxWidth, innerWms, step.calculateHorizontalUsedSpace(step.getInnerView()));
            int innerHms = measureSpecs.get(1);
            assertExpectedHeightMeasureSpec(maxHeight, innerHms, additionalInnerVerticalUsedSpace);

            int continueWms = measureSpecs.get(2);
            assertExpectedWidthMeasureSpec(maxWidth, continueWms, step.calculateHorizontalUsedSpace(step.getContinueButton()));
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

        void measureChildViews(int maxWidth, int maxHeight) {
            int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
            int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
            stepper.measureChildViews(wms, hms);
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
        public void initChildViews_ShouldHaveInnerViewsWithSingleElement() {
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
        public void calculateWidth_ShouldReturnHorizontalPaddingAndStepWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = decoratorWidth * 4;
            int continueUsedSpace = 30;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);

            int width = stepper.calculateWidth();

            assertThat(width)
                    .isEqualTo(stepper.calculateHorizontalPadding()
                            + innerWidth + innerUsedSpace);
        }

        @Test
        public void calculateMaxStepWidth_DecoratorsHaveMaxWidth_ShouldReturnDecoratorsWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 10;
            int innerWidth = 0;
            int continueUsedSpace = 15;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(decoratorWidth);
        }

        @Test
        public void calculateMaxStepWidth_InnerViewHasMaxWidth_ShouldReturnInnerViewWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = decoratorWidth * 4;
            int continueUsedSpace = 15;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(innerWidth + innerUsedSpace);
        }

        @Test
        public void calculateMaxStepWidth_NavButtonsHaveMaxWidth_ShouldReturnNavButtonsWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = 0;
            int continueUsedSpace = 10;
            int continueWidth = decoratorWidth * 4;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

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
        public void measureChildViews_ShouldHaveChildrenVisibleHeightsWithActualHeight() {
            final int innerViewHeight = 100;
            final int buttonHeight = 50;
            when(mockedStep1.innerView.getMeasuredHeight()).thenReturn(innerViewHeight);
            when(mockedStep1.continueButton.getMeasuredHeight()).thenReturn(buttonHeight);

            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureChildViews(ms, ms);

            verify(mockedStep1.step).setChildrenVisibleHeight(innerViewHeight + buttonHeight);
        }

        @Test
        public void measureChildViews_ShouldMeasureNavButtonsAccountingForInnerView() {
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            int innerHeight = 200;
            when(mockedStep1.innerView.getMeasuredHeight()).thenReturn(innerHeight);
            int innerVerticalUsedSpace = 20;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.innerView)).thenReturn(innerVerticalUsedSpace);

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            int continueVerticalUsedSpace = 10;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.continueButton)).thenReturn(continueVerticalUsedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

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
        public void measureChildViews_NoMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            int innerVerticalUsedSpace = 20;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.innerView)).thenReturn(innerVerticalUsedSpace);

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            int continueVerticalUsedSpace = 10;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.continueButton)).thenReturn(continueVerticalUsedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUsedSpace, continueVerticalUsedSpace);
        }

        @Test
        public void measureChildViews_HasMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            VerticalStepper.LayoutParams innerLp = createTestLayoutParams(5, 10, 5, 10);
            when(mockedStep1.innerView.getLayoutParams()).thenReturn(innerLp);
            VerticalStepper.LayoutParams continueLp = createTestLayoutParams(10, 20, 10, 20);
            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(continueLp);

            int innerVerticalUsedSpace = 20;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.innerView)).thenReturn(innerVerticalUsedSpace);
            int continueVerticalUsedSpace = 10;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.continueButton)).thenReturn(continueVerticalUsedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUsedSpace, continueVerticalUsedSpace);
        }

        @Test
        public void measureChildViews_ShouldMeasureChildrenAccountingForDecorator() {
            int decoratorHeight = 100;
            when(mockedStep1.step.getDecoratorHeight()).thenReturn(decoratorHeight);

            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            int innerVerticalUsedSpace = 20;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.innerView)).thenReturn(innerVerticalUsedSpace);

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            int continueVerticalUsedSpace = 10;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.continueButton)).thenReturn(continueVerticalUsedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

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

        void mockStep2Widths(int decoratorWidth, int innerUsedSpace, int innerWidth,
                             int continueUsedSpace, int continueWidth) {
            mockStepWidths(mockedStep2, decoratorWidth, innerUsedSpace, innerWidth, continueUsedSpace, continueWidth);
        }
    }

    public static class GivenExactlyTwoSteps extends GivenTwoSteps {
        @Test
        public void initChildViews_ShouldHaveInnerViewsWithTwoElements() {
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
        public void measureChildViews_ShouldMeasureViews() {
            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureChildViews(ms, ms);

            verify(mockedStep1.innerView).measure(anyInt(), anyInt());
            verify(mockedStep2.innerView).measure(anyInt(), anyInt());
            verify(mockedStep1.continueButton).measure(anyInt(), anyInt());
            verify(mockedStep1.continueButton).measure(anyInt(), anyInt());
        }

        @Test
        public void measureChildViews_ShouldMeasureChildrenAccountingForBottomMargin() {
            int decoratorHeight = 100;
            int bottomMargin = 30;
            when(mockedStep1.step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep2.step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(mockedStep1.step.getBottomMarginHeight()).thenReturn(bottomMargin);
            when(mockedStep2.step.getBottomMarginHeight()).thenReturn(0);

            when(mockedStep1.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            when(mockedStep2.innerView.getLayoutParams()).thenReturn(createTestLayoutParams());
            int innerVerticalUsedSpace = 20;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.innerView)).thenReturn(innerVerticalUsedSpace);
            when(mockedStep2.step.calculateVerticalUsedSpace(mockedStep2.innerView)).thenReturn(innerVerticalUsedSpace);

            when(mockedStep1.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            when(mockedStep2.continueButton.getLayoutParams()).thenReturn(createTestLayoutParams());
            int continueVerticalUsedSpace = 10;
            when(mockedStep1.step.calculateVerticalUsedSpace(mockedStep1.continueButton)).thenReturn(continueVerticalUsedSpace);
            when(mockedStep2.step.calculateVerticalUsedSpace(mockedStep2.continueButton)).thenReturn(continueVerticalUsedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUsedSpace + decoratorHeight,
                    continueVerticalUsedSpace + decoratorHeight);

            assertExpectedStep2MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUsedSpace + decoratorHeight * 2 + bottomMargin,
                    continueVerticalUsedSpace + decoratorHeight * 2 + bottomMargin);
        }

        @Test
        public void calculateMaxStepWidth_ShouldReturnLargerStepWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int continueWidth = 0;
            int continueUsedSpace = 10;

            int inner1Width = decoratorWidth * 2;
            mockStep1Widths(decoratorWidth, innerUsedSpace, inner1Width, continueUsedSpace, continueWidth);

            int inner2Width = decoratorWidth * 3;
            mockStep2Widths(decoratorWidth, innerUsedSpace, inner2Width, continueUsedSpace, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isNotEqualTo(inner1Width + innerUsedSpace)
                    .isEqualTo(inner2Width + innerUsedSpace);
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
}
