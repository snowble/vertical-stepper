package com.snowble.android.verticalstepper;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class StepViewTest {
    private VerticalStepper.StepView stepView;

    private static final TextPaint TITLE_ACTIVE_PAINT = new TextPaint();
    private static final TextPaint TITLE_INACTIVE_PAINT = new TextPaint();
    private static final Paint ICON_ACTIVE_PAINT = new TextPaint();
    private static final Paint ICON_INACTIVE_PAINT = new TextPaint();
    private static final int ACTIVE_BOTTOM_MARGIN = 48;
    private static final int INACTIVE_BOTTOM_MARGIN = 40;

    @Before
    public void before() {
        VerticalStepper.StepView.setTitleActiveTextPaint(TITLE_ACTIVE_PAINT);
        VerticalStepper.StepView.setTitleInactiveTextPaint(TITLE_INACTIVE_PAINT);
        VerticalStepper.StepView.setIconActiveBackgroundPaint(ICON_ACTIVE_PAINT);
        VerticalStepper.StepView.setIconInactiveBackgroundPaint(ICON_INACTIVE_PAINT);
        VerticalStepper.StepView.setActiveBottomMarginToNextStep(ACTIVE_BOTTOM_MARGIN);
        VerticalStepper.StepView.setInactiveBottomMarginToNextStep(INACTIVE_BOTTOM_MARGIN);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        Activity activity = activityController.create().get();
        View innerView = mock(View.class);
        when(innerView.getLayoutParams()).thenReturn(RobolectricTestUtils.createTestLayoutParams(activity));

        stepView = new VerticalStepper.StepView(innerView,
                new VerticalStepper.InternalTouchView(activity), new AppCompatButton(activity));
    }

    @Test
    public void getIconColor_InactiveStep_ShouldReturnInactiveStepPaint() {
        stepView.setActive(false);

        Paint paint = stepView.getIconColor();

        assertThat(paint).isSameAs(ICON_INACTIVE_PAINT);
    }

    @Test
    public void getIconColor_ActiveStep_ShouldReturnInactiveStepPaint() {
        stepView.setActive(true);

        Paint paint = stepView.getIconColor();

        assertThat(paint).isSameAs(ICON_ACTIVE_PAINT);
    }

    @Test
    public void getTitleTextPaint_InactiveStep_ShouldReturnInactiveStepPaint() {
        stepView.setActive(false);

        TextPaint paint = stepView.getTitleTextPaint();

        assertThat(paint).isSameAs(TITLE_INACTIVE_PAINT);
    }

    @Test
    public void getTitleTextPaint_ActiveStep_ShouldReturnActiveStepPaint() {
        stepView.setActive(true);

        TextPaint paint = stepView.getTitleTextPaint();

        assertThat(paint).isSameAs(TITLE_ACTIVE_PAINT);
    }

    @Test
    public void getBottomMarginToNextStep_LastStep_ShouldReturnZeroSizedMargin() {
        stepView.setActive(false);

        int margin = stepView.getBottomMarginToNextStep(true);

        assertThat(margin).isEqualTo(VerticalStepper.StepView.ZERO_SIZE_MARGIN);
    }

    @Test
    public void getBottomMarginToNextStep_NotLastStepInactive_ShouldReturnInactiveMargin() {
        stepView.setActive(false);

        int margin = stepView.getBottomMarginToNextStep(false);

        assertThat(margin).isEqualTo(INACTIVE_BOTTOM_MARGIN);
    }

    @Test
    public void getBottomMarginToNextStep_NotLastStepActive_ShouldReturnActiveMargin() {
        stepView.setActive(true);

        int margin = stepView.getBottomMarginToNextStep(false);

        assertThat(margin).isEqualTo(ACTIVE_BOTTOM_MARGIN);
    }
}