package com.snowble.android.widget.verticalstepper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class VerticalStepperTest {

    @RunWith(RobolectricTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
    public abstract static class GivenAnActivity {
        Activity activity;

        @Before
        public void givenAnActivity() {
            ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
            activity = activityController.create().get();
        }
    }

    public static class GivenTestStepper extends GivenAnActivity {
        TestStepper stepper;

        private class TestStepper extends VerticalStepper {
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
            void layoutTouchView(int left, int top, int right, int bottom, InternalTouchView touchView) {
                layoutTouchViewArgRects.add(new Rect(left, top, right, bottom));
            }

            public List<Rect> getLayoutTouchViewArgRects() {
                return layoutTouchViewArgRects;
            }

            @Override
            void layoutInnerView(int left, int top, int right, int bottom, Step step) {
                layoutInnerViewArgRects.add(new Rect(left, top, right, bottom));
            }

            public List<Rect> getLayoutInnerViewArgRects() {
                return layoutInnerViewArgRects;
            }

            @Override
            void layoutNavButtons(int left, int top, int right, int bottom, Step step) {
                layoutNavButtonArgRects.add(new Rect(left, top, right, bottom));
            }

            public List<Rect> getLayoutNavButtonArgRects() {
                return layoutNavButtonArgRects;
            }
        }

        @Before
        public void givenTestStepper() {
            stepper = new TestStepper();
        }

        private void initTwoSteps() {
            addTwoChildViews();
            stepper.initSteps();
        }

        private void addTwoChildViews() {
            View child1 = mock(View.class);
            View child2 = mock(View.class);
            VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
            when(lp.getTitle()).thenReturn("title");
            when(child1.getLayoutParams()).thenReturn(lp);
            when(child2.getLayoutParams()).thenReturn(lp);

            stepper.addView(child1);
            stepper.addView(child2);
        }

        @Test
        public void onAttachedToWindow_ShouldInitSteps() {
            stepper.onAttachedToWindow();

            assertThat(stepper.wasInitStepsCalled()).isTrue();
        }

        @Test
        public void initSteps_ShouldInitStepsAndChildViews() {
            addTwoChildViews();

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

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_NoActiveSteps_ShouldNotCallLayoutInnerViewOrLayoutNavButtons() {
            initTwoSteps();

            stepper.onLayout(true, 0, 0, 0, 0);

            assertThat(stepper.getLayoutTouchViewArgRects()).hasSize(2);

            assertThat(stepper.getLayoutInnerViewArgRects()).isEmpty();
            assertThat(stepper.getLayoutNavButtonArgRects()).isEmpty();
        }

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_ActiveStep_ShouldCallLayoutInnerViewAndLayoutNavButtons() {
            initTwoSteps();
            stepper.steps.get(0).setActive(true);

            stepper.onLayout(true, 0, 0, 0, 0);

            assertThat(stepper.getLayoutTouchViewArgRects()).hasSize(2);

            assertThat(stepper.getLayoutInnerViewArgRects()).hasSize(1);
            assertThat(stepper.getLayoutNavButtonArgRects()).hasSize(1);
        }

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_ShouldAdjustTouchTopForPadding() {
            initTwoSteps();
            int topPadding = 20;
            stepper.setPadding(0, topPadding, 0, 0);

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).isNotEmpty();

            int touchTop = layoutTouchViewArgRects.get(0).top;
            assertThat(touchTop).isEqualTo(stepper.outerVerticalPadding + topPadding);
        }

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_ActiveStep_ShouldAdjustInnerTopForPaddingAndStepDecorators() {
            initTwoSteps();
            Step step = stepper.steps.get(0);
            step.setActive(true);
            int topPadding = 20;
            stepper.setPadding(0, topPadding, 0, 0);

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutInnerViewArgRects = stepper.getLayoutInnerViewArgRects();
            assertThat(layoutInnerViewArgRects).isNotEmpty();

            Rect innerViewLayoutRect = layoutInnerViewArgRects.get(0);
            int innerTop = innerViewLayoutRect.top;
            assertThat(innerTop).isEqualTo(stepper.outerVerticalPadding + topPadding
                    + step.calculateYDistanceToTextBottom());
            int innerLeft = innerViewLayoutRect.left;
            assertThat(innerLeft).isEqualTo(step.calculateStepDecoratorIconWidth());
        }

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_ActiveStep_ShouldAdjustButtonsTopForInnerViewHeight() {
            initTwoSteps();
            stepper.steps.get(0).setActive(true);
            int topPadding = 20;
            stepper.setPadding(0, topPadding, 0, 0);

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            List<Rect> layoutNavButtonArgRects = stepper.getLayoutNavButtonArgRects();
            assertThat(layoutTouchViewArgRects).isNotEmpty();
            assertThat(layoutNavButtonArgRects).isNotEmpty();

            int stepTop = layoutTouchViewArgRects.get(0).top;
            int buttonsTop = layoutNavButtonArgRects.get(0).top;

            assertThat(buttonsTop).isEqualTo(stepTop + stepper.steps.get(0).calculateYDistanceToButtons());
        }

        @SuppressLint("WrongCall") // Explicitly testing onLayout
        @Test
        public void onLayout_ShouldAdjustNextTopForPreviousStepHeight() {
            initTwoSteps();

            stepper.onLayout(true, 0, 0, 0, 0);

            List<Rect> layoutTouchViewArgRects = stepper.getLayoutTouchViewArgRects();
            assertThat(layoutTouchViewArgRects).hasSize(2);

            int firstStepTop = layoutTouchViewArgRects.get(0).top;
            int secondStepTop = layoutTouchViewArgRects.get(1).top;
            assertThat(secondStepTop).isEqualTo(firstStepTop + stepper.steps.get(0).calculateYDistanceToNextStep());
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
        public void layoutTouchView_WhenNotEnoughSpace_ShouldClipToPadding() {
            int leftPadding = 20;
            int topPadding = 4;
            int rightPadding = 10;
            int bottomPadding = 2;
            stepper.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

            int left = 0;
            int top = stepper.outerVerticalPadding + topPadding;
            int right = 300;
            int bottom = 500;

            VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);
            when(touchView.getMeasuredHeight()).thenReturn((bottom - top) * 2);

            stepper.layoutTouchView(left, top, right, bottom, touchView);

            verify(touchView).layout(eq(leftPadding), eq(top - stepper.outerVerticalPadding),
                    eq(right - left - rightPadding), eq(bottom - bottomPadding));
        }

        @Test
        public void layoutTouchView_WhenEnoughSpace_ShouldUseFullWidthAndMeasuredHeight() {
            int left = 0;
            int top = stepper.outerVerticalPadding;
            int right = 300;
            int bottom = 500;

            VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);
            int touchMeasuredHeight = (bottom - top) / 2;
            when(touchView.getMeasuredHeight()).thenReturn(touchMeasuredHeight);

            stepper.layoutTouchView(left, top, right, bottom, touchView);

            int expectedTop = top - stepper.outerVerticalPadding;
            verify(touchView).layout(eq(0), eq(expectedTop), eq(right - left), eq(expectedTop + touchMeasuredHeight));
        }

        @Test
        public void layoutTouchView_NonZeroLeft_ShouldAdjustForLeftOffset() {
            int left = 50;
            int right = 300;

            VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);

            stepper.layoutTouchView(left, 0, right, 0, touchView);

            verify(touchView).layout(eq(0), anyInt(), eq(right - left), anyInt());
        }
    }

    public abstract static class GivenOneStep extends GivenAStepper {
        View mockInnerView1;
        VerticalStepper.InternalTouchView mockTouchView1;
        AppCompatButton mockContinueButton1;
        VerticalStepper.LayoutParams mockLayoutParams1;
        Step mockedStep1;

        @Before
        public void givenOneStep() {
            mockInnerView1 = mock(View.class);
            mockLayoutParams1 = mock(VerticalStepper.LayoutParams.class);
            when(mockInnerView1.getLayoutParams()).thenReturn(mockLayoutParams1);
            mockContinueButton1 = mock(AppCompatButton.class);
            mockTouchView1 = mock(VerticalStepper.InternalTouchView.class);
            mockedStep1 = mock(Step.class);
            when(mockedStep1.getInnerView()).thenReturn(mockInnerView1);
            when(mockedStep1.getTouchView()).thenReturn(mockTouchView1);
            when(mockedStep1.getContinueButton()).thenReturn(mockContinueButton1);

            stepper.initStep(mockedStep1);

            clearInvocations(mockInnerView1);
            clearInvocations(mockLayoutParams1);
            clearInvocations(mockContinueButton1);
            clearInvocations(mockTouchView1);
            clearInvocations(mockedStep1);
        }

        void mockStep1Widths(int decoratorWidth, int innerUsedSpace, int innerWidth, int continueWidth) {
            mockStepWidths(mockedStep1, mockInnerView1, mockContinueButton1,
                    decoratorWidth, innerUsedSpace, innerWidth, continueWidth);
        }

        void mockStepWidths(Step step, View innerView, AppCompatButton continueButton,
                            int decoratorWidth, int innerUsedSpace, int innerWidth, int continueWidth) {
            when(step.calculateStepDecoratorWidth()).thenReturn(decoratorWidth);
            when(step.calculateInnerViewHorizontalUsedSpace()).thenReturn(innerUsedSpace);
            when(innerView.getMeasuredWidth()).thenReturn(innerWidth);
            when(continueButton.getMeasuredWidth()).thenReturn(continueWidth);
        }

        void mockStepHeights(int decoratorHeight, int childrenVisibleHeight, int bottomMarginHeight, Step step) {
            when(step.getDecoratorHeight()).thenReturn(decoratorHeight);
            when(step.getChildrenVisibleHeight()).thenReturn(childrenVisibleHeight);
            when(step.getBottomMarginHeight()).thenReturn(bottomMarginHeight);
        }

        void assertExpectedStep1MeasureSpecs(int maxWidth, int maxHeight,
                                             int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep1MeasureSpecs(), mockedStep1, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        List<Integer> captureStep1MeasureSpecs() {
            return captureStepMeasureSpecs(mockInnerView1, mockContinueButton1);
        }

        List<Integer> captureStepMeasureSpecs(View innerView, View continueButton) {
            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(innerView).measure(captor.capture(), captor.capture());
            verify(continueButton).measure(captor.capture(), captor.capture());
            return captor.getAllValues();
        }

        void assertExpectedStepMeasureSpecs(List<Integer> measureSpecs, Step step,
                                            int maxWidth, int maxHeight,
                                            int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            int innerWms = measureSpecs.get(0);
            assertExpectedWidthMeasureSpec(step, maxWidth, innerWms);
            int innerHms = measureSpecs.get(1);
            assertExpectedHeightMeasureSpec(maxHeight, innerHms, additionalInnerUsedSpace);

            int continueWms = measureSpecs.get(2);
            assertExpectedWidthMeasureSpec(step, maxWidth, continueWms);
            int continueHms = measureSpecs.get(3);
            assertExpectedHeightMeasureSpec(maxHeight, continueHms, additionalContinueUsedSpace);
        }

        void assertExpectedHeightMeasureSpec(int maxHeight, int heightMeasureSpec,
                                             int additionalUsedSpace) {
            int verticalUsedSpace =
                    stepper.calculateVerticalPadding() + additionalUsedSpace;
            assertThat(View.MeasureSpec.getSize(heightMeasureSpec))
                    .isEqualTo(maxHeight - verticalUsedSpace);
        }

        void assertExpectedWidthMeasureSpec(Step step,
                                            int maxWidth, int widthMeasureSpec) {
            int horizontalUsedSpace =
                    step.calculateInnerViewHorizontalUsedSpace() + stepper.calculateHorizontalPadding();
            assertThat(View.MeasureSpec.getSize(widthMeasureSpec))
                    .isEqualTo(maxWidth - horizontalUsedSpace);
        }

        void measureChildViews(int maxWidth, int maxHeight) {
            int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
            int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
            stepper.measureChildViews(wms, hms);
        }

        void mockActiveState(boolean isActive) {
            when(mockedStep1.isActive()).thenReturn(isActive);
            int visibility = isActive ? View.VISIBLE : View.GONE;
            when(mockContinueButton1.getVisibility()).thenReturn(visibility);
            when(mockInnerView1.getVisibility()).thenReturn(visibility);
        }

        void assertActiveState(boolean expectedActiveState) {
            verify(mockedStep1).setActive(expectedActiveState);
            int visibility = expectedActiveState ? View.VISIBLE : View.GONE;
            verify(mockInnerView1).setVisibility(visibility);
            verify(mockContinueButton1).setVisibility(visibility);
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
            stepper.initStep(mockedStep1);

            verify(mockInnerView1).setVisibility(View.GONE);
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
        public void measureBottomMarginHeights_ShouldNotMeasureBottomMarginToNextStep() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1, never()).measureBottomMarginToNextStep();
        }

        @Test
        public void calculateWidth_ShouldReturnHorizontalPaddingAndStepWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = decoratorWidth * 4;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueWidth);

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
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(decoratorWidth);
        }

        @Test
        public void calculateMaxStepWidth_InnerViewHasMaxWidth_ShouldReturnInnerViewWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = decoratorWidth * 4;
            int continueWidth = 0;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(innerWidth + innerUsedSpace);
        }

        @Test
        public void calculateMaxStepWidth_NavButtonsHaveMaxWidth_ShouldReturnNavButtonsWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int innerWidth = 0;
            int continueWidth = decoratorWidth * 4;
            mockStep1Widths(decoratorWidth, innerUsedSpace, innerWidth, continueWidth);

            int maxWidth = stepper.calculateMaxStepWidth();

            assertThat(maxWidth)
                    .isEqualTo(continueWidth + innerUsedSpace);
        }

        @Test
        public void calculateHeight_ShouldReturnVerticalPaddingPlusTotalStepHeight() {
            int decoratorHeight = 100;
            int childrenVisibleHeight = 400;
            int bottomMarginHeight = 48;
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep1);

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
            stepper.toggleStepExpandedState(mockedStep1);

            assertActiveState(false);
        }

        @Test
        public void measureChildViews_ShouldHaveChildrenVisibleHeightsWithActualHeight() {
            final int innerViewHeight = 100;
            final int buttonHeight = 50;
            when(mockInnerView1.getMeasuredHeight()).thenReturn(innerViewHeight);
            when(mockContinueButton1.getMeasuredHeight()).thenReturn(buttonHeight);

            int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            stepper.measureChildViews(ms, ms);

            verify(mockedStep1).setChildrenVisibleHeight(innerViewHeight + buttonHeight);
        }

        @Test
        public void measureChildViews_ShouldMeasureNavButtonsAccountingForInnerView() {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);
            int innerHeight = 200;
            when(mockInnerView1.getMeasuredHeight()).thenReturn(innerHeight);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    stepper.steps.get(0).calculateInnerViewVerticalUsedSpace(), innerHeight);
        }
    }

    public static class GivenExactlyOneInactiveStep extends GivenOneStep {

        @Before
        public void givenExactlyOneInactiveStep() {
            mockActiveState(false);
        }

        @Test
        public void toggleStepExpandedState_ShouldBecomeActiveAndExpanded() {
            stepper.toggleStepExpandedState(mockedStep1);

            assertActiveState(true);
        }

        @Test
        public void measureChildViews_NoMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);
            int innerVerticalUSedSpace = 20;
            when(mockedStep1.calculateInnerViewVerticalUsedSpace()).thenReturn(innerVerticalUSedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUSedSpace, 0);
        }

        @Test
        public void measureChildViews_HasMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
            int horizontalMargin = 10;
            int verticalMargin = 20;
            VerticalStepper.LayoutParams lp =
                    RobolectricTestUtils.createTestLayoutParams(activity,
                            horizontalMargin / 2, verticalMargin / 2, horizontalMargin / 2, verticalMargin / 2);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);
            int innerVerticalUSedSpace = 20;
            when(mockedStep1.calculateInnerViewVerticalUsedSpace()).thenReturn(innerVerticalUSedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight, innerVerticalUSedSpace, 0);
        }

        @Test
        public void measureChildViews_ShouldMeasureChildrenAccountingForDecorator() {
            int decoratorHeight = 100;
            when(mockedStep1.getDecoratorHeight()).thenReturn(decoratorHeight);
            VerticalStepper.LayoutParams lp = RobolectricTestUtils.createTestLayoutParams(activity);
            when(mockInnerView1.getLayoutParams()).thenReturn(lp);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    stepper.steps.get(0).calculateInnerViewVerticalUsedSpace() + decoratorHeight, decoratorHeight);
        }
    }

    public static abstract class GivenTwoSteps extends GivenOneStep {
        View mockInnerView2;
        VerticalStepper.InternalTouchView mockTouchView2;
        AppCompatButton mockContinueButton2;
        VerticalStepper.LayoutParams mockLayoutParams2;
        Step mockedStep2;

        @Before
        public void givenTwoSteps() {
            mockInnerView2 = mock(View.class);
            mockLayoutParams2 = mock(VerticalStepper.LayoutParams.class);
            when(mockInnerView2.getLayoutParams()).thenReturn(mockLayoutParams2);
            mockContinueButton2 = mock(AppCompatButton.class);
            mockTouchView2 = mock(VerticalStepper.InternalTouchView.class);
            mockedStep2 = mock(Step.class);
            when(mockedStep2.getInnerView()).thenReturn(mockInnerView2);
            when(mockedStep2.getTouchView()).thenReturn(mockTouchView2);
            when(mockedStep2.getContinueButton()).thenReturn(mockContinueButton2);

            stepper.initStep(mockedStep2);

            clearInvocations(mockInnerView2);
            clearInvocations(mockLayoutParams2);
            clearInvocations(mockContinueButton2);
            clearInvocations(mockTouchView2);
            clearInvocations(mockedStep2);
        }

        void assertExpectedStep2MeasureSpecs(int maxWidth, int maxHeight,
                                             int additionalInnerUsedSpace, int additionalContinueUsedSpace) {
            assertExpectedStepMeasureSpecs(captureStep2MeasureSpecs(), mockedStep2, maxWidth, maxHeight,
                    additionalInnerUsedSpace, additionalContinueUsedSpace);
        }

        List<Integer> captureStep2MeasureSpecs() {
            return captureStepMeasureSpecs(mockInnerView2, mockContinueButton2);
        }

        void mockStep2Widths(int decoratorWidth, int innerUsedSpace, int innerWidth, int continueWidth) {
            mockStepWidths(mockedStep2, mockInnerView2, mockContinueButton2,
                    decoratorWidth, innerUsedSpace, innerWidth, continueWidth);
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

            verify(mockedStep1).measureStepDecoratorHeight();
            verify(mockedStep2).measureStepDecoratorHeight();
        }

        @Test
        public void measureBottomMarginHeights_ShouldMeasureBottomMarginToNextStepOnce() {
            stepper.measureStepBottomMarginHeights();

            verify(mockedStep1).measureBottomMarginToNextStep();
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

            int innerVerticalUSedSpace = 20;
            when(mockedStep1.calculateInnerViewVerticalUsedSpace()).thenReturn(innerVerticalUSedSpace);
            when(mockedStep2.calculateInnerViewVerticalUsedSpace()).thenReturn(innerVerticalUSedSpace);

            int maxWidth = 1080;
            int maxHeight = 1920;
            measureChildViews(maxWidth, maxHeight);

            assertExpectedStep1MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUSedSpace + decoratorHeight, decoratorHeight);

            assertExpectedStep2MeasureSpecs(maxWidth, maxHeight,
                    innerVerticalUSedSpace + decoratorHeight * 2 + bottomMargin,
                    decoratorHeight * 2 + bottomMargin);
        }

        @Test
        public void calculateMaxStepWidth_ShouldReturnLargerStepWidth() {
            int decoratorWidth = 20;
            int innerUsedSpace = 20;
            int continueWidth = 0;

            int inner1Width = decoratorWidth * 2;
            mockStep1Widths(decoratorWidth, innerUsedSpace, inner1Width, continueWidth);

            int inner2Width = decoratorWidth * 3;
            mockStep2Widths(decoratorWidth, innerUsedSpace, inner2Width, continueWidth);

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
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep1);
            mockStepHeights(decoratorHeight, childrenVisibleHeight, bottomMarginHeight, mockedStep2);

            int width = stepper.calculateHeight();

            assertThat(width)
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
            verify(mockTouchView1).measure(wmsCaptor.capture(), hmsCaptor.capture());
            verify(mockTouchView2).measure(wmsCaptor.capture(), hmsCaptor.capture());

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
