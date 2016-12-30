package com.snowble.android.verticalstepper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class VerticalStepperTest {

    private Activity activity;
    private VerticalStepper stepper;
    private View mockInnerView1;
    private View mockInnerView2;
    private VerticalStepper.InternalTouchView mockTouchView1;
    private VerticalStepper.InternalTouchView mockTouchView2;
    private AppCompatButton mockContinueButton1;
    private AppCompatButton mockContinueButton2;
    private VerticalStepper.LayoutParams mockLayoutParams1;
    private VerticalStepper.LayoutParams mockLayoutParams2;

    @Before
    public void before() {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.create().get();
        stepper = new VerticalStepper(activity);

        mockInnerView1 = mock(View.class);
        mockLayoutParams1 = mock(VerticalStepper.LayoutParams.class);
        when(mockInnerView1.getLayoutParams()).thenReturn(mockLayoutParams1);
        mockContinueButton1 = mock(AppCompatButton.class);
        when(mockLayoutParams1.getContinueButton()).thenReturn(mockContinueButton1);
        mockTouchView1 = mock(VerticalStepper.InternalTouchView.class);
        when(mockLayoutParams1.getTouchView()).thenReturn(mockTouchView1);

        mockInnerView2 = mock(View.class);
        mockLayoutParams2 = mock(VerticalStepper.LayoutParams.class);
        when(mockInnerView2.getLayoutParams()).thenReturn(mockLayoutParams2);
        mockContinueButton2 = mock(AppCompatButton.class);
        when(mockLayoutParams2.getContinueButton()).thenReturn(mockContinueButton2);
        mockTouchView2 = mock(VerticalStepper.InternalTouchView.class);
        when(mockLayoutParams2.getTouchView()).thenReturn(mockTouchView2);
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
    public void initChildViews_NoSteps_ShouldHaveEmptyInnerViews() {
        stepper.initChildViews();

        assertThat(stepper.innerViews).isEmpty();
    }

    @Test
    public void initChildViews_OneStep_ShouldHaveInnerViewsWithSingleElement() {
        initOneStep();

        assertThat(stepper.innerViews).hasSize(1);
    }

    @Test
    public void initChildViews_TwoSteps_ShouldHaveInnerViewsWithTwoElements() {
        initTwoSteps();

        assertThat(stepper.innerViews).hasSize(2);
    }

    @Test
    public void initInnerView_ShouldSetVisibilityToGone() {
        stepper.initInnerView(mockInnerView1);

        verify(mockInnerView1).setVisibility(View.GONE);
    }

    @Test
    public void initInnerView_ShouldInitializeStepViews() {
        stepper.initInnerView(mockInnerView1);

        verify(mockLayoutParams1).setTouchView((VerticalStepper.InternalTouchView) notNull());
        verify(mockLayoutParams1).setContinueButton((AppCompatButton) notNull());
    }

    @Test
    public void initTouchView_ShouldSetClickListener() {
        stepper.initTouchView(mockInnerView1);

        verify(mockTouchView1).setOnClickListener((View.OnClickListener) notNull());
    }

    @Test
    public void initTouchView_ShouldAttachToStepper() {
        stepper.initTouchView(mockInnerView1);

        assertThat(stepper.getChildCount()).isEqualTo(1);
    }

    @Test
    public void initNavButtons_ShouldSetVisibilityToGone() {
        stepper.initNavButtons(mockInnerView1);

        verify(mockContinueButton1).setVisibility(View.GONE);
    }

    @Test
    public void initNavButtons_ShouldSetClickListener() {
        stepper.initNavButtons(mockInnerView1);

        verify(mockContinueButton1).setOnClickListener((View.OnClickListener) notNull());
    }

    @Test
    public void initNavButtons_ShouldAttachToStepper() {
        stepper.initNavButtons(mockInnerView1);

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
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.setActive(initialActivateState);
        when(lp.getContinueButton().getVisibility()).thenReturn(initialVisibility);

        when(mockInnerView1.getVisibility()).thenReturn(initialVisibility);
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(mockInnerView1);

        assertThat(lp.isActive()).isEqualTo(finalExpectedActiveState);
        verify(mockInnerView1).setVisibility(finalExpectedVisibility);
        verify(lp.getContinueButton()).setVisibility(finalExpectedVisibility);
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
        VerticalStepper.LayoutParams lp = createTestLayoutParams(20, 0, 10, 0);

        int horizontalPadding = stepper.calculateInnerViewHorizontalUsedSpace(lp);

        assertThat(horizontalPadding)
                .isEqualTo(lp.leftMargin + lp.rightMargin + stepper.iconDimension + stepper.iconMarginRight);
    }

    @Test
    public void calculateInnerViewVerticalUsedSpace_ShouldReturnAllMargins() {
        VerticalStepper.LayoutParams lp = createTestLayoutParams(0, 10, 0, 20);

        int verticalPadding = stepper.calculateInnerViewVerticalUsedSpace(lp);

        assertThat(verticalPadding).isEqualTo(lp.topMargin + lp.bottomMargin);
    }

    @Test
    public void measureStepDecoratorHeights_NoSteps_ShouldHaveEmptyDecoratorHeights() {
        stepper.measureStepDecoratorHeights();

        assertThat(stepper.decoratorHeights).isEmpty();
    }

    @Test
    public void measureStepDecoratorHeights_OneStep_ShouldHaveDecoratorHeightsWithSingleElement() {
        initOneStep();

        stepper.measureStepDecoratorHeights();

        assertThat(stepper.decoratorHeights).hasSize(1);
    }

    @Test
    public void measureStepDecoratorHeights_TwoSteps_ShouldHaveDecoratorHeightsWithTwoElements() {
        initTwoSteps();

        stepper.measureStepDecoratorHeights();

        assertThat(stepper.decoratorHeights).hasSize(2);
    }

    @Test
    public void measureBottomMarginHeights_NoSteps_ShouldHaveEmptyMarginHeights() {
        stepper.measureStepBottomMarginHeights();

        assertThat(stepper.bottomMarginHeights).isEmpty();
    }

    @Test
    public void measureBottomMarginHeights_OneStep_ShouldHaveMarginHeightsWithSingleElement() {
        initOneStep();

        stepper.measureStepBottomMarginHeights();

        assertThat(stepper.bottomMarginHeights).hasSize(1);
    }

    @Test
    public void measureBottomMarginHeights_TwoSteps_ShouldHaveMarginHeightsWithTwoElements() {
        initTwoSteps();

        stepper.measureStepBottomMarginHeights();

        assertThat(stepper.bottomMarginHeights).hasSize(2);
    }

    @Test
    public void measureChildViews_NoSteps_ShouldHaveEmptyChildrenVisibleHeights() {
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        assertThat(stepper.childrenVisibleHeights).isEmpty();
    }

    @Test
    public void measureChildViews_OneStep_ShouldHaveChildrenVisibleHeightsWithSingleElement() {
        initOneStep();
        initStepperStateForChildMeasurement(1);

        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        assertThat(stepper.childrenVisibleHeights).hasSize(1);
    }

    @Test
    public void measureChildViews_TwoSteps_ShouldHaveChildrenVisibleHeightsWithTwoElements() {
        initTwoSteps();
        initStepperStateForChildMeasurement(2);

        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        assertThat(stepper.childrenVisibleHeights).hasSize(2);
    }

    @Test
    public void measureChildViews_NoActiveSteps_ShouldMeasureViews() {
        initTwoSteps();
        initStepperStateForChildMeasurement(2);

        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        verify(mockInnerView1).measure(anyInt(), anyInt());
        verify(mockInnerView2).measure(anyInt(), anyInt());
        verify(mockContinueButton1).measure(anyInt(), anyInt());
        verify(mockContinueButton1).measure(anyInt(), anyInt());
    }

    @Test
    public void measureChildViews_NoActiveSteps_ShouldHaveChildrenVisibleHeightsWithZeros() {
        initTwoSteps();
        initStepperStateForChildMeasurement(2);
        when(mockInnerView1.getMeasuredHeight()).thenReturn(100);
        when(mockInnerView2.getMeasuredHeight()).thenReturn(100);

        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        assertThat(stepper.childrenVisibleHeights).containsExactly(0, 0);
    }

    @Test
    public void measureChildViews_OneActiveStep_ShouldHaveChildrenVisibleHeightsWithActualHeight() {
        initOneStep();
        initStepperStateForChildMeasurement(1);
        int innerViewHeight = 100;
        int buttonHeight = 50;
        when(mockInnerView1.getMeasuredHeight()).thenReturn(innerViewHeight);
        when(mockContinueButton1.getMeasuredHeight()).thenReturn(buttonHeight);
        when(mockLayoutParams1.isActive()).thenReturn(true);

        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        stepper.measureChildViews(ms, ms);

        assertThat(stepper.childrenVisibleHeights).containsExactly(innerViewHeight + buttonHeight);
    }

    @Test
    public void measureChildViews_OneInactiveStepNoMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
        initOneStep();
        initStepperStateForChildMeasurement(1);
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);

        int maxWidth = 1080;
        int maxHeight = 1920;
        int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);

        stepper.measureChildViews(wms, hms);

        ArgumentCaptor<Integer> childSpecsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockInnerView1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        List<Integer> allValues = childSpecsCaptor.getAllValues();

        int innerWms = allValues.get(0);
        assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms);
        int innerHms = allValues.get(1);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms, stepper.calculateInnerViewVerticalUsedSpace(lp));

        int continueWms = allValues.get(2);
        assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms);
        int continueHms = allValues.get(3);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms, 0);
    }

    @Test
    public void measureChildViews_OneInactiveStepHasMargins_ShouldMeasureChildrenAccountingForUsedSpace() {
        initOneStep();
        initStepperStateForChildMeasurement(1);
        int horizontalMargin = 10;
        int verticalMargin = 20;
        VerticalStepper.LayoutParams lp = createTestLayoutParams(horizontalMargin / 2, verticalMargin / 2,
                horizontalMargin / 2, verticalMargin / 2);
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);

        int maxWidth = 1080;
        int maxHeight = 1920;
        int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);

        stepper.measureChildViews(wms, hms);

        ArgumentCaptor<Integer> childSpecsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockInnerView1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        List<Integer> allValues = childSpecsCaptor.getAllValues();

        int innerWms = allValues.get(0);
        assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms);
        int innerHms = allValues.get(1);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms, stepper.calculateInnerViewVerticalUsedSpace(lp));

        int continueWms = allValues.get(2);
        assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms);
        int continueHms = allValues.get(3);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms, 0);
    }

    @Test
    public void measureChildViews_OneActiveStep_ShouldMeasureNavButtonsAccountingForInnerView() {
        initOneStep();
        initStepperStateForChildMeasurement(1);
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.setActive(true);
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);
        int innerHeight = 200;
        when(mockInnerView1.getMeasuredHeight()).thenReturn(innerHeight);

        int maxWidth = 1080;
        int maxHeight = 1920;
        int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);

        stepper.measureChildViews(wms, hms);

        ArgumentCaptor<Integer> childSpecsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockInnerView1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        List<Integer> allValues = childSpecsCaptor.getAllValues();

        int innerWms = allValues.get(0);
        assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms);
        int innerHms = allValues.get(1);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms, stepper.calculateInnerViewVerticalUsedSpace(lp));

        int continueWms = allValues.get(2);
        assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms);
        int continueHms = allValues.get(3);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms, innerHeight);
    }

    @Test
    public void measureChildViews_OneStep_ShouldMeasureChildrenAccountingForDecorator() {
        initOneStep();
        int decoratorHeight = 100;
        int bottomMargin = 30;
        initStepperStateForChildMeasurement(Collections.singletonList(decoratorHeight),
                Collections.singletonList(bottomMargin));
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);

        int maxWidth = 1080;
        int maxHeight = 1920;
        int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);

        stepper.measureChildViews(wms, hms);

        ArgumentCaptor<Integer> childSpecsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockInnerView1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        List<Integer> allValues = childSpecsCaptor.getAllValues();

        int innerWms = allValues.get(0);
        assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms);
        int innerHms = allValues.get(1);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms,
                stepper.calculateInnerViewVerticalUsedSpace(lp) + decoratorHeight);

        int continueWms = allValues.get(2);
        assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms);
        int continueHms = allValues.get(3);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms, decoratorHeight);
    }

    @Test
    public void measureChildViews_TwoSteps_ShouldMeasureChildrenAccountingForBottomMargin() {
        initTwoSteps();
        int decoratorHeight = 100;
        int bottomMargin = 30;
        initStepperStateForChildMeasurement(Arrays.asList(decoratorHeight, decoratorHeight),
                Arrays.asList(bottomMargin, 0));

        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        when(mockInnerView1.getLayoutParams()).thenReturn(lp);
        VerticalStepper.LayoutParams lp2 = createTestLayoutParams();
        lp2.setContinueButton(mockContinueButton2);
        when(mockInnerView2.getLayoutParams()).thenReturn(lp2);

        int maxWidth = 1080;
        int maxHeight = 1920;
        int wms = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);

        stepper.measureChildViews(wms, hms);

        ArgumentCaptor<Integer> childSpecsCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockInnerView1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton1).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockInnerView2).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        verify(mockContinueButton2).measure(childSpecsCaptor.capture(), childSpecsCaptor.capture());
        List<Integer> allValues = childSpecsCaptor.getAllValues();

        Integer innerWms1 = allValues.get(0);
        assertExpectedWidthMeasureSpec(lp, maxWidth, innerWms1);
        Integer innerHms1 = allValues.get(1);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms1,
                stepper.calculateInnerViewVerticalUsedSpace(lp) + decoratorHeight);

        Integer continueWms1 = allValues.get(2);
        assertExpectedWidthMeasureSpec(lp, maxWidth, continueWms1);
        Integer continueHms1 = allValues.get(3);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms1, decoratorHeight);

        Integer innerWms2 = allValues.get(4);
        assertExpectedWidthMeasureSpec(lp2, maxWidth, innerWms2);
        Integer innerHms2 = allValues.get(5);
        assertExpectedHeightMeasureSpec(maxHeight, innerHms2,
                stepper.calculateInnerViewVerticalUsedSpace(lp2) + decoratorHeight * 2 + bottomMargin);

        Integer continueWms2 = allValues.get(6);
        assertExpectedWidthMeasureSpec(lp2, maxWidth, continueWms2);
        Integer continueHms2 = allValues.get(7);
        assertExpectedHeightMeasureSpec(maxHeight, continueHms2, decoratorHeight * 2 + bottomMargin);
    }

    private void assertExpectedHeightMeasureSpec(int maxHeight, int heightMeasureSpec,
                                                 int additionalUsedSpace) {
        int verticalUsedSpace =
                stepper.calculateVerticalPadding() + additionalUsedSpace;
        assertThat(View.MeasureSpec.getSize(heightMeasureSpec))
                .isEqualTo(maxHeight - verticalUsedSpace);
    }

    private void assertExpectedWidthMeasureSpec(VerticalStepper.LayoutParams lp, int maxWidth, int widthMeasureSpec) {
        int horizontalUsedSpace =
                stepper.calculateInnerViewHorizontalUsedSpace(lp) + stepper.calculateHorizontalPadding();
        assertThat(View.MeasureSpec.getSize(widthMeasureSpec))
                .isEqualTo(maxWidth - horizontalUsedSpace);
    }

    @Test
    public void calculateWidth_NoSteps_ShouldReturnHorizontalPadding() {
        int width = stepper.calculateWidth();

        assertThat(width)
                .isEqualTo(stepper.calculateHorizontalPadding());
    }

    @Test
    public void calculateWidth_OneStep_ShouldReturnHorizontalPaddingAndStepWidth() {
        initOneStep();
        int innerWidth = stepper.calculateStepDecoratorWidth(mockLayoutParams1) * 2;
        when(mockInnerView1.getMeasuredWidth()).thenReturn(innerWidth);
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

        int width = stepper.calculateWidth();

        assertThat(width)
                .isEqualTo(stepper.calculateHorizontalPadding()
                        + innerWidth + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
    }

    @Test
    public void calculateMaxStepWidth_DecoratorsHaveMaxWidth_ShouldReturnDecoratorsWidth() {
        initOneStep();
        when(mockInnerView1.getMeasuredWidth()).thenReturn(0);
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

        int maxWidth = stepper.calculateMaxStepWidth();

        assertThat(maxWidth)
                .isEqualTo(stepper.calculateStepDecoratorWidth(mockLayoutParams1));
    }

    @Test
    public void calculateMaxStepWidth_InnerViewHasMaxWidth_ShouldReturnInnerViewWidth() {
        initOneStep();
        int width = stepper.calculateStepDecoratorWidth(mockLayoutParams1) * 2;
        when(mockInnerView1.getMeasuredWidth()).thenReturn(width);
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

        int maxWidth = stepper.calculateMaxStepWidth();

        assertThat(maxWidth)
                .isEqualTo(width + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
    }

    @Test
    public void calculateMaxStepWidth_NavButtonsHaveMaxWidth_ShouldReturnNavButtonsWidth() {
        initOneStep();
        int width = stepper.calculateStepDecoratorWidth(mockLayoutParams1) * 2;
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(width);
        when(mockInnerView1.getMeasuredWidth()).thenReturn(0);

        int maxWidth = stepper.calculateMaxStepWidth();

        assertThat(maxWidth)
                .isEqualTo(width + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1));
    }

    @Test
    public void calculateMaxStepWidth_TwoSteps_ShouldReturnLargerStepWidth() {
        initTwoSteps();
        int width1 = stepper.calculateStepDecoratorWidth(mockLayoutParams1) * 2;
        when(mockInnerView1.getMeasuredWidth()).thenReturn(width1);
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(0);

        int width2 = stepper.calculateStepDecoratorWidth(mockLayoutParams2) * 3;
        when(mockInnerView2.getMeasuredWidth()).thenReturn(width2);
        when(mockContinueButton2.getMeasuredWidth()).thenReturn(0);

        int maxWidth = stepper.calculateMaxStepWidth();

        assertThat(maxWidth)
                .isNotEqualTo(width1 + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1))
                .isEqualTo(width2 + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams2));
    }

    @Test
    public void doMeasurement_NoStepsUnspecifiedSpecs_ShouldMeasurePadding() {
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.doMeasurement(ms, ms);

        assertThat(stepper.getMeasuredHeight()).isEqualTo(stepper.calculateVerticalPadding());
        assertThat(stepper.getMeasuredWidth()).isEqualTo(stepper.calculateHorizontalPadding());
    }

    @Test
    public void doMeasurement_NoStepsAtMostSpecsRequiresClipping_ShouldMeasureToAtMostValues() {
        int width = stepper.calculateHorizontalPadding() / 2;
        int height = stepper.calculateVerticalPadding() / 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);

        stepper.doMeasurement(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @Test
    public void doMeasurement_NoStepsExactlySpecsRequiresClipping_ShouldMeasureToExactValues() {
        int width = stepper.calculateHorizontalPadding() / 2;
        int height = stepper.calculateVerticalPadding() / 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        stepper.doMeasurement(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @Test
    public void doMeasurement_NoStepsExactlySpecsRequiresExpanding_ShouldMeasureToExactValues() {
        int width = stepper.calculateHorizontalPadding() * 2;
        int height = stepper.calculateVerticalPadding() * 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        stepper.doMeasurement(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @Test
    public void doMeasurement_OneStepUnspecifiedSpec_ShouldMeasurePaddingStepDecorationsAndInnerView() {
        int innerViewMeasuredWidth = 100;
        int innerViewMeasuredHeight = 400;
        mockInnerViewMeasurements(innerViewMeasuredWidth, innerViewMeasuredHeight);

        int buttonMeasuredWidth = 80;
        int buttonMeasuredHeight = 20;
        mockContinueButtonMeasurements(buttonMeasuredWidth, buttonMeasuredHeight);

        float titleWidth = 10f;
        float summaryWidth = 20f;
        mockLayoutParamsWidths(titleWidth, summaryWidth);

        float titleBottom = 24f;
        float summaryBottom = 20f;
        mockLayoutParamsHeights(titleBottom, summaryBottom);

        initOneStep();

        testSingleChildInactiveMeasurement(innerViewMeasuredWidth, titleBottom, summaryBottom);
        testSingleChildActiveMeasurement(innerViewMeasuredWidth, innerViewMeasuredHeight,
                titleBottom, summaryBottom,
                buttonMeasuredHeight);
    }

    private void testSingleChildInactiveMeasurement(int innerViewMeasuredWidth,
                                                    float titleBottom, float summaryBottom) {
        when(mockLayoutParams1.isActive()).thenReturn(false);
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.doMeasurement(ms, ms);

        assertThat(stepper.getMeasuredWidth())
                .isEqualTo(stepper.calculateHorizontalPadding() + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1)
                + innerViewMeasuredWidth);
        assertThat(stepper.getMeasuredHeight())
                .isEqualTo(stepper.calculateVerticalPadding() + stepper.calculateInnerViewVerticalUsedSpace(mockLayoutParams1)
                + (int) (titleBottom + summaryBottom));
    }

    private void testSingleChildActiveMeasurement(int innerViewMeasuredWidth, int innerViewMeasuredHeight,
                                                  float titleBottom, float summaryBottom,
                                                  int buttonMeasuredHeight) {
        when(mockLayoutParams1.isActive()).thenReturn(true);
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.doMeasurement(ms, ms);

        assertThat(stepper.getMeasuredWidth())
                .isEqualTo(stepper.calculateHorizontalPadding() + stepper.calculateInnerViewHorizontalUsedSpace(mockLayoutParams1)
                        + innerViewMeasuredWidth);
        assertThat(stepper.getMeasuredHeight())
                .isEqualTo(stepper.calculateVerticalPadding() + stepper.calculateInnerViewVerticalUsedSpace(mockLayoutParams1)
                        + (int) (titleBottom + summaryBottom) + innerViewMeasuredHeight + buttonMeasuredHeight);
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
    public void calculateStepDecoratorWidth_ShouldReturnIconAndTextSum() {
        float textWidth = 10f;
        mockLayoutParamsWidths(textWidth, textWidth);

        int iconWidth = stepper.iconDimension + stepper.iconMarginRight;

        assertThat(stepper.calculateStepDecoratorWidth(mockLayoutParams1))
                .isEqualTo(iconWidth + (int) textWidth);
    }

    @Test
    public void calculateStepDecoratorIconWidth_ShouldReturnIconWidthAndMarginSum() {
        int iconWidth = stepper.calculateStepDecoratorIconWidth();

        assertThat(iconWidth)
                .isEqualTo(stepper.iconDimension + stepper.iconMarginRight);
    }

    @Test
    public void calculateStepDecoratorTextWidth_TallerTitle_ShouldReturnTitle() {
        mockLayoutParamsWidths(20f, 10f);

        float width = stepper.calculateStepDecoratorTextWidth(mockLayoutParams1);

        assertThat(width).isEqualTo(20f);
    }

    @Test
    public void calculateStepDecoratorTextWidth_TallerSummary_ShouldReturnSummary() {
        mockLayoutParamsWidths(20f, 25f);

        float width = stepper.calculateStepDecoratorTextWidth(mockLayoutParams1);

        assertThat(width).isEqualTo(25f);
    }

    @Test
    public void calculateStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
        float lessThanHalfIconHeight = (stepper.iconDimension - 2) / 2;
        mockLayoutParamsHeights(lessThanHalfIconHeight, lessThanHalfIconHeight);

        int height = stepper.calculateStepDecoratorHeight(mockLayoutParams1);

        assertThat(height).isEqualTo(stepper.iconDimension);
    }

    @Test
    public void calculateStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
        float twiceIconHeight = stepper.iconDimension * 2;
        mockLayoutParamsHeights(twiceIconHeight, twiceIconHeight);

        int height = stepper.calculateStepDecoratorHeight(mockLayoutParams1);

        assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
    }

    @Test
    public void getTitleTextPaint_InactiveStep_ShouldReturnInactiveStepPaint() {
        when(mockLayoutParams1.isActive()).thenReturn(false);
        TextPaint paint = stepper.getTitleTextPaint(mockLayoutParams1);

        assertThat(paint).isEqualTo(stepper.titleInactiveTextPaint);
    }

    @Test
    public void getTitleTextPaint_ActiveStep_ShouldReturnActiveStepPaint() {
        when(mockLayoutParams1.isActive()).thenReturn(true);
        TextPaint paint = stepper.getTitleTextPaint(mockLayoutParams1);

        assertThat(paint).isEqualTo(stepper.titleActiveTextPaint);
    }

    @Test
    public void getBottomMarginToNextStep_LastStep_ShouldReturnZeroSizedMargin() {
        int margin = stepper.getBottomMarginToNextStep(mockLayoutParams1, true);

        assertThat(margin).isEqualTo(VerticalStepper.ZERO_SIZE_MARGIN);
    }

    @Test
    public void getBottomMarginToNextStep_NotLastStepInactive_ShouldReturnInactiveMargin() {
        when(mockLayoutParams1.isActive()).thenReturn(false);
        int margin = stepper.getBottomMarginToNextStep(mockLayoutParams1, false);

        assertThat(margin).isEqualTo(stepper.inactiveBottomMarginToNextStep);
    }

    @Test
    public void getBottomMarginToNextStep_NotLastStepActive_ShouldReturnActiveMargin() {
        when(mockLayoutParams1.isActive()).thenReturn(true);
        int margin = stepper.getBottomMarginToNextStep(mockLayoutParams1, false);

        assertThat(margin).isEqualTo(stepper.activeBottomMarginToNextStep);
    }

    private void initOneStep() {
        stepper.addView(mockInnerView1);
        stepper.initChildViews();
    }

    private void initTwoSteps() {
        stepper.addView(mockInnerView1);
        stepper.addView(mockInnerView2);
        stepper.initChildViews();
    }

    private void initStepperStateForChildMeasurement(int size) {
        List<Integer> dummyHeights = Collections.nCopies(size, 0);
        initStepperStateForChildMeasurement(dummyHeights, dummyHeights);
    }

    private void initStepperStateForChildMeasurement(List<Integer> decoratorHeights,
                                                     List<Integer> bottomMarginHeights) {
        stepper.decoratorHeights.addAll(decoratorHeights);
        stepper.bottomMarginHeights.addAll(bottomMarginHeights);
    }

    private void mockInnerViewMeasurements(int measuredWidth, int measuredHeight) {
        when(mockInnerView1.getMeasuredWidth()).thenReturn(measuredWidth);
        when(mockInnerView1.getMeasuredHeight()).thenReturn(measuredHeight);
    }

    private void mockContinueButtonMeasurements(int measuredWidth, int measuredHeight) {
        when(mockContinueButton1.getMeasuredWidth()).thenReturn(measuredWidth);
        when(mockContinueButton1.getMeasuredHeight()).thenReturn(measuredHeight);
    }

    private void mockLayoutParamsWidths(float titleWidth, float summaryWidth) {
        when(mockLayoutParams1.getTitleWidth()).thenReturn(titleWidth);
        when(mockLayoutParams1.getSummaryWidth()).thenReturn(summaryWidth);
    }

    private void mockLayoutParamsHeights(float titleBottom, float summaryBottom) {
        when(mockLayoutParams1.getTitleBottomRelativeToStepTop()).thenReturn(titleBottom);
        when(mockLayoutParams1.getSummaryBottomRelativeToTitleBottom()).thenReturn(summaryBottom);
    }

    private VerticalStepper.LayoutParams createTestLayoutParams(int leftMargin, int topMargin,
                                                                int rightMargin, int bottomMargin) {
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.leftMargin = leftMargin;
        lp.topMargin = topMargin;
        lp.rightMargin = rightMargin;
        lp.bottomMargin = bottomMargin;

        return lp;
    }

    private VerticalStepper.LayoutParams createTestLayoutParams() {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "wrap_content");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");
        attributeSetBuilder.addAttribute(R.attr.step_title, "title");
        VerticalStepper.LayoutParams lp =
                new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());
        lp.setContinueButton(mockContinueButton1);
        lp.width = VerticalStepper.LayoutParams.MATCH_PARENT;
        lp.height = VerticalStepper.LayoutParams.WRAP_CONTENT;
        return lp;
    }

}
