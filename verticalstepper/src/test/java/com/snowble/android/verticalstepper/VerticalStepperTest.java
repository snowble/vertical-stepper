package com.snowble.android.verticalstepper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class VerticalStepperTest {

    private Activity activity;
    private VerticalStepper stepper;
    private View mockInnerView;
    private VerticalStepper.InternalTouchView mockTouchView;
    private AppCompatButton mockContinueButton;
    private VerticalStepper.LayoutParams mockLayoutParams;

    @Before
    public void before() {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.create().get();
        stepper = new VerticalStepper(activity);

        mockInnerView = mock(View.class);
        mockLayoutParams = mock(VerticalStepper.LayoutParams.class);
        when(mockInnerView.getLayoutParams()).thenReturn(mockLayoutParams);
        mockContinueButton = mock(AppCompatButton.class);
        when(mockLayoutParams.getContinueButton()).thenReturn(mockContinueButton);
        mockTouchView = mock(VerticalStepper.InternalTouchView.class);
        when(mockLayoutParams.getTouchView()).thenReturn(mockTouchView);
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
    public void initInnerView_ShouldSetVisibilityToGone() {
        stepper.initInnerView(mockInnerView);

        verify(mockInnerView).setVisibility(View.GONE);
    }

    @Test
    public void initInnerView_ShouldInitializeStepViews() {
        stepper.initInnerView(mockInnerView);

        verify(mockLayoutParams).setTouchView((VerticalStepper.InternalTouchView) notNull());
        verify(mockLayoutParams).setContinueButton((AppCompatButton) notNull());
    }

    @Test
    public void initTouchView_ShouldSetClickListener() {
        stepper.initTouchView(mockInnerView);

        verify(mockTouchView).setOnClickListener((View.OnClickListener) notNull());
    }

    @Test
    public void initTouchView_ShouldAttachToStepper() {
        stepper.initTouchView(mockInnerView);

        assertThat(stepper.getChildCount()).isEqualTo(1);
    }

    @Test
    public void initNavButtons_ShouldSetVisibilityToGone() {
        stepper.initNavButtons(mockInnerView);

        verify(mockContinueButton).setVisibility(View.GONE);
    }

    @Test
    public void initNavButtons_ShouldSetClickListener() {
        stepper.initNavButtons(mockInnerView);

        verify(mockContinueButton).setOnClickListener((View.OnClickListener) notNull());
    }

    @Test
    public void initNavButtons_ShouldAttachToStepper() {
        stepper.initNavButtons(mockInnerView);

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

        when(mockInnerView.getVisibility()).thenReturn(initialVisibility);
        when(mockInnerView.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(mockInnerView);

        assertThat(lp.isActive()).isEqualTo(finalExpectedActiveState);
        verify(mockInnerView).setVisibility(finalExpectedVisibility);
        verify(lp.getContinueButton()).setVisibility(finalExpectedVisibility);
    }

    private VerticalStepper.LayoutParams createTestLayoutParams() {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "wrap_content");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");
        attributeSetBuilder.addAttribute(R.attr.step_title, "title");
        VerticalStepper.LayoutParams lp =
                new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());
        lp.setContinueButton(mock(AppCompatButton.class));
        return lp;
    }

    @Test
    public void getHorizontalPadding_ShouldReturnAllPadding() {
        int horizontalPadding = stepper.getHorizontalPadding();

        assertThat(horizontalPadding)
                .isEqualTo((stepper.outerHorizontalPadding * 2) +
                        stepper.getPaddingLeft() + stepper.getPaddingRight());
    }

    @Test
    public void getVerticalPadding_ShouldReturnAllPadding() {
        int verticalPadding = stepper.getVerticalPadding();

        assertThat(verticalPadding)
                .isEqualTo((stepper.outerVerticalPadding * 2) +
                        stepper.getPaddingTop() + stepper.getPaddingBottom());
    }
    @Test
    public void getInnerViewHorizontalPadding_ShouldReturnPaddingAndIconLeftAdjustment() {
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.leftMargin = 20;
        lp.rightMargin = 10;

        int horizontalPadding = stepper.getInnerViewHorizontalPadding(lp);

        assertThat(horizontalPadding)
                .isEqualTo(lp.leftMargin + lp.rightMargin + stepper.iconDimension + stepper.iconMarginRight);
    }

    @Test
    public void getInnerViewVerticalPadding_ShouldReturnAllMargins() {
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.topMargin = 10;
        lp.bottomMargin = 20;

        int verticalPadding = stepper.getInnerViewVerticalPadding(lp);

        assertThat(verticalPadding).isEqualTo(lp.topMargin + lp.bottomMargin);
    }

    // In production code, we would call measure() instead of onMeasure() but that's not what we're testing here.
    // This applies to all onMeasure() tests
    @SuppressLint("WrongCall")
    @Test
    public void onMeasure_NoStepsUnspecifiedSpecs_ShouldMeasurePadding() {
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.onMeasure(ms, ms);

        assertThat(stepper.getMeasuredHeight()).isEqualTo(stepper.getVerticalPadding());
        assertThat(stepper.getMeasuredWidth()).isEqualTo(stepper.getHorizontalPadding());
    }

    @SuppressLint("WrongCall")
    @Test
    public void onMeasure_NoStepsAtMostSpecsRequiresClipping_ShouldMeasureToAtMostValues() {
        int width = stepper.getHorizontalPadding() / 2;
        int height = stepper.getVerticalPadding() / 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);

        stepper.onMeasure(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @SuppressLint("WrongCall")
    @Test
    public void onMeasure_NoStepsExactlySpecsRequiresClipping_ShouldMeasureToExactValues() {
        int width = stepper.getHorizontalPadding() / 2;
        int height = stepper.getVerticalPadding() / 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        stepper.onMeasure(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @SuppressLint("WrongCall")
    @Test
    public void onMeasure_NoStepsExactlySpecsRequiresExpanding_ShouldMeasureToExactValues() {
        int width = stepper.getHorizontalPadding() * 2;
        int height = stepper.getVerticalPadding() * 2;
        int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hms = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        stepper.onMeasure(wms, hms);

        assertThat(stepper.getMeasuredWidth()).isEqualTo(width);
        assertThat(stepper.getMeasuredHeight()).isEqualTo(height);
    }

    @Test
    public void onMeasure_OneStepUnspecifiedSpec_ShouldMeasurePaddingStepDecorationsAndInnerView() {
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

        stepper.addView(mockInnerView);
        stepper.initChildViews();

        testSingleChildInactiveMeasurement(innerViewMeasuredWidth, titleBottom, summaryBottom);
        testSingleChildActiveMeasurement(innerViewMeasuredWidth, innerViewMeasuredHeight,
                titleBottom, summaryBottom,
                buttonMeasuredHeight);
    }

    @SuppressLint("WrongCall")
    private void testSingleChildInactiveMeasurement(int innerViewMeasuredWidth,
                                                    float titleBottom, float summaryBottom) {
        when(mockLayoutParams.isActive()).thenReturn(false);
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.onMeasure(ms, ms);

        assertThat(stepper.getMeasuredWidth())
                .isEqualTo(stepper.getHorizontalPadding() + stepper.getInnerViewHorizontalPadding(mockLayoutParams)
                + innerViewMeasuredWidth);
        assertThat(stepper.getMeasuredHeight())
                .isEqualTo(stepper.getVerticalPadding() + stepper.getInnerViewVerticalPadding(mockLayoutParams)
                + (int) (titleBottom + summaryBottom));
    }

    @SuppressLint("WrongCall")
    private void testSingleChildActiveMeasurement(int innerViewMeasuredWidth, int innerViewMeasuredHeight,
                                                  float titleBottom, float summaryBottom,
                                                  int buttonMeasuredHeight) {
        when(mockLayoutParams.isActive()).thenReturn(true);
        int ms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        stepper.onMeasure(ms, ms);

        assertThat(stepper.getMeasuredWidth())
                .isEqualTo(stepper.getHorizontalPadding() + stepper.getInnerViewHorizontalPadding(mockLayoutParams)
                        + innerViewMeasuredWidth);
        assertThat(stepper.getMeasuredHeight())
                .isEqualTo(stepper.getVerticalPadding() + stepper.getInnerViewVerticalPadding(mockLayoutParams)
                        + (int) (titleBottom + summaryBottom) + innerViewMeasuredHeight + buttonMeasuredHeight);
    }

    @Test
    public void getStepDecoratorWidth_ShouldReturnIconAndTextSum() {
        float textWidth = 10f;
        mockLayoutParamsWidths(textWidth, textWidth);

        int iconWidth = stepper.iconDimension + stepper.iconMarginRight;

        assertThat(stepper.getStepDecoratorWidth(mockLayoutParams))
                .isEqualTo(iconWidth + (int) textWidth);
    }

    @Test
    public void getStepDecoratorIconWidth_ShouldReturnIconWidthAndMarginSum() {
        int iconWidth = stepper.getStepDecoratorIconWidth();

        assertThat(iconWidth)
                .isEqualTo(stepper.iconDimension + stepper.iconMarginRight);
    }

    @Test
    public void getStepDecoratorTextWidth_TallerTitle_ShouldReturnTitle() {
        mockLayoutParamsWidths(20f, 10f);

        float width = stepper.getStepDecoratorTextWidth(mockLayoutParams);

        assertThat(width).isEqualTo(20f);
    }

    @Test
    public void getStepDecoratorTextWidth_TallerSummary_ShouldReturnSummary() {
        mockLayoutParamsWidths(20f, 25f);

        float width = stepper.getStepDecoratorTextWidth(mockLayoutParams);

        assertThat(width).isEqualTo(25f);
    }

    @Test
    public void getStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
        float lessThanHalfIconHeight = (stepper.iconDimension - 2) / 2;
        mockLayoutParamsHeights(lessThanHalfIconHeight, lessThanHalfIconHeight);

        int height = stepper.getStepDecoratorHeight(mockLayoutParams);

        assertThat(height).isEqualTo(stepper.iconDimension);
    }

    @Test
    public void getStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
        float twiceIconHeight = stepper.iconDimension * 2;
        mockLayoutParamsHeights(twiceIconHeight, twiceIconHeight);

        int height = stepper.getStepDecoratorHeight(mockLayoutParams);

        assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
    }

    @Test
    public void measureTouchView_ShouldMeasureWidthAndHeightExactly() {
        ArgumentCaptor<Integer> wmsCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> hmsCaptor = ArgumentCaptor.forClass(Integer.class);
        int width = 20;

        stepper.measureTouchView(width, mockTouchView);

        verify(mockTouchView).measure(wmsCaptor.capture(), hmsCaptor.capture());

        int actualWms = wmsCaptor.getValue();
        assertThat(View.MeasureSpec.getMode(actualWms)).isEqualTo(View.MeasureSpec.EXACTLY);
        assertThat(View.MeasureSpec.getSize(actualWms)).isEqualTo(width);

        int actualHms = hmsCaptor.getValue();
        assertThat(View.MeasureSpec.getMode(actualHms)).isEqualTo(View.MeasureSpec.EXACTLY);
        assertThat(View.MeasureSpec.getSize(actualHms)).isEqualTo(stepper.touchViewHeight);
    }

    private void mockInnerViewMeasurements(int measuredWidth, int measuredHeight) {
        when(mockInnerView.getMeasuredWidth()).thenReturn(measuredWidth);
        when(mockInnerView.getMeasuredHeight()).thenReturn(measuredHeight);
    }

    private void mockContinueButtonMeasurements(int measuredWidth, int measuredHeight) {
        when(mockContinueButton.getMeasuredWidth()).thenReturn(measuredWidth);
        when(mockContinueButton.getMeasuredHeight()).thenReturn(measuredHeight);
    }

    private void mockLayoutParamsWidths(float titleWidth, float summaryWidth) {
        when(mockLayoutParams.getTitleWidth()).thenReturn(titleWidth);
        when(mockLayoutParams.getSummaryWidth()).thenReturn(summaryWidth);
    }

    private void mockLayoutParamsHeights(float titleBottom, float summaryBottom) {
        when(mockLayoutParams.getTitleBottomRelativeToStepTop()).thenReturn(titleBottom);
        when(mockLayoutParams.getSummaryBottomRelativeToTitleBottom()).thenReturn(summaryBottom);
    }
}
