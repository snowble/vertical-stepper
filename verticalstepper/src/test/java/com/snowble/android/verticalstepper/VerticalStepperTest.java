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
    public void getStepDecoratorWidth_ShouldReturnIconAndTextSum() {
        float textWidth = 10f;
        when(mockLayoutParams.getTitleWidth()).thenReturn(textWidth);
        when(mockLayoutParams.getSummaryWidth()).thenReturn(textWidth);

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
        when(mockLayoutParams.getTitleWidth()).thenReturn(20f);
        when(mockLayoutParams.getSummaryWidth()).thenReturn(10f);

        float width = stepper.getStepDecoratorTextWidth(mockLayoutParams);

        assertThat(width).isEqualTo(20f);
    }

    @Test
    public void getStepDecoratorTextWidth_TallerSummary_ShouldReturnSummary() {
        when(mockLayoutParams.getTitleWidth()).thenReturn(20f);
        when(mockLayoutParams.getSummaryWidth()).thenReturn(25f);

        float width = stepper.getStepDecoratorTextWidth(mockLayoutParams);

        assertThat(width).isEqualTo(25f);
    }

    @Test
    public void getStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
        float lessThanHalfIconHeight = (stepper.iconDimension - 2) / 2;
        when(mockLayoutParams.getTitleBottomRelativeToStepTop()).thenReturn(lessThanHalfIconHeight);
        when(mockLayoutParams.getSummaryBottomRelativeToTitleBottom())
                .thenReturn(lessThanHalfIconHeight);

        int height = stepper.getStepDecoratorHeight(mockLayoutParams);

        assertThat(height).isEqualTo(stepper.iconDimension);
    }

    @Test
    public void getStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
        float twiceIconHeight = stepper.iconDimension * 2;
        when(mockLayoutParams.getTitleBottomRelativeToStepTop()).thenReturn(twiceIconHeight);
        when(mockLayoutParams.getSummaryBottomRelativeToTitleBottom()).thenReturn(twiceIconHeight);

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
}
