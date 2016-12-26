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
        testStepToggle(false, View.GONE, true, View.VISIBLE);
    }

    @Test
    public void toggleStepExpandedState_Active_ShouldBecomeInactiveAndCollapsed() {
        testStepToggle(true, View.VISIBLE, false, View.GONE);
    }

    private void testStepToggle(boolean initialActivateState, int initialVisibility,
                                boolean finalExpectedActiveState, int finalExpectedVisibility) {
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.active = initialActivateState;
        when(lp.continueButton.getVisibility()).thenReturn(initialVisibility);

        View innerView = mock(View.class);
        when(innerView.getVisibility()).thenReturn(initialVisibility);
        when(innerView.getLayoutParams()).thenReturn(lp);

        stepper.toggleStepExpandedState(innerView);

        assertThat(lp.active).isEqualTo(finalExpectedActiveState);
        verify(innerView).setVisibility(finalExpectedVisibility);
        verify(lp.continueButton).setVisibility(finalExpectedVisibility);
    }

    private VerticalStepper.LayoutParams createTestLayoutParams() {
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
