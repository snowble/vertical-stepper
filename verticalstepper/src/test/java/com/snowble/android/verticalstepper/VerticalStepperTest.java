package com.snowble.android.verticalstepper;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        VerticalStepper.LayoutParams lp = mockLayoutParams();
        lp.active = false;
        when(lp.continueButton.getVisibility()).thenReturn(View.GONE);

        View innerView = mock(View.class);
        when(innerView.getVisibility()).thenReturn(View.GONE);
        when(innerView.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(innerView);

        assertThat(lp.active).isTrue();
        verify(innerView).setVisibility(View.VISIBLE);
        verify(lp.continueButton).setVisibility(View.VISIBLE);
    }

    @Test
    public void toggleStepExpandedState_Active_ShouldBecomeInactiveAndCollapsed() {
        VerticalStepper.LayoutParams lp = mockLayoutParams();
        lp.active = true;
        when(lp.continueButton.getVisibility()).thenReturn(View.VISIBLE);

        View innerView = mock(View.class);
        when(innerView.getVisibility()).thenReturn(View.VISIBLE);
        when(innerView.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(innerView);

        assertThat(lp.active).isFalse();
        verify(innerView).setVisibility(View.GONE);
        verify(lp.continueButton).setVisibility(View.GONE);
    }

    private VerticalStepper.LayoutParams mockLayoutParams() {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "wrap_content");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");

        attributeSetBuilder.addAttribute(R.attr.step_title, "title");
        VerticalStepper.LayoutParams lp = new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());
        lp.continueButton = mock(AppCompatButton.class);
        return lp;
    }

    private static class DummyActivity extends Activity {
    }
}
