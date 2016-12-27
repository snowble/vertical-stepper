package com.snowble.android.verticalstepper;

import android.app.Activity;
import android.os.Build;
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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class VerticalStepperTest {

    private Activity activity;
    private VerticalStepper stepper;

    @Before
    public void before() {
        ActivityController<DummyActivity> activityController = Robolectric.buildActivity(DummyActivity.class);
        activity = activityController.create().get();
        stepper = new VerticalStepper(activity);
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

        View innerView = mock(View.class);
        when(innerView.getVisibility()).thenReturn(initialVisibility);
        when(innerView.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(innerView);

        assertThat(lp.isActive()).isEqualTo(finalExpectedActiveState);
        verify(innerView).setVisibility(finalExpectedVisibility);
        verify(lp.getContinueButton()).setVisibility(finalExpectedVisibility);
    }

    private VerticalStepper.LayoutParams createTestLayoutParams() {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "wrap_content");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");
        attributeSetBuilder.addAttribute(R.attr.step_title, "title");
        VerticalStepper.LayoutParams lp = new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());
        lp.setContinueButton(mock(AppCompatButton.class));
        return lp;
    }

    @Test
    public void getStepDecoratorWidth_ShouldReturnIconAndTextSum() {
        VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
        float textWidth = 10f;
        when(lp.getTitleWidth()).thenReturn(textWidth);
        when(lp.getSummaryWidth()).thenReturn(textWidth);

        int iconWidth = stepper.iconDimension + stepper.iconMarginRight;

        assertThat(stepper.getStepDecoratorWidth(lp)).isEqualTo(iconWidth + (int) textWidth);
    }

    @Test
    public void getStepDecoratorIconWidth_ShouldReturnIconWidthAndMarginSum() {
        assertThat(stepper.getStepDecoratorIconWidth())
                .isEqualTo(stepper.iconDimension + stepper.iconMarginRight);
    }

    @Test
    public void getStepDecoratorTextWidth_TallerTitle_ShouldReturnTitle() {
        VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
        when(lp.getTitleWidth()).thenReturn(20f);
        when(lp.getSummaryWidth()).thenReturn(10f);

        float width = stepper.getStepDecoratorTextWidth(lp);
        assertThat(width).isEqualTo(20f);
    }

    @Test
    public void getStepDecoratorTextWidth_TallerSummary_ShouldReturnSummary() {
        VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
        when(lp.getTitleWidth()).thenReturn(20f);
        when(lp.getSummaryWidth()).thenReturn(25f);

        float width = stepper.getStepDecoratorTextWidth(lp);
        assertThat(width).isEqualTo(25f);
    }

    @Test
    public void getStepDecoratorHeight_TallerIcon_ShouldReturnIconHeight() {
        VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
        float lessThanHalfIconHeight = (stepper.iconDimension - 2) / 2;
        when(lp.getTitleBottomRelativeToStepTop()).thenReturn(lessThanHalfIconHeight);
        when(lp.getSummaryBottomRelativeToTitleBottom()).thenReturn(lessThanHalfIconHeight);

        int height = stepper.getStepDecoratorHeight(lp);
        assertThat(height).isEqualTo(stepper.iconDimension);
    }

    @Test
    public void getStepDecoratorHeight_TallerText_ShouldReturnTextHeight() {
        VerticalStepper.LayoutParams lp = mock(VerticalStepper.LayoutParams.class);
        float twiceIconHeight = stepper.iconDimension * 2;
        when(lp.getTitleBottomRelativeToStepTop()).thenReturn(twiceIconHeight);
        when(lp.getSummaryBottomRelativeToTitleBottom()).thenReturn(twiceIconHeight);

        int height = stepper.getStepDecoratorHeight(lp);
        assertThat(height).isEqualTo((int) (twiceIconHeight + twiceIconHeight));
    }

    @Test
    public void measureTouchView_ShouldMeasureWidthAndHeightExactly() {
        VerticalStepper.InternalTouchView touchView = mock(VerticalStepper.InternalTouchView.class);
        int width = 20;

        ArgumentCaptor<Integer> wmsCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> hmsCaptor = ArgumentCaptor.forClass(Integer.class);
        stepper.measureTouchView(width, touchView);
        verify(touchView).measure(wmsCaptor.capture(), hmsCaptor.capture());

        int actualWms = wmsCaptor.getValue();
        assertThat(View.MeasureSpec.getMode(actualWms)).isEqualTo(View.MeasureSpec.EXACTLY);
        assertThat(View.MeasureSpec.getSize(actualWms)).isEqualTo(width);

        int actualHms = hmsCaptor.getValue();
        assertThat(View.MeasureSpec.getMode(actualHms)).isEqualTo(View.MeasureSpec.EXACTLY);
        assertThat(View.MeasureSpec.getSize(actualHms)).isEqualTo(stepper.touchViewHeight);
    }

    private static class DummyActivity extends Activity {
    }
}
