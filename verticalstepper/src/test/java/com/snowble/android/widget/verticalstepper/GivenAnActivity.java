package com.snowble.android.widget.verticalstepper;

import android.app.Activity;
import android.os.Build;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public abstract class GivenAnActivity {
    Activity activity;

    @Before
    public void givenAnActivity() {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.create().get();
    }

    VerticalStepper.LayoutParams createTestLayoutParams( int leftMargin, int topMargin,
                                                         int rightMargin, int bottomMargin) {
        VerticalStepper.LayoutParams lp = createTestLayoutParams();
        lp.leftMargin = leftMargin;
        lp.topMargin = topMargin;
        lp.rightMargin = rightMargin;
        lp.bottomMargin = bottomMargin;

        return lp;
    }

    VerticalStepper.LayoutParams createTestLayoutParams() {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "match_parent");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");
        attributeSetBuilder.addAttribute(R.attr.step_title, "title");
        attributeSetBuilder.addAttribute(R.attr.step_summary, "summary");

        return new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());
    }
}
