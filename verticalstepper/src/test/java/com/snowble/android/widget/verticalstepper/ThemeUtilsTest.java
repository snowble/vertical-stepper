package com.snowble.android.widget.verticalstepper;

import android.app.Activity;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Java6Assertions.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class ThemeUtilsTest {

    private Activity activity;

    @Before
    public void setUp() {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.create().get();
    }

    @Test
    public void getResolvedAttributeData_MissingAttr_ShouldReturnDefault() {
        int defaultData = 2;

        int data = ThemeUtils.getResolvedAttributeData(activity.getTheme(), R.attr.colorPrimary, defaultData);

        assertThat(data).isEqualTo(defaultData);
    }

    @Test
    public void getResolvedAttributeData_HasAttr_ShouldNotReturnDefault() {
        int defaultData = 2;
        activity.setTheme(android.R.style.Theme);

        int data = ThemeUtils.getResolvedAttributeData(
                activity.getTheme(), android.R.attr.listPreferredItemHeight, defaultData);

        assertThat(data)
                .isNotEqualTo(defaultData);
    }
}
